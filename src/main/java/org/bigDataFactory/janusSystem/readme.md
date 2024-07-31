#Connect JanusGraph
JanusGraphClient is main class for using JanusGraph features. We would like to leverage embedded JanusGraph server at every process. Because we don't need continous work for it. Data store in Apache Cassandra. Thus, we must keep only Cassandra server online.

```java
public class JanusGraphClient {
  private final GraphTraversalSource g;
  private final JanusGraph graph;
  
  public JanusGraphClient() {
      graph = JanusGraphConfiguration.getInstance().config("cql", "elasticsearch", "0.0.0.0",
              "9042", "0.0.0.0", "9200").open();
      System.out.println("opened client");
      g = graph.traversal();
  }
```

Firstly, I should introduce constructor method. We creating JanusGraph instance with open() method. Before this method, we must build instance with configurations. JanusGraphConfiguration does it for us. For now, we only need to know that this class builds JanusGraph instance. You might know graph and g variables from Gremlin. "graph" is for managing database. "g" is for querying and add vertex or edge.

```java
public void closeConnection() throws Exception {
    System.out.println("closed client");
    g.close();
    graph.close();
}
```
This code simply stoping and closing instance.

```java
public GraphTraversalSource getG() {
    return g;
}

public JanusGraph getGraph() {
    return graph;
}
```
And these getter methods for reach these variables from other classes. We should be sure they cannot be set.

# Build with Configurations
JanusGraphConfiguration is builder and configuration class. We can simply build JanusGraph instances with configurations.

```java
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
```
This class is singleton because if we started to project with certain configurations, we shouldn't change it in every creation. 

```java
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
```
config method makes easy to build instances. For our project, I would like to prevent boilerplate code for bulk loading and configured them automaticaly. If you want more deep information for these configurations, [you can look documentations]("https://docs.janusgraph.org/operations/bulk-loading/"). We are using two differant server for this instance. Apache Cassandra as the backstorage and Elasticsearch as the indexer. You only must give hostname and port of each them. If you don't know how to know hostname and port, [please look at here](https://github.com/alihsan-tsdln/movie-database-data-engineering/blob/main/README.md).


# Create Schema for JanusGraph
JanusGrapProducer produces schema for database.

```java
public class JanusGraphProducer {
  private static JanusGraphProducer producer = null;

  private JanusGraphProducer() {

  }

  public static JanusGraphProducer getInstance() {
      if(producer == null) {
          producer = new JanusGraphProducer();
      }
      return producer;
  }
```
This class is singleton because if we started to project with certain configurations, we shouldn't change it in every creation. 

```java
public void createSchema() throws Exception {
```
createSchema method adding vertex labels, edge labels, property keys, vertex properties, edge properties and indexes. Let's dive in.

```java
JanusGraphClient  client = new JanusGraphClient();
JanusGraphManagement management = client.getGraph().openManagement();
```
Firstly we should open managment from graph variable. We can manage schema with this feature.
















