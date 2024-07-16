package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.*;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.diskstorage.BackendException;

public class JanusGraphProducer {
    private static JanusGraphProducer producer = null;
    private final JanusGraphConfiguration configuration;
    private final GraphTraversalSource g;

    public static synchronized JanusGraphProducer getInstance() {
        if(producer == null) {
            producer = new JanusGraphProducer();
        }

        return producer;
    }

    private JanusGraphProducer() {
        JanusGraphClient client = JanusGraphClient.getInstance();
        g = client.getG();
        configuration = JanusGraphConfiguration.getInstance();
    }
    public void createSchema(boolean uniqueNameCompositeIndex) {

        try {

            JanusGraphClient client = JanusGraphClient.getInstance();

            JanusGraphManagement management = client.getGraph().openManagement();

            final PropertyKey name = management.makePropertyKey("name").dataType(String.class).make();
            JanusGraphManagement.IndexBuilder idx = management.buildIndex("name", Vertex.class).addKey(name);
            if (uniqueNameCompositeIndex)
                idx.unique();
            management.setConsistency(idx.buildCompositeIndex(), ConsistencyModifier.LOCK);

            final PropertyKey age = management.makePropertyKey("age").dataType(Integer.class).make();
            final PropertyKey time = management.makePropertyKey("age").dataType(Integer.class).make();
            final PropertyKey reason = management.makePropertyKey("reason").dataType(String.class).make();
            final PropertyKey place = management.makePropertyKey("place").dataType(Geoshape.class).make();


            management.makeEdgeLabel("father").multiplicity(Multiplicity.MANY2ONE).make();
            management.makeEdgeLabel("mother").multiplicity(Multiplicity.MANY2ONE).make();
            EdgeLabel battled = management.makeEdgeLabel("battled").signature(time).make();
            management.buildEdgeIndex(battled, "battlesByTime", Direction.BOTH, Order.desc, time);
            management.makeEdgeLabel("lives").signature(reason).make();
            management.makeEdgeLabel("pet").make();
            management.makeEdgeLabel("brother").make();

            management.makeVertexLabel("titan").make();
            management.makeVertexLabel("location").make();
            management.makeVertexLabel("god").make();
            management.makeVertexLabel("demigod").make();
            management.makeVertexLabel("human").make();
            management.makeVertexLabel("monster").make();

            management.commit();
        }
        catch (SchemaViolationException e) {
            e.printStackTrace();
        }
        JanusGraphManagement management = JanusGraphClient.getInstance().getGraph().openManagement();
        System.out.println(management.printSchema());
    }

    public void deleteAllData() throws BackendException {
        JanusGraphFactory.drop(JanusGraphClient.getInstance().getGraph());

    }

    public void addAllGodsData() {
        JanusGraphTransaction tx = JanusGraphClient.getInstance().getGraph().newTransaction();
        // vertices

        Vertex saturn = tx.addVertex(T.label, "titan", "name", "saturn", "age", 10000);
        Vertex sky = tx.addVertex(T.label, "location", "name", "sky");
        Vertex sea = tx.addVertex(T.label, "location", "name", "sea");
        Vertex jupiter = tx.addVertex(T.label, "god", "name", "jupiter", "age", 5000);
        Vertex neptune = tx.addVertex(T.label, "god", "name", "neptune", "age", 4500);
        Vertex hercules = tx.addVertex(T.label, "demigod", "name", "hercules", "age", 30);
        Vertex alcmene = tx.addVertex(T.label, "human", "name", "alcmene", "age", 45);
        Vertex pluto = tx.addVertex(T.label, "god", "name", "pluto", "age", 4000);
        Vertex nemean = tx.addVertex(T.label, "monster", "name", "nemean");
        Vertex hydra = tx.addVertex(T.label, "monster", "name", "hydra");
        Vertex cerberus = tx.addVertex(T.label, "monster", "name", "cerberus");
        Vertex tartarus = tx.addVertex(T.label, "location", "name", "tartarus");

        // edges
        jupiter.addEdge("father", saturn);
        jupiter.addEdge("lives", sky, "reason", "loves fresh breezes");
        jupiter.addEdge("brother", neptune);
        jupiter.addEdge("brother", pluto);

        neptune.addEdge("lives", sea).property("reason", "loves waves");
        neptune.addEdge("brother", jupiter);
        neptune.addEdge("brother", pluto);

        hercules.addEdge("father", jupiter);
        hercules.addEdge("mother", alcmene);
        hercules.addEdge("battled", nemean, "time", 1, "place", Geoshape.point(38.1f, 23.7f));
        hercules.addEdge("battled", hydra, "time", 2, "place", Geoshape.point(37.7f, 23.9f));
        hercules.addEdge("battled", cerberus, "time", 12, "place", Geoshape.point(39f, 22f));

        pluto.addEdge("brother", jupiter);
        pluto.addEdge("brother", neptune);
        pluto.addEdge("lives", tartarus, "reason", "no fear of death");
        pluto.addEdge("pet", cerberus);

        cerberus.addEdge("lives", tartarus);

        // commit the transaction to disk
        tx.commit();
    }
}
