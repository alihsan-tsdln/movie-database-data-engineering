package org.bigDataFactory.api;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.bigDataFactory.api.entities.CastEdgeEntity;
import org.bigDataFactory.api.entities.CrewEdgeEntity;
import org.bigDataFactory.api.entities.MovieEntity;
import org.bigDataFactory.api.entities.PersonEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public PersonEntity searchCast(@RequestParam String id) {
        return new PersonEntity(getDataFromJanusGraph("person_" + id));
    }

    @ResponseBody
    @GetMapping(value = "/person", params = {"name"})
    public PersonEntity searchCastName(@RequestParam String name) {
        return new PersonEntity(getG().V().has("name",name).next());
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
        return returnMovieResponseBody(getG().V().has("name",name).out("acted"));
    }

    @ResponseBody
    @GetMapping(value = "/worked", params = {"id"})
    public List<MovieEntity> getWorked(@RequestParam String id) {
        return returnMovieResponseBody(getG().V("person_" + id).out("worked"));
    }

    @ResponseBody
    @GetMapping(value = "/worked", params = {"name"})
    public List<MovieEntity> getWorkedName(@RequestParam String name) {
        return returnMovieResponseBody(getG().V().has("name", name).out("worked"));
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

    private @NotNull List<MovieEntity> returnMovieResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values) {
        ArrayList<MovieEntity> movieEntities = new ArrayList<>();
        while (values.hasNext())
            movieEntities.add(new MovieEntity(values.next()));
        return movieEntities;
    }

    private Vertex getDataFromJanusGraph(String id) {
        return getG().V(id).next();
    }

    private GraphTraversalSource getG() {
         return connector.connectJanusGraph().getG();
    }

}
