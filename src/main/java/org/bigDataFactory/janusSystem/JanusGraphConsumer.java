package org.bigDataFactory.janusSystem;

import org.apache.spark.sql.Row;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class JanusGraphConsumer {

    private static JanusGraphConsumer consumer = null;

    private JanusGraphConsumer() {

    }

    public static JanusGraphConsumer getInstance() {
        if(consumer == null) {
            consumer = new JanusGraphConsumer();
        }
        return consumer;
    }

    public void loadMoviesToJanus(@NotNull Iterator<Row> iterator) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEdgesCastToJanus(@NotNull Iterator<Row> iterator) {
        try {
            System.out.println("Came Cast Partition");
            JanusGraphClient client = new JanusGraphClient();
            GraphTraversalSource g = client.getG();
            JanusGraph graph = client.getGraph();
            while (iterator.hasNext()) {
                Row info = iterator.next();
                Vertex v = g.V("person_" + info.getInt(0)).next();
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
    }

    public void loadEdgesCrewToJanus(@NotNull Iterator<Row> iterator) {
        try {
            JanusGraphClient client = new JanusGraphClient();
            GraphTraversalSource g = client.getG();
            JanusGraph graph = client.getGraph();

            while (iterator.hasNext()) {
                Row info = iterator.next();
                Vertex v = g.V("person_" + info.getInt(0)).next();
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
    }

    public void loadVertexesToJanus(@NotNull Iterator<Row> iterator) {
        try {
            JanusGraphClient client = new JanusGraphClient();
            JanusGraphTransaction tx = client.getGraph().buildTransaction().enableBatchLoading().consistencyChecks(false).start();
            while (iterator.hasNext()) {
                Row info = iterator.next();
                tx.addVertex(T.label, "person",
                        T.id, "person_" + info.getInt(0),
                        "id", info.getInt(0),
                        "name", info.getString(1),
                        "gender", info.getInt(2),
                        "profile_path", info.getString(3),
                        "vertex_type", "person");
            }
            tx.commit();
            tx.close();
            client.closeConnection();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
