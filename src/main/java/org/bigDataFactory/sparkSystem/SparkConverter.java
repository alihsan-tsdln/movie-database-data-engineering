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
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.bigDataFactory.janusSystem.JanusGraphProducer;
import org.janusgraph.core.JanusGraph;
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
        spark.close();
    }

    private void loadVertexesToJanus(Dataset<Row> df, String labelName) {
        df = df.select("id","name","gender","profile_path").dropDuplicates("id");
        System.out.println("VERTEX PARTITION COUNT");
        System.out.println((int) SizeEstimator.estimate(df));
        System.out.println(Runtime.getRuntime().freeMemory());

        System.gc();
        int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(df) / Runtime.getRuntime().freeMemory() * 5);
        System.out.println(partitionNumber);
        df.repartition(partitionNumber).foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Vertex Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = client.getGraph().buildTransaction().enableBatchLoading().consistencyChecks(false).start();
                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    tx.addVertex(T.label, labelName,
                            T.id, labelName + "_" + info.getInt(0),
                            "id", info.getInt(0),
                            "name", info.getString(1),
                            "gender", info.getInt(2),
                            "profile_path", info.getString(3),
                            "vertex_type", labelName);
                }
                tx.commit();
                tx.close();
                client.closeConnection();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });
    }

    private void loadMoviesToJanus(Dataset<Row> df) {
        Dataset<Row> movieDataset = df.select("id").distinct();
        int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(df) / Runtime.getRuntime().freeMemory() * 5);
        System.out.println(partitionNumber);

        movieDataset.repartition(partitionNumber).foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            System.out.println("Came Movie Partition");
            JanusGraphClient client = new JanusGraphClient();
            JanusGraphTransaction tx = client.getGraph().buildTransaction().enableBatchLoading().consistencyChecks(false).start();

            while (iterator.hasNext()) {
                Row info = iterator.next();
                tx.addVertex(T.label, "movie", T.id, "movie_" + info.getString(0),
                        "movie_id", info.getString(0), "vertex_type", "movie");
            }
            tx.commit();
            tx.close();
            client.closeConnection();
        });
    }

    private void loadEdgesCastToJanus(Dataset<Row> dfCast) {
        Dataset<Row> castEdges = dfCast.select( "id","movie_id","cast_id", "character", "credit_id", "order");
        System.out.println("CAST PARTITION COUNT");
        System.out.println(SizeEstimator.estimate(castEdges));
        System.out.println(Runtime.getRuntime().freeMemory());
        int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(castEdges) / Runtime.getRuntime().freeMemory() * 5);

        System.out.println(partitionNumber);

        castEdges.repartition(partitionNumber)
            .foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
                try {
                    System.out.println("Came Cast Partition");
                    JanusGraphClient client = new JanusGraphClient();
                    GraphTraversalSource g = client.getG();
                    JanusGraph graph = client.getGraph();
                    while (iterator.hasNext()) {
                        Row info = iterator.next();
                        Vertex v = g.V("cast_" + info.getInt(0)).next();
                        Vertex movie = g.V("movie_" + info.getString(1)).next();
                        v.addEdge("acted", movie,
                                "cast_id", info.getInt(2), "character", info.getString(3),
                                "credit_id", info.getString(4), "order", info.getInt(5),
                                "edge_type", "acted");
                        movie.addEdge("acted", v,
                                "cast_id", info.getInt(2), "character", info.getString(3),
                                "credit_id", info.getString(4), "order", info.getInt(5),
                                "edge_type", "acted");
                    }
                    graph.tx().commit();
                    graph.tx().close();
                    client.closeConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    private void loadEdgesCrewToJanus(Dataset<Row> dfCrew) {
        Dataset<Row> crewEdges = dfCrew.select("id","movie_id","credit_id", "department", "job");
        System.out.println("CREW PARTITION COUNT");
        System.out.println(SizeEstimator.estimate(crewEdges));
        System.out.println(Runtime.getRuntime().freeMemory());
        int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(crewEdges) / Runtime.getRuntime().freeMemory() * 5);
        System.out.println(partitionNumber);

        crewEdges.repartition(partitionNumber)
            .foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
                try {
                    System.out.println("Came Crew Partition");
                    JanusGraphClient client = new JanusGraphClient();
                    GraphTraversalSource g = client.getG();
                    JanusGraph graph = client.getGraph();

                    while (iterator.hasNext()) {
                        Row info = iterator.next();
                        Vertex v = g.V("crew_" + info.getInt(0)).next();
                        Vertex movie = g.V("movie_"+info.getString(1)).next();
                        v.addEdge("worked", movie,
                                "credit_id", info.getString(2), "department", info.getString(3),
                                "job", info.getString(4),
                                "edge_type", "worked");
                        movie.addEdge("worked", v,
                                "credit_id", info.getString(2), "department", info.getString(3),
                                "job", info.getString(4),
                                "edge_type", "worked");

                    }
                    graph.tx().commit();
                    graph.tx().close();
                    client.closeConnection();
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            });
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

        new JanusGraphProducer().createSchema();
        loadMoviesToJanus(df);

        loadVertexesToJanus(dfCast, "cast");
        loadVertexesToJanus(dfCrew, "crew");

        loadEdgesCastToJanus(dfCast);
        loadEdgesCrewToJanus(dfCrew);
    }


    private Row createRowfromFactory(@NotNull JSONObject object, String id) {
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
                moviesDataList.add(createRowfromFactory(jsonArray.getJSONObject(i), movie_id));
            movieIdx++;
        }
        return spark.createDataFrame(moviesDataList, schema);
    }
}
