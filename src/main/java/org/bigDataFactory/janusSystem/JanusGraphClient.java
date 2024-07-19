package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

public class JanusGraphClient {
    private GraphTraversalSource g;
    private JanusGraph graph;

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

    public GraphTraversalSource getG() {
        return g;
    }

    public JanusGraph getGraph() {
        return graph;
    }
}
