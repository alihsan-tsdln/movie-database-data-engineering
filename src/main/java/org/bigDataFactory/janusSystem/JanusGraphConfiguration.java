package org.bigDataFactory.janusSystem;

import org.janusgraph.core.JanusGraphFactory;

public class JanusGraphConfiguration {

    private static JanusGraphConfiguration config = null;
    private JanusGraphFactory.Builder build = null;


    public static synchronized JanusGraphConfiguration getInstance() {
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
        build.set("schema.default","default");
        build.set("schema.constraints",false);

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


}
