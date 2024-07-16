package org.bigDataFactory.sparkSystem;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.janusgraph.core.JanusGraphTransaction;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SparkConverter {

    SparkSession spark;

    private static SparkConverter converter;
    JanusGraphClient client;
    GraphTraversalSource g;
    JanusGraphTransaction tx;

    private SparkConverter() {
        spark = SparkSession.builder().master("local").getOrCreate();
        client = JanusGraphClient.getInstance();
        g = client.getG();
        tx = client.getGraph().newTransaction();
    }

    public static synchronized SparkConverter getInstance() {
        if(converter == null) {
            converter = new SparkConverter();
        }
        return converter;
    }

    public void closeSpark() {
        spark.stop();
    }

    public void fetchDataCsv() {
        Dataset<Row> df = spark.read().option("header",true).csv("src/main/resources/Movies/credits.csv");

        StructType schemaCast = new StructType()
                .add("cast_id", DataTypes.IntegerType, false)
                .add("character", DataTypes.StringType, true)
                .add("gender", DataTypes.IntegerType, true)
                .add("credit_id", DataTypes.StringType, true)
                .add("name", DataTypes.StringType, true)
                .add("profile_path", DataTypes.StringType, true)
                .add("id", DataTypes.IntegerType, true)
                .add("order", DataTypes.IntegerType, true)
                .add("movie_id", DataTypes.StringType);

        StructType schemaCrew = new StructType()
                .add("gender", DataTypes.IntegerType)
                .add("credit_id", DataTypes.StringType)
                .add("name", DataTypes.StringType)
                .add("profile_path", DataTypes.StringType)
                .add("id", DataTypes.IntegerType)
                .add("department", DataTypes.StringType)
                .add("job", DataTypes.StringType)
                .add("movie_id", DataTypes.StringType);


        Dataset<Row> dfCast = createDataset(df, "cast", schemaCast);
        Dataset<Row> dfCrew = createDataset(df, "crew", schemaCrew);

        addMovieToGraph(df.select("id").collectAsList());
        addCollegues(dfCast, dfCrew, df.select("id").collectAsList());
    }


    private Row createRowfromFactory(@NotNull JSONObject object, String id, String columnName) {
        Iterator<String> it = object.keys();
        List<Object> valueOfRow = new ArrayList<>();
        while (it.hasNext())
            valueOfRow.add(object.get(it.next()));
        valueOfRow.add(id);
        return RowFactory.create(valueOfRow.toArray());
    }


    private Dataset<Row> createDataset(Dataset<Row> df, String columnName, StructType schema) {
        int movieIdx = 0;
        List<Row> listOfData = df.select(columnName).collectAsList();
        List<Row> movie_ids = df.select("id").collectAsList();
        ArrayList<Row> moviesDataList = new ArrayList<>();
        for(Row cast : listOfData) {
            String movie_id = movie_ids.get(movieIdx).getString(0);
            JSONArray jsonArray = new JSONArray(cast.getString(0));
            for (int i = 0; i < jsonArray.length(); i++)
                moviesDataList.add(createRowfromFactory(jsonArray.getJSONObject(i), movie_id, columnName));
            movieIdx++;
        }
        return spark.createDataFrame(moviesDataList, schema);
    }

    private void addMovieToGraph(List<Row> movie_ids) {
        for(Row i : movie_ids)
            tx.addVertex(T.label, "movie", "id", i.getString(0));
        tx.commit();

    }

    private void addCollegues(Dataset<Row> cast, Dataset<Row> crew, List<Row> movie_ids) {
        GraphTraversal<Vertex, Object> movies = g.V().hasLabel("movie").id();
        for(Row i : movie_ids) {
            ArrayList<Vertex> colleagues = new ArrayList<>();
            String movieId = i.getString(0);
            System.out.println(movies.next());
            Vertex movie = tx.getVertex(movies.next());
            List<Row> castInfo = cast.where("movie_id = " + movieId).collectAsList();
            for(Row j : castInfo) {
                Vertex v = tx.addVertex(T.label, "cast", "name", j.getString(4), "gender", j.getInt(2),
                        "id", j.getInt(6), "profile_path", j.getInt(5));

                v.addEdge("acted", movie, "cast_id", j.getString(0),
                        "character", j.getString(1), "credit_id", j.getString(3), "order", j.getString(7));
                for(Vertex k : colleagues)
                    v.addEdge("worked_together", k, "movie_id", movieId);
                colleagues.add(v);
            }

            List<Row> crewInfo = crew.where("movie_id = " + movieId).collectAsList();
            for(Row j : crewInfo) {
                Vertex v = tx.addVertex(T.label, "crew", "name", j.getString(2), "gender", j.getInt(0),
                        "id", j.getInt(4), "profile_path", j.getInt(3));

                v.addEdge("worked_on", movie,
                        "credit_id", j.getString(0), "department", j.getString(5), "job", j.getString(6));
                for(Vertex k : colleagues)
                    v.addEdge("worked_together", k, "movie_id", movieId);
                colleagues.add(v);
            }

            Vertex director = tx.getVertex(g.V().hasLabel("cast").has("job", "Director"));
            for(Vertex k : colleagues)
                director.addEdge("directed", k, "movie_id", movieId);
        }
    }
}
