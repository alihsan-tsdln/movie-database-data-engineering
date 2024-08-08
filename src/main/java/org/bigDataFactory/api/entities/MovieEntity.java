package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class MovieEntity {
    private String movie_id;

    public MovieEntity(Vertex v) {
        this.movie_id = v.value("movie_id");
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }
}
