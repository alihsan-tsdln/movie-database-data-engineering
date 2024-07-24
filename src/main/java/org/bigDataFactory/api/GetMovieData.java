package org.bigDataFactory.api;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.bigDataFactory.janusSystem.JanusGraphClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Set;

@RestController
public class GetMovieData {

    @Autowired
    JanusGraphConnector connector;

    @GetMapping("/movie")
    public void searchMovie(@RequestParam String movie) { getDataFromJanusGraph("movie_" + movie); }

    @GetMapping("/cast")
    public void searchCast(@RequestParam String cast) { getDataFromJanusGraph("cast_" + cast); }

    @GetMapping("/crew")
    public void searchCrew(@RequestParam String crew) { getDataFromJanusGraph("crew_" + crew); }

    @GetMapping("/movieActors")
    public void getActor(@RequestParam String movie) {
        GraphTraversal<Vertex, Vertex> values = getClient().getG().V("movie_" + movie).out("acted");
        while (values.hasNext())
            printValues(values.next());
    }

    @GetMapping("/movieCrew")
    public void getCrew(@RequestParam String movie) {
        GraphTraversal<Vertex, Vertex> values = getClient().getG().V("movie_" + movie).out("worked");
        while (values.hasNext())
            printValues(values.next());
    }

    @GetMapping("/played")
    public void getPlayed(@RequestParam String cast) {
        GraphTraversal<Vertex, Vertex> values = getClient().getG().V("cast_" + cast).out();
        while (values.hasNext())
            printValues(values.next());
    }

    private void getDataFromJanusGraph(String id) {
        printValues(getClient().getG().V(id).next());
    }

    private void printValues(Vertex val) {
        Iterator<Object> values = val.values();
        Set<String> keys = val.keys();
        System.out.println();
        System.out.println(keys);
        while (values.hasNext())
            System.out.println(values.next());
    }

    private JanusGraphClient getClient() {
         return new JanusGraphConnector().connectJanusGraph();
    }

}
