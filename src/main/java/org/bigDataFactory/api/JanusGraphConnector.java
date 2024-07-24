package org.bigDataFactory.api;

import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JanusGraphConnector {
    @Bean
    public JanusGraphClient connectJanusGraph() {
        return new JanusGraphClient();
    }
}
