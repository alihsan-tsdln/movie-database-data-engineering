package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

public class JanusGraphClient {
    private final GraphTraversalSource g;
    private final JanusGraph graph;

    public JanusGraphClient() {
        graph = new JanusGraphConfiguration().config("cql","0.0.0.0","9042").open();
        System.out.println("opened client");
        g = graph.traversal();
    }

    public void closeConnection() throws Exception {
        System.out.println("closed client");
        g.close();
        graph.close();
    }

    public GraphTraversalSource getG() {
        return g;
    }

    public JanusGraph getGraph() {
        return graph;
    }
}
