package org.example.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

public class JanusGraphClient {
    private static volatile JanusGraphClient client = null;
    private static GraphTraversalSource g;
    private static JanusGraph graph;

    private JanusGraphClient() {

        g = createConnection();
    }

    public static JanusGraphClient getInstance() {
        if(client == null) {
            client = new JanusGraphClient();
        }
        return client;
    }

    private GraphTraversalSource createConnection() {
        JanusGraphConfiguration configuration = JanusGraphConfiguration.getInstance();
        graph = configuration.config("cql","janusgraph","8182", "none", true).open();
        return graph.traversal();
    }

    public void closeConnection(){
        g.tx().commit();
        graph.close();
    }

    public GraphTraversalSource getG() {
        return g;
    }

    public JanusGraph getGraph() {
        return graph;
    }
}
