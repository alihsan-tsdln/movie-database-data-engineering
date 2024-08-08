package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class MovieCrewEdgeEntity extends MovieEntity{
    private String credit_id;
    private String department;
    private String job;

    public MovieCrewEdgeEntity(Vertex v, Edge e) {
        super(v);
        this.credit_id = e.value("credit_id");
        this.department = e.value("department");
        this.job = e.value("job");
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
}
