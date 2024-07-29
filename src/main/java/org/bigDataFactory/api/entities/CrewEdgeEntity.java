package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class CrewEdgeEntity {
    private String credit_id;
    private String department;
    private String job;
    private String movie_id;

    public CrewEdgeEntity(Vertex v) {
        this.department = v.value("department");
        this.job = v.value("job");
        this.credit_id = v.value("credit_id");
        this.movie_id = v.value("movie_id");
    }

    public String getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(String credit_id) {
        this.credit_id = credit_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }
}
