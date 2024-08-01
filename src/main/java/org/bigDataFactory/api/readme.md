# Connecting to server
After leverage Spring server, we don't need different instancies in every job. We are just going to query data from API's.

```java
@Configuration
public class JanusGraphConnector {
    @Bean
    public JanusGraphClient connectJanusGraph() {
        return new JanusGraphClient();
    }
}
```
@Configuration is Springboot annotation for configuration on server. We've used it to don't create instance at every GET request. We will autowire it on other class.

# Configure CORS
CORS let you reach the server. We need it for React.js web application.

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3002", "http://localhost:3003")  // İzin verilen origin (kaynak) adres
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // İzin verilen HTTP metodlar
                .allowedHeaders("*")  // İzin verilen başlıklar
                .allowCredentials(true);  // Cookie'lerin veya kimlik doğrulama bilgilerin gönderilmesine izin verir
    }
}
```
We allowed the ports 3002, 3003 of localhost. We let every request, every header from them.


# Query Data 
Spring takes GET requests and response them with JSON file. These queries are predetermined questions.

```java
@RestController
public class GetMovieData {
```
@RestController makes API works really easy. We can add easily GET request types after that.

```java
@Autowired
JanusGraphConnector connector;
```
We autowired to connector which we introduced before.

```java
@ResponseBody
@GetMapping(value = "/movie")
public MovieEntity searchMovie(@RequestParam String id) {
    return new MovieEntity(getDataFromJanusGraph("movie_" + id));
}

@ResponseBody
@GetMapping(value = "/person", params = {"id"})
public PersonEntity searchCast(@RequestParam String id) {
    return new PersonEntity(getDataFromJanusGraph("person_" + id));
}

@ResponseBody
@GetMapping(value = "/person", params = {"name"})
public List<PersonEntity> searchCastName(@RequestParam String name) {
    return returnPersonResponseBody(getG().V().has("name", textContainsFuzzy(name)).out());
}

@ResponseBody
@GetMapping(value = "/movieActors")
public List<CastEdgeEntity> getActor(@RequestParam String id) {
    return returnCastResponseBody(getG().V("movie_" + id).out("acted"), getG().V("movie_" + id).outE("acted"));
}

@ResponseBody
@GetMapping(value = "/movieCrew")
public List<CrewEdgeEntity> getCrew(@RequestParam String id) {
    return returnCrewResponseBody(getG().V("movie_" + id).out("worked"), getG().V("movie_" + id).outE("worked"));
}

@ResponseBody
@GetMapping(value = "/played", params = {"id"})
public List<MovieEntity> getPlayed(@RequestParam String id) {
    return returnMovieResponseBody(getG().V("person_" + id).out("acted"));
}

@ResponseBody
@GetMapping(value = "/played", params = {"name"})
public List<MovieEntity> getPlayedName(@RequestParam String name) {
    return returnMovieResponseBody(getG().V().has("name", textContainsFuzzy(name)).out("acted"));
}

@ResponseBody
@GetMapping(value = "/worked", params = {"id"})
public List<MovieEntity> getWorked(@RequestParam String id) {
    return returnMovieResponseBody(getG().V("person_" + id).out("worked"));
}

@ResponseBody
@GetMapping(value = "/worked", params = {"name"})
public List<MovieEntity> getWorkedName(@RequestParam String name) {
    return returnMovieResponseBody(getG().V().has("name", textContainsFuzzy(name)).out("worked"));
}

private @NotNull List<CastEdgeEntity> returnCastResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values, @NotNull GraphTraversal<Vertex, Edge> edges) {
    ArrayList<CastEdgeEntity> personEntities = new ArrayList<>();
    while (values.hasNext() && edges.hasNext())
        personEntities.add(new CastEdgeEntity(values.next(), edges.next()));
    return personEntities;
}

private @NotNull List<CrewEdgeEntity> returnCrewResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values, @NotNull GraphTraversal<Vertex, Edge> edges) {
    ArrayList<CrewEdgeEntity> personEntities = new ArrayList<>();
    while (values.hasNext() && edges.hasNext())
        personEntities.add(new CrewEdgeEntity(values.next(), edges.next()));
    return personEntities;
}
```
We don't have to tell these methods one by one. Their logic are same. We took GET request with request parameters. With this parameter we querying our data and send them JSON file. In entities package, you can see entity objects as well. SpringBoot converts them to JSON file. Above methods have our defined functions for preparing data.

```java
private @NotNull List<CastEdgeEntity> returnCastResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values, @NotNull GraphTraversal<Vertex, Edge> edges) {
    ArrayList<CastEdgeEntity> personEntities = new ArrayList<>();
    while (values.hasNext() && edges.hasNext())
        personEntities.add(new CastEdgeEntity(values.next(), edges.next()));
    return personEntities;
}

private @NotNull List<CrewEdgeEntity> returnCrewResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values, @NotNull GraphTraversal<Vertex, Edge> edges) {
    ArrayList<CrewEdgeEntity> personEntities = new ArrayList<>();
    while (values.hasNext() && edges.hasNext())
        personEntities.add(new CrewEdgeEntity(values.next(), edges.next()));
    return personEntities;
}

private @NotNull List<MovieEntity> returnMovieResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values) {
    ArrayList<MovieEntity> movieEntities = new ArrayList<>();
    while (values.hasNext())
        movieEntities.add(new MovieEntity(values.next()));
    return movieEntities;
}
private @NotNull List<PersonEntity> returnPersonResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values) {
    ArrayList<PersonEntity> personEntities = new ArrayList<>();
    while (values.hasNext())
        personEntities.add(new PersonEntity(values.next()));
    return personEntities;
}
```
We can take Traversal iterator and create JSONList with these methods. SpringBoot sends List<Entity> as JSONList.











