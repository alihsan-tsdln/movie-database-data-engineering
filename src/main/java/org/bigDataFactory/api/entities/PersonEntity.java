package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class PersonEntity {
    private int gender;
    private String name;
    private String profile_path;
    private int id;

    public PersonEntity(Vertex v) {
        this.gender = v.value("gender");
        this.name = v.value("name");
        this.profile_path = v.value("profile_path");
        this.id = v.value("id");
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
