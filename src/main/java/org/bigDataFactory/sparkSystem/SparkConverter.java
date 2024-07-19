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

    private void loadVertexesToJanus(Dataset<Row> df) {
        df = df.select("id","name","gender","profile_path").dropDuplicates("id");
        System.out.println("VERTEX PARTITION COUNT");
        System.out.println((int) SizeEstimator.estimate(df));
        System.out.println((int) SizeEstimator.estimate(df)/1000000 + 1);
        df.repartition((int) SizeEstimator.estimate(df)/1000000+ 1).foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Vertex Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = client.getGraph().newTransaction();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    JanusGraphVertex v = tx.addVertex("cast");
                    v.property("id", info.getInt(0));
                    v.property("name", info.getString(1));
                    v.property("gender", info.getInt(2));
                    v.property("profile_path", info.getString(3));
                }
                tx.commit();
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


        //dfCast.show();
        //System.out.println("CAST SAYISI");
        //System.out.println(dfCast.count());
        //System.out.println("CREDIT ID SAYISI");
        //System.out.println(dfCast.dropDuplicates("credit_id").count());
        //dfCrew.show();


        long startTime = System.currentTimeMillis();

        new JanusGraphProducer().createSchema();

        Dataset<Row> movieDataset = df.select("id").distinct();
        System.out.println("MOVIE PARTITION COUNT");
        System.out.println((int) SizeEstimator.estimate(movieDataset)/1000000 + 1);
        System.out.println(SizeEstimator.estimate(movieDataset));

        movieDataset.repartition((int) SizeEstimator.estimate(movieDataset)/1000000 + 1).foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Movie Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = client.getGraph().newTransaction();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    JanusGraphVertex v = tx.addVertex("movie");
                    v.property("movie_id", info.getString(0));
                }
                tx.commit();
                client.closeConnection();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });

        loadVertexesToJanus(dfCast);
        loadVertexesToJanus(dfCrew);

        Dataset<Row> castEdges = dfCast.select("id", "movie_id", "cast_id", "character", "credit_id", "order");
        System.out.println("CAST PARTITION COUNT");
        System.out.println((int) SizeEstimator.estimate(castEdges)/1000000 + 1);
        System.out.println(SizeEstimator.estimate(castEdges));

            castEdges.repartition((int) SizeEstimator.estimate(castEdges)/1000000 + 1)
                .foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Cast Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = client.getGraph().newTransaction();
                GraphTraversalSource g = client.getG();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    Vertex v = g.V().hasLabel("cast").has("id", info.getInt(0)).next();

                    if(v != null)
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

        Dataset<Row> crewEdges = dfCrew.select("id", "movie_id", "credit_id", "department", "job");
        System.out.println("CREW PARTITION COUNT");
        System.out.println((int) SizeEstimator.estimate(crewEdges)/1000000 + 1);
        System.out.println(SizeEstimator.estimate(crewEdges));

        crewEdges.repartition((int) SizeEstimator.estimate(crewEdges)/1000000 + 1)
                .foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            try {
                System.out.println("Came Crew Partition");
                JanusGraphClient client = new JanusGraphClient();
                JanusGraphTransaction tx = client.getGraph().newTransaction();
                GraphTraversalSource g = client.getG();

                while (iterator.hasNext()) {
                    Row info = iterator.next();
                    Vertex v = g.V().hasLabel("cast").has("id", info.getInt(0)).next();

                    if(v != null)
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
        JanusGraphClient client = new JanusGraphClient();
        System.out.println("JANUS VERTEX COUNT");
        System.out.println(client.getG().V().count().next());
        System.out.println("JANUS EDGE COUNT");
        System.out.println(client.getG().E().count().next());
        client.closeConnection();
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
