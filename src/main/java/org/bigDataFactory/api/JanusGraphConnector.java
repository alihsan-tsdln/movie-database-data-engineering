package org.bigDataFactory.api;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JanusGraphConnector {
    @Bean
    public GraphTraversalSource connectJanusGraph() {
        return new JanusGraphClient().getG();
    }
}
