package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class MovieCastEdgeEntity extends MovieEntity{
    private int cast_id;
    private String character;
    private String credit_id;
    private int order;

    public MovieCastEdgeEntity(Vertex v, Edge e) {
        super(v);
        this.cast_id = e.value("cast_id");
        this.character = e.value("character");
        this.credit_id = e.value("credit_id");
        this.order = e.value("order");
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
}
