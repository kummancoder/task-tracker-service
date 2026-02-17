package com.axeno.tasktracking.model;

import java.util.List;

public class TaskTrackingData {

    private List<Individual> individuals;
    private List<Program> programs;

    public TaskTrackingData() {
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<Individual> individuals) {
        this.individuals = individuals;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }
}