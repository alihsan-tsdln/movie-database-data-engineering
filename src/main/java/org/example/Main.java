package org.example;

import org.example.janusSystem.JanusGraphClient;
import org.example.janusSystem.JanusGraphConsumer;
import org.example.janusSystem.JanusGraphProducer;

public class Main {
    public static void main(String[] args) throws Exception {

        JanusGraphProducer producer = JanusGraphProducer.getInstance();

        JanusGraphClient client = JanusGraphClient.getInstance();

        JanusGraphConsumer consumer = JanusGraphConsumer.getInstance();

        consumer.readAllVertexs();

        consumer.readAllEdges();

        client.closeConnection();
    }
}
