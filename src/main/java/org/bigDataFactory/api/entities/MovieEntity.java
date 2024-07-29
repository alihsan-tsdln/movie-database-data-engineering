package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class MovieEntity {
    private String movie_id;
    private String vertex_type;

    public MovieEntity(Vertex v) {
        this.movie_id = v.value("movie_id");
        this.vertex_type = v.value("vertex_type");
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getVertex_type() {
        return vertex_type;
    }

    public void setVertex_type(String vertex_type) {
        this.vertex_type = vertex_type;
    }
}
