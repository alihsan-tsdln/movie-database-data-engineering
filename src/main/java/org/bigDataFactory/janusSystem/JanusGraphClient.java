package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

public class JanusGraphClient {
    private final GraphTraversalSource g;
    private final JanusGraph graph;

    public JanusGraphClient() {
        graph = JanusGraphConfiguration.getInstance().config("cql", "elasticsearch", "0.0.0.0",
                "9042", "0.0.0.0", "9200").open();
        g = graph.traversal();
    }

    public void closeConnection() throws Exception {
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
