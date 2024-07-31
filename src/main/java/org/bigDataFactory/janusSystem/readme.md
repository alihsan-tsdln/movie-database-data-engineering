# Connect JanusGraph
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
Firstly we should open management from graph variable. We can manage schema with this feature.

```java
management.set("graph.set-vertex-id", true);
management.set("graph.allow-custom-vid-types", true);
```
We set for add custom vertex id because reaching with specific id is fastest way to get vertex. We have unique id from json lists.

```java
final VertexLabel person = management.makeVertexLabel("person").make();
final VertexLabel movie = management.makeVertexLabel("movie").make();
```
We got two vertex type. Person and movie. Cast and crew are same for vertex properties.

```java
final EdgeLabel acted = management.makeEdgeLabel("acted").make();
final EdgeLabel worked = management.makeEdgeLabel("worked").make();
```
We got two edge type. Acted and worked. Cast and crew getting distinct at this part. For example Clint Eastwood is a person and connecting a movie which names The Good, The Bad and The Ugly as actor and connecting a movie which names The Unforgiven as a director.

```java
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
```
These are all property keys. Vertex and Edge properties are not different at making them.

```java
management.addProperties(person, name, gender, id, profile_path, vertex_type);
management.addProperties(acted, cast_id, character, credit_id, order, edge_type);
management.addProperties(worked, credit_id, department, job, edge_type);
management.addProperties(movie, movie_id, vertex_type);
```
We could add properties to vertexes and edges finally.

```java
final Index byIdAndLabel = management.buildIndex("byIdAndLabel", Vertex.class).addKey(vertex_type).addKey(id).unique().buildCompositeIndex();
final Index byName = management.buildIndex("byName", Vertex.class).addKey(name, Mapping.TEXT.asParameter()).buildMixedIndex("search");
final Index byMovieId = management.buildIndex("byMovieId", Vertex.class).addKey(movie_id).unique().buildCompositeIndex();
final Index byEdgeType = management.buildIndex("byEdgeType", Edge.class).addKey(edge_type).buildCompositeIndex();
final Index byVertexType = management.buildIndex("byVertexType", Vertex.class).addKey(vertex_type).buildCompositeIndex();

management.updateIndex(byIdAndLabel, SchemaAction.ENABLE_INDEX);
management.updateIndex(byName, SchemaAction.ENABLE_INDEX);
management.updateIndex(byMovieId, SchemaAction.ENABLE_INDEX);
management.updateIndex(byVertexType, SchemaAction.ENABLE_INDEX);
management.updateIndex(byEdgeType, SchemaAction.ENABLE_INDEX);
```
We've built indexes as two differant type. Composite index and mixed index. Mixed indexes need indexer. For more information, [please read the documentation](https://docs.janusgraph.org/index-backend/). Composite indexes can create on JanusGraph. For more information, [please read the documentation](https://docs.janusgraph.org/schema/index-management/index-performance/). Finally, we've enable indexes.

```java
client.closeConnection();
```
Last of the code, we should close JanusGraph instances. All changes saved on Apache Cassandra.

# Load data to JanusGraph
JanusGraphConsumer consumes data from Spark and loads it to JanusGraph.

```java
public class JanusGraphConsumer {
  private static JanusGraphConsumer consumer = null;

  private JanusGraphConsumer() {

  }

  public static JanusGraphConsumer getInstance() {
      if(consumer == null) {
          consumer = new JanusGraphConsumer();
      }
      return consumer;
  }
```
This class is singleton because if we started to project with certain configurations, we shouldn't change it in every creation. 

```java
public void loadMoviesToJanus(@NotNull Iterator<Row> iterator) {
    try {
        JanusGraphClient client = new JanusGraphClient();
        JanusGraphTransaction tx = client.getGraph().buildTransaction().enableBatchLoading().consistencyChecks(false).start();

        while (iterator.hasNext()) {
            Row info = iterator.next();
            tx.addVertex(T.label, "movie", T.id, "movie_" + info.getString(0),
                    "movie_id", info.getString(0), "vertex_type", "movie");
        }
        tx.commit();
        tx.close();
        client.closeConnection();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```
We create JanusGraph intance to load data. We got Movie ids from csv and we can bulk load them easily. Our custom id is "movie_x" like. x is variable which must replace with movie id. Getting transaction object and use it for add vertexes as bulk load.

```java
public void loadVertexesToJanus(@NotNull Iterator<Row> iterator) {
    try {
        JanusGraphClient client = new JanusGraphClient();
        JanusGraphTransaction tx = client.getGraph().buildTransaction().enableBatchLoading().consistencyChecks(false).start();
        while (iterator.hasNext()) {
            Row info = iterator.next();
            tx.addVertex(T.label, "person",
                    T.id, "person_" + info.getInt(0),
                    "id", info.getInt(0),
                    "name", info.getString(1),
                    "gender", info.getInt(2),
                    "profile_path", info.getString(3),
                    "vertex_type", "person");
        }
        tx.commit();
        tx.close();
        client.closeConnection();
    } catch (Exception e) {
        System.out.println(e.getLocalizedMessage());
    }
}
```
We've done same process with loadMoviesToJanus but vertex properties are differant as you know. 

```java
public void loadEdgesCastToJanus(@NotNull Iterator<Row> iterator) {
    try {
        System.out.println("Came Cast Partition");
        JanusGraphClient client = new JanusGraphClient();
        GraphTraversalSource g = client.getG();
        JanusGraph graph = client.getGraph();
        while (iterator.hasNext()) {
            Row info = iterator.next();
            Vertex v = g.V("person_" + info.getInt(0)).next();
            Vertex movie = g.V("movie_" + info.getString(1)).next();
            v.addEdge("acted", movie,
                    "cast_id", info.getInt(2), "character", info.getString(3),
                    "credit_id", info.getString(4), "order", info.getInt(5),
                    "edge_type", "acted");
            movie.addEdge("acted", v,
                    "cast_id", info.getInt(2), "character", info.getString(3),
                    "credit_id", info.getString(4), "order", info.getInt(5),
                    "edge_type", "acted");
        }
        graph.tx().commit();
        graph.tx().close();
        client.closeConnection();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadEdgesCrewToJanus(@NotNull Iterator<Row> iterator) {
    try {
        JanusGraphClient client = new JanusGraphClient();
        GraphTraversalSource g = client.getG();
        JanusGraph graph = client.getGraph();

        while (iterator.hasNext()) {
            Row info = iterator.next();
            Vertex v = g.V("person_" + info.getInt(0)).next();
            Vertex movie = g.V("movie_"+info.getString(1)).next();
            v.addEdge("worked", movie,
                    "credit_id", info.getString(2), "department", info.getString(3),
                    "job", info.getString(4),
                    "edge_type", "worked");
            movie.addEdge("worked", v,
                    "credit_id", info.getString(2), "department", info.getString(3),
                    "job", info.getString(4),
                    "edge_type", "worked");

        }
        graph.tx().commit();
        graph.tx().close();
        client.closeConnection();
    } catch (Exception e) {
        System.out.println(e.getLocalizedMessage());
    }
}
```

We can start adding edges after load vertexes. We can use same approach with other load methods. Only difference is properties are differant in each vertex.

As you can easily see, we created JanusGraph instance at the each method and methods. Because we don't need them always. They just do they job and go.














