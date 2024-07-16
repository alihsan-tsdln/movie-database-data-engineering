package org.bigDataFactory.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetMovieData {

    @GetMapping("/search")
    public void searchMovie(@RequestParam String movie, @RequestParam String cast, @RequestParam String crew) {
        System.out.println(movie);
    }

}
