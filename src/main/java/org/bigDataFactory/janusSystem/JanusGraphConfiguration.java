package org.bigDataFactory.janusSystem;

import org.janusgraph.core.JanusGraphFactory;

public class JanusGraphConfiguration {
    private static JanusGraphConfiguration config = null;

    private JanusGraphConfiguration() {

    }

    public static JanusGraphConfiguration getInstance() {
        if(config == null) {
            System.out.println("CONFIGURED");
            config = new JanusGraphConfiguration();
        }
        return config;
    }

    public JanusGraphFactory.Builder config(String backend, String hostname, String port) {
        return JanusGraphFactory.build()
            .set("storage.backend", backend)
            .set("storage.hostname", hostname)
            .set("storage.port", port)
            .set("storage.cql.read-consistency-level","LOCAL_ONE")
            .set("storage.cql.replication-factor",1)
            .set("storage.cql.write-consistency-level","LOCAL_ONE")
            .set("storage.buffer-size", 2048)
            .set("storage.batch-loading", true)
            .set("query.batch",true)
            .set("cache.db-cache", true)
            .set("cache.db-cache-clean-wait", 20)
            .set("cache.db-cache-time", 500000)
            .set("cache.db-cache-size", 0.5)
            .set("cluster.max-partitions",6)
            .set("ids.authority.wait-time", 1000)
            .set("query.force-index", true);
    }

    public JanusGraphFactory.Builder config() {
        return JanusGraphFactory.build();
    }

    public JanusGraphFactory.Builder config(String backStorage, String indexer, String hostnameCQL, String portCQL, String hostnameES, String portES) {
        return JanusGraphFactory.build()
            .set("storage.backend", backStorage)
            .set("storage.hostname", hostnameCQL)
            .set("storage.port", portCQL)
            .set("storage.cql.read-consistency-level","LOCAL_ONE")
            .set("storage.cql.replication-factor",1)
            .set("storage.cql.write-consistency-level","LOCAL_ONE")
            .set("storage.buffer-size", 2048)
            .set("storage.batch-loading", true)
            .set("query.batch",true)
            .set("cache.db-cache", true)
            .set("cache.db-cache-clean-wait", 20)
            .set("cache.db-cache-time", 500000)
            .set("cache.db-cache-size", 0.5)
            .set("cluster.max-partitions",6)
            .set("ids.authority.wait-time", 1000)
            .set("query.force-index", true)
            .set("index.search.backend", indexer)
            .set("index.search.hostname", hostnameES)
            .set("index.search.port", portES)
            .set("index.search.elasticsearch.client-only", true);
    }
}
