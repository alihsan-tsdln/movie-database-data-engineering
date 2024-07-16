package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

public class JanusGraphClient {
    private static JanusGraphClient client = null;
    private static GraphTraversalSource g;
    private static JanusGraph graph;

    private JanusGraphClient() {
        graph = JanusGraphConfiguration.getInstance().config("cql","janusgraph","8182", "none", true).open();
        g = graph.traversal();
    }

    public static synchronized JanusGraphClient getInstance() {
        if(client == null) {
            client = new JanusGraphClient();
        }
        return client;
    }

    public void closeConnection() throws Exception {
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
