package org.bigDataFactory.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetMovieData {

    @GetMapping("/tmdb_id")
    public void searchMovie(@RequestParam String movie) {
        new JanusGraphConnector().connectJanusGraph();
    }

}
