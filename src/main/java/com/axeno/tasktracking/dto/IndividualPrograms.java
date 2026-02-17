package com.axeno.tasktracking.dto;

public class IndividualPrograms {
    private String id;
    private String name;

    public IndividualPrograms(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
