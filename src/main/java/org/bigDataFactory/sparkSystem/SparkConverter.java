package org.bigDataFactory.sparkSystem;

import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.SizeEstimator;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.bigDataFactory.janusSystem.JanusGraphProducer;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.JanusGraphVertex;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SparkConverter {

    SparkSession spark;

    private static SparkConverter converter;

    private SparkConverter() {
        spark = SparkSession.builder().master("local").getOrCreate();
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

    private void loadVertexesToJanus(Dataset<Row> df, List<Object> ids) {
        df.select("id","name","gender","profile_path").dropDuplicates("id")
                .repartition(10).foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Vertex Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = JanusGraphClient.getGraph().newTransaction();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    if (!ids.contains(info.getInt(0))) {
                        JanusGraphVertex v = tx.addVertex("cast");
                        v.property("id", info.getInt(0));
                        v.property("name", info.getString(1));
                        v.property("gender", info.getInt(2));
                        v.property("profile_path", info.getString(3));
                        ids.add(info.getInt(0));
                    }
                }
                tx.commit();
                client.closeConnection();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });
        ids.clear();
    }

    public void fetchDataCsv() throws Exception {
        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/Movies/credits.csv");

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


        Dataset<Row> dfCast = createDataset(df, "cast", schemaCast).distinct();
        Dataset<Row> dfCrew = createDataset(df, "crew", schemaCrew).distinct();

        System.out.println("dfCast SIZE");
        System.out.println(SizeEstimator.estimate(dfCast.select("id","movie_id", "cast_id", "character", "credit_id", "order")));

        //dfCast.show();
        //System.out.println("CAST SAYISI");
        //System.out.println(dfCast.count());
        //System.out.println("CREDIT ID SAYISI");
        //System.out.println(dfCast.dropDuplicates("credit_id").count());
        //dfCrew.show();


        long startTime = System.currentTimeMillis();

        new JanusGraphProducer().createSchema();

        JanusGraphClient client2 = new JanusGraphClient();
        List<Object> cast_ids = JanusGraphClient.getG().V().hasLabel("cast").values("id").toList();
        List<Object> movie_ids = JanusGraphClient.getG().V().hasLabel("movie").values("movie_id").toList();
        List<Object> crew_ids = JanusGraphClient.getG().V().hasLabel("crew").values("id").toList();
        client2.closeConnection();

        System.out.println(Runtime.getRuntime().freeMemory());

        df.select("id").distinct().foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Movie Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = JanusGraphClient.getGraph().newTransaction();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    if (!movie_ids.contains(info.getString(0))) {
                        JanusGraphVertex v = tx.addVertex("movie");
                        v.property("movie_id", info.getString(0));
                        movie_ids.add(info.getString(0));
                    }
                }
                tx.commit();
                client.closeConnection();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });

        movie_ids.clear();

        loadVertexesToJanus(dfCast, cast_ids);
        loadVertexesToJanus(dfCrew, crew_ids);

        dfCast.select("id","movie_id", "cast_id", "character", "credit_id", "order")
                .repartition(5)
                .foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Cast Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = JanusGraphClient.getGraph().newTransaction();
                GraphTraversalSource g = JanusGraphClient.getG();
                System.out.println(SizeEstimator.estimate(iterator));
                System.out.println(SizeEstimator.estimate(iterator) / Runtime.getRuntime().freeMemory());

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    Vertex v = g.V().hasLabel("cast").has("id", info.getInt(0)).next();

                    if(v != null && g.E().hasLabel("acted").has("credit_id", info.getString(4)).hasNext())
                    {
                        v.addEdge("acted", tx.getVertex(info.getString(1)), "cast_id", info.getString(2),
                                "character", info.getString(3), "credit_id", info.getString(4),
                                "order", info.getString(5));
                    }
                }
                tx.commit();
                client.closeConnection();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });

        dfCrew.select("id","movie_id", "credit_id", "department", "job")
                .repartition(20)
                .foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Crew Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = JanusGraphClient.getGraph().newTransaction();
                GraphTraversalSource g = JanusGraphClient.getG();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    Vertex v = g.V().hasLabel("cast").has("id", info.getInt(0)).next();

                    if(v != null && g.E().hasLabel("worked").has("credit_id", info.getString(2)).hasNext())
                    {
                        v.addEdge("worked", tx.getVertex(info.getString(1)),
                                "credit_id", info.getString(2), "department",
                                info.getString(3), "job", info.getString(4));
                    }
                }
                tx.commit();
                client.closeConnection();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });



        System.out.println();
        System.out.println("TIME CHANGE");
        System.out.println(System.currentTimeMillis() - startTime);
        System.out.println();
        client2 = new JanusGraphClient();
        System.out.println("JANUS VERTEX COUNT");
        System.out.println(JanusGraphClient.getG().V().count().next());
        System.out.println("JANUS EDGE COUNT");
        System.out.println(JanusGraphClient.getG().E().count().next());
        client2.closeConnection();
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

    /*private void addCollegues(Dataset<Row> cast, Dataset<Row> crew, List<Row> movie_ids) {
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
    }*/
}
