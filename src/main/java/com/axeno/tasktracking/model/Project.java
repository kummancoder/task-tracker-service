package com.axeno.tasktracking.model;

import java.util.List;

public class Project {

    private String id;
    private String name;

    private List<String> enrolledIndividuals;

    private List<Task> tasks;

    public Project() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnrolledIndividuals() {
        return enrolledIndividuals;
    }

    public void setEnrolledIndividuals(List<String> enrolledIndividuals) {
        this.enrolledIndividuals = enrolledIndividuals;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}