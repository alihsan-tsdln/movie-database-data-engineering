package org.example.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Iterator;

public class JanusGraphConsumer {

    private static JanusGraphConsumer traversaler = null;
    private final GraphTraversalSource g;
    private JanusGraphConsumer() {
        JanusGraphClient client = JanusGraphClient.getInstance();
        g = client.getG();
    }

    public static JanusGraphConsumer getInstance() {
        if(traversaler == null) {
            traversaler = new JanusGraphConsumer();
        }

        return traversaler;
    }

    public void readAllVertexs() {
        GraphTraversal<Vertex, Vertex> vertices = g.V();

        while (vertices.hasNext()) {
            Vertex v = vertices.next();
            System.out.println("KEYS");
            System.out.println(v.keys());
            System.out.println("LABEL");
            System.out.println(v.label());
            Iterator<Object> values = v.values();
            System.out.println("VALUES");
            while (values.hasNext()) {
                System.out.println(values.next());
            }
            System.out.println();
        }
    }

    public void readAllEdges() {
        GraphTraversal<Edge, Edge> edges = g.E();

        while (edges.hasNext()) {
            Edge e = edges.next();
            Iterator<Object> values = e.values();
            System.out.println("KEYS");
            System.out.println(e.keys());
            System.out.println("LABEL");
            System.out.println(e.label());
            System.out.println("VALUES");
            while (values.hasNext()) {
                System.out.println(values.next());
            }
            System.out.println();
        }
    }
}
