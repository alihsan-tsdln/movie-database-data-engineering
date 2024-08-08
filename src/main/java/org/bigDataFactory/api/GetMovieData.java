package org.bigDataFactory.api;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.bigDataFactory.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.janusgraph.core.attribute.Text.textContainsFuzzy;

@RestController
public class GetMovieData {

    @Autowired
    JanusGraphConnector connector;

    @ResponseBody
    @GetMapping(value = "/movie")
    public MovieEntity searchMovie(@RequestParam String id) {
        return new MovieEntity(getDataFromJanusGraph("movie_" + id));
    }

    @ResponseBody
    @GetMapping(value = "/person", params = {"id"})
    public PersonEntity searchPerson(@RequestParam String id) {
        return new PersonEntity(getDataFromJanusGraph("person_" + id));
    }

    @ResponseBody
    @GetMapping(value = "/person", params = {"name"})
    public List<PersonEntity> searchPersonByName(@RequestParam String name) {
        return returnPersonResponseBody(getG().V().has("name", textContainsFuzzy(name)));
    }

    @ResponseBody
    @GetMapping(value = "/movieActors")
    public List<CastEdgeEntity> getActor(@RequestParam String id) {
        return returnCastResponseBody(getG().V("movie_" + id).in("acted"), getG().V("movie_" + id).inE("acted"));
    }

    @ResponseBody
    @GetMapping(value = "/movieCrew")
    public List<CrewEdgeEntity> getCrew(@RequestParam String id) {
        return returnCrewResponseBody(getG().V("movie_" + id).in("worked"), getG().V("movie_" + id).inE("worked"));
    }

    @ResponseBody
    @GetMapping(value = "/played", params = {"id"})
    public List<MovieCastEdgeEntity> getPlayed(@RequestParam String id) {
        return returnMovieCastResponseBody(getG().V("person_" + id).out("acted"), getG().V("person_" + id).outE("acted"));
    }

    @ResponseBody
    @GetMapping(value = "/played", params = {"name"})
    public List<MovieCastEdgeEntity> getPlayedByName(@RequestParam String name) {
        return returnMovieCastResponseBody(getG().V().has("name", textContainsFuzzy(name)).out("acted"), getG().V().has("name", textContainsFuzzy(name)).outE("acted"));
    }

    @ResponseBody
    @GetMapping(value = "/worked", params = {"id"})
    public List<MovieCrewEdgeEntity> getWorked(@RequestParam String id) {
        return returnMovieCrewResponseBody(getG().V("person_" + id).out("worked"), getG().V("person_" + id).outE("worked"));
    }

    @ResponseBody
    @GetMapping(value = "/worked", params = {"name"})
    public List<MovieCrewEdgeEntity> getWorkedByName(@RequestParam String name) {
        return returnMovieCrewResponseBody(getG().V().has("name", textContainsFuzzy(name)).out("worked"), getG().V().has("name", textContainsFuzzy(name)).outE("worked"));
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

    private @NotNull List<MovieCastEdgeEntity> returnMovieCastResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values, @NotNull GraphTraversal<Vertex, Edge> edges) {
        ArrayList<MovieCastEdgeEntity> movieEntities = new ArrayList<>();
        while (values.hasNext() && edges.hasNext())
            movieEntities.add(new MovieCastEdgeEntity(values.next(), edges.next()));
        return movieEntities;
    }

    private @NotNull List<MovieCrewEdgeEntity> returnMovieCrewResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values, @NotNull GraphTraversal<Vertex, Edge> edges) {
        ArrayList<MovieCrewEdgeEntity> movieEntities = new ArrayList<>();
        while (values.hasNext() && edges.hasNext())
            movieEntities.add(new MovieCrewEdgeEntity(values.next(), edges.next()));
        return movieEntities;
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

    private Vertex getDataFromJanusGraph(String id) {
        return getG().V(id).next();
    }

    private GraphTraversalSource getG() {
         return connector.connectJanusGraph().getG();
    }

}
