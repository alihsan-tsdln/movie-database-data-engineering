package org.bigDataFactory.janusSystem;

import org.janusgraph.core.JanusGraphFactory;

public class JanusGraphConfiguration {
    private JanusGraphFactory.Builder build = null;

    public JanusGraphFactory.Builder config(String backend, String hostname, String port) {
        build = JanusGraphFactory.build();
        build.set("storage.backend", backend);
        build.set("storage.hostname", hostname);
        build.set("storage.port", port);
        build.set("schema.default","default");
        build.set("schema.constraints",false);
        build.set("query.batch",true);
        build.set("cache.db-cache", true);
        build.set("cache.db-cache-clean-wait", 20);
        build.set("cache.db-cache-time", 180000);
        build.set("cache.db-cache-size", 0.5);
        build.set("storage.cql.read-consistency-level","LOCAL_ONE");
        build.set("storage.cql.replication-factor",3);
        build.set("storage.cql.write-consistency-level","LOCAL_ONE");
        build.set("cluster.max-partitions",6);
        build.set("storage.buffer-size", 2048);
        build.set("ids.authority.wait-time", 1000);
        build.set("storage.batch-loading", true);
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
