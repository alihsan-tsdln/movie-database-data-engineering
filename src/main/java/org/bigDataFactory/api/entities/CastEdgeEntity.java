package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class CastEdgeEntity {
    private int cast_id;
    private String character;
    private String credit_id;
    private int order;
    private String movie_id;

    public CastEdgeEntity(Vertex v) {
        this.cast_id = v.value("cast_id");
        this.character = v.value("character");
        this.credit_id = v.value("credit_id");
        this.order = v.value("order");
        this.movie_id = v.value("movie_id");
    }

    public int getCast_id() {
        return cast_id;
    }

    public void setCast_id(int cast_id) {
        this.cast_id = cast_id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(String credit_id) {
        this.credit_id = credit_id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }
}
