package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

public class JanusGraphClient {
    private static GraphTraversalSource g;
    private static JanusGraph graph;

    public JanusGraphClient() {
        graph = new JanusGraphConfiguration().config("cql","0.0.0.0","9042").open();
        System.out.println("opened client");
        g = graph.traversal();
    }

    public void closeConnection() throws Exception {
        System.out.println("closed client");
        g.tx().commit();
        g.tx().close();
        g.close();
        graph.close();
    }

    public static GraphTraversalSource getG() {
        return g;
    }

    public static JanusGraph getGraph() {
        return graph;
    }
}
