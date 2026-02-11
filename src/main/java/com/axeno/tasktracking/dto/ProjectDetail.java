package com.axeno.tasktracking.dto;

public class ProjectDetail {
    private String id;
    private String name;
    private String programName;

    public ProjectDetail(String id, String name, String programName) {
        this.id = id;
        this.name = name;
        this.programName = programName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProgramName() {
        return programName;
    }
}
