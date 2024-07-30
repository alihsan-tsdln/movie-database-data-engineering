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
        JanusGraphClient  client = new JanusGraphClient();
        JanusGraphFactory.drop(client.getGraph());
        client = new JanusGraphClient();
        JanusGraphManagement management = client.getGraph().openManagement();

        management.set("graph.set-vertex-id", true);
        // optional, if you want to provide string ID
        management.set("graph.allow-custom-vid-types", true);

        final VertexLabel person = management.makeVertexLabel("person").make();
        final VertexLabel movie = management.makeVertexLabel("movie").make();

        final EdgeLabel acted = management.makeEdgeLabel("acted").make();
        final EdgeLabel worked = management.makeEdgeLabel("worked").make();


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
        final PropertyKey vertex_type = management.makePropertyKey("vertex_type").dataType(String.class).make();
        final PropertyKey edge_type = management.makePropertyKey("edge_type").dataType(String.class).make();

        management.addProperties(person, name, gender, id, profile_path, vertex_type);
        management.addProperties(acted, cast_id, character, credit_id, order, edge_type);
        management.addProperties(worked, credit_id, department, job, edge_type);
        management.addProperties(movie, movie_id, vertex_type);

        final Index byIdandLabel = management.buildIndex("byIdAndLabel", Vertex.class).addKey(vertex_type).addKey(id).unique().buildCompositeIndex();
        final Index byName = management.buildIndex("byName", Vertex.class).addKey(name).buildCompositeIndex();
        final Index byMovieId = management.buildIndex("byMovieId", Vertex.class).addKey(movie_id).unique().buildCompositeIndex();
        final Index byEdgeType = management.buildIndex("byEdgeType", Edge.class).addKey(edge_type).buildCompositeIndex();
        final Index byVertexType = management.buildIndex("byVertexType", Vertex.class).addKey(vertex_type).buildCompositeIndex();

        management.updateIndex(byIdandLabel, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byName, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byMovieId, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byVertexType, SchemaAction.ENABLE_INDEX);
        management.updateIndex(byEdgeType, SchemaAction.ENABLE_INDEX);

        //System.out.println(management.printSchema());
        //System.out.println(management.printIndexes());

        management.commit();
        client.closeConnection();
    }
}
