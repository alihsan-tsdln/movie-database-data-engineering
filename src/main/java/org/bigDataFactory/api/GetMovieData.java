package org.bigDataFactory.api;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
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
    @GetMapping(value = "/cast", params = {"id"})
    public PersonEntity searchCast(@RequestParam String id) {
        return new PersonEntity(getDataFromJanusGraph("cast_" + id));
    }

    @ResponseBody
    @GetMapping(value = "/cast", params = {"name"})
    public PersonEntity searchCastName(@RequestParam String name) {
        return new PersonEntity(getG().V().has("name",name).next());
    }

    @ResponseBody
    @GetMapping(value = "/crew", params = {"id"})
    public PersonEntity searchCrew(@RequestParam String id) {
        return new PersonEntity(getDataFromJanusGraph("crew_" + id));
    }

    @ResponseBody
    @GetMapping(value = "/crew", params = {"name"})
    public PersonEntity searchCrewName(@RequestParam String name) {
        return new PersonEntity(getG().V().has("name",name).next());
    }

    @ResponseBody
    @GetMapping(value = "/movieActors")
    public List<PersonEntity> getActor(@RequestParam String id) {
        return returnPersonResponseBody(getG().V("movie_" + id).out("acted"));
    }

    @ResponseBody
    @GetMapping(value = "/movieCrew")
    public List<PersonEntity> getCrew(@RequestParam String id) {
        return returnPersonResponseBody(getG().V("movie_" + id).out("worked"));
    }

    @ResponseBody
    @GetMapping(value = "/played")
    public List<MovieEntity> getPlayed(@RequestParam String id) {
        return returnMovieResponseBody(getG().V("cast_" + id).out());
    }

    @ResponseBody
    @GetMapping(value = "/worked")
    public List<MovieEntity> getWorked(@RequestParam String id) {
        return returnMovieResponseBody(getG().V("crew_" + id).out());
    }

    private @NotNull List<PersonEntity> returnPersonResponseBody(@NotNull GraphTraversal<Vertex, Vertex> values) {
        ArrayList<PersonEntity> personEntities = new ArrayList<>();
        while (values.hasNext())
            personEntities.add(new PersonEntity(values.next()));
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
         return new JanusGraphConnector().connectJanusGraph();
    }

}
