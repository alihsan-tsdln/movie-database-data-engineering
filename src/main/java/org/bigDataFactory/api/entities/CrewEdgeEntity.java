package org.bigDataFactory.api.entities;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class CrewEdgeEntity extends PersonEntity{
    private String credit_id;
    private String department;
    private String job;

    public CrewEdgeEntity(Vertex v, Edge e) {
        super(v);
        this.department = e.value("department");
        this.job = e.value("job");
        this.credit_id = e.value("credit_id");
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
