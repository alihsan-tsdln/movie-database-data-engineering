package org.bigDataFactory;

import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
        JanusGraphClient client = new JanusGraphClient();
        //SparkConverter spark = SparkConverter.getInstance();
        //spark.fetchDataCsv();
        //spark.closeSpark();
/*
        JanusGraphClient client = new JanusGraphClient();
        GraphTraversalSource g = client.getG();
        //System.out.println("VERTEX COUNT");
        //System.out.println(g.V().has("vertex_type", P.within("cast","crew","movie")).count().next());
        //System.out.println("EDGE COUNT");
        //System.out.println(g.E().has("edge_type", P.within("acted","worked")).count().next());
        /*
        System.out.println("CAST 31");
        GraphTraversal<Vertex, Vertex> m = g.V("cast_31").outE().inV();

        System.out.println(g.V("cast_31").values("name").next());

        while (m.hasNext()) {
            Vertex info = m.next();
            System.out.println(info.label());
            System.out.println(info.keys());
            Iterator<Object> a = info.values();
            while (a.hasNext()) {
                System.out.println(a.next());
            }
            System.out.println();
        }

        Iterator<Object> a;
        a = g.V("movie_27205").out().values();

        while (a.hasNext()) {
            System.out.println(a.next());
        }

        System.out.println("\n\n\n");

        a = g.V("movie_27205").outE().values();

        while (a.hasNext()) {
            System.out.println(a.next());
        }

        client.closeConnection();
        */
    }
}
