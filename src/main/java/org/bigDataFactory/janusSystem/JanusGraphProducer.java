package org.bigDataFactory.janusSystem;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.Index;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.SchemaAction;

public class JanusGraphProducer {

    public void createSchema() throws Exception {

        new JanusGraphClient();
        JanusGraphFactory.drop(JanusGraphClient.getGraph());
        JanusGraphClient client = new JanusGraphClient();
        JanusGraphManagement management = JanusGraphClient.getGraph().openManagement();

        final VertexLabel crew = management.makeVertexLabel("crew").make();
        final VertexLabel cast = management.makeVertexLabel("cast").make();
        final VertexLabel movie = management.makeVertexLabel("movie").make();

        final EdgeLabel acted = management.makeEdgeLabel("acted").unidirected().make();
        final EdgeLabel worked = management.makeEdgeLabel("worked").unidirected().make();


        final PropertyKey cast_id = management.makePropertyKey("cast_id").dataType(Integer.class).make();
        final PropertyKey character = management.makePropertyKey("character").dataType(String.class).make();
        final PropertyKey gender = management.makePropertyKey("gender").dataType(Integer.class).make();
        final PropertyKey credit_id = management.makePropertyKey("credit_id").dataType(String.class).make();
        final PropertyKey name = management.makePropertyKey("name").dataType(String.class).make();
        final PropertyKey profile_path = management.makePropertyKey("profile_path").dataType(String.class).make();
        final PropertyKey id = management.makePropertyKey("id").dataType(Integer.class).make();
        final PropertyKey order = management.makePropertyKey("order").dataType(Integer.class).make();
        final PropertyKey movie_id = management.makePropertyKey("movie_id").dataType(String.class).make();
        final PropertyKey department = management.makePropertyKey("department").dataType(String.class).make();
        final PropertyKey job = management.makePropertyKey("job").dataType(String.class).make();

        management.addProperties(cast, name, gender, id, profile_path);
        management.addProperties(crew, name, gender, id, profile_path);
        management.addProperties(acted, cast_id, character, credit_id, order);
        management.addProperties(worked, credit_id, department, job);
        management.addProperties(movie, movie_id);

        final Index byId = management.buildIndex("byId", Vertex.class).addKey(id).unique().buildCompositeIndex();
        final Index byName = management.buildIndex("byName", Vertex.class).addKey(name).buildCompositeIndex();
        final Index byMovieId = management.buildIndex("byMovieId", Vertex.class).addKey(movie_id).unique().buildCompositeIndex();
        final Index byCreditId = management.buildIndex("byCreditId", Edge.class).addKey(credit_id).buildCompositeIndex();

        management.updateIndex(byId, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byName, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byMovieId, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byCreditId, SchemaAction.ENABLE_INDEX);

        System.out.println(management.printSchema());
        System.out.println(management.printIndexes());

        management.commit();

        client.closeConnection();
    }
}
