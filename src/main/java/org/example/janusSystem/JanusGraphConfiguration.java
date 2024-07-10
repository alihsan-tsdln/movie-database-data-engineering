package org.example.janusSystem;

import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphManagement;

public class JanusGraphConfiguration {

    private static volatile JanusGraphConfiguration config = null;
    private JanusGraphFactory.Builder build = null;

    private static JanusGraphManagement management;

    private JanusGraphConfiguration() {
        JanusGraphClient client = JanusGraphClient.getInstance();
        management = client.getGraph().openManagement();
    }


    public static JanusGraphConfiguration getInstance() {
        if(config == null) {
            config = new JanusGraphConfiguration();
        }
        return config;
    }

    public JanusGraphFactory.Builder config(String backend, String hostname, String port) {
        build = JanusGraphFactory.build();
        build.set("storage.backend", backend);
        build.set("storage.hostname", hostname);
        build.set("storage.port", port);

        return build;
    }

    public JanusGraphFactory.Builder config(String backend, String hostname, String port, String schemaDefault, boolean schemaConstraints) {
        build = JanusGraphFactory.build();
        build.set("storage.backend", backend);
        build.set("storage.hostname", hostname);
        build.set("storage.port", port);
        build.set("schema.default",schemaDefault);
        build.set("schema.constraints",schemaConstraints);

        return build;
    }

    public JanusGraphFactory.Builder config() {
        build = JanusGraphFactory.build();
        return build;
    }

    public JanusGraphFactory.Builder config(String backend, String indexer, String hostname, String port) {
        JanusGraphFactory.Builder build = JanusGraphFactory.build();
        build.set("storage.backend", backend);
        build.set("storage.hostname", hostname);
        build.set("storage.port", port);
        build.set("index.search.backend", indexer);
        build.set("index.search.hostname", hostname);
        build.set("index.search.elasticsearch.client-only", "true");

        return build;
    }
    
    public PropertyKey addPropertyKey(String label, Class<?> dataType) {
        return management.makePropertyKey(label).dataType(dataType).make();
    }

    public EdgeLabel createEdgeLabel(String label, Multiplicity multiplicity) {
        return management.makeEdgeLabel(label).multiplicity(multiplicity).make();
    }

    public EdgeLabel createEdgeLabel(String label, Multiplicity multiplicity, PropertyKey signature) {
        return management.makeEdgeLabel(label).multiplicity(multiplicity).signature(signature).make();
    }

    public EdgeLabel createEdgeLabel(String label, PropertyKey signature) {
        return management.makeEdgeLabel(label).signature(signature).make();
    }

    public EdgeLabel createEdgeLabel(String label) {
        return management.makeEdgeLabel(label).make();
    }

    public JanusGraphManagement getManagement() {
        return management;
    }


}
