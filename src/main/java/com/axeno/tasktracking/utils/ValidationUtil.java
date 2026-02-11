package com.axeno.tasktracking.utils;

import com.axeno.tasktracking.model.TaskTrackingData;
import com.axeno.tasktracking.model.Individual;
import com.axeno.tasktracking.model.Program;
import com.axeno.tasktracking.model.Project;

public class ValidationUtil {

    public static boolean doesUserExist(String userId, TaskTrackingData data) {
        if (data == null || data.getIndividuals() == null) {
            return false;
        }
        for (Individual individual : data.getIndividuals()) {
            if (individual.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesProgramExist(String programId, TaskTrackingData data) {
        if (data == null || data.getPrograms() == null) {
            return false;
        }
        for (Program program : data.getPrograms()) {
            if (program.getId().equals(programId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesProjectExist(String projectId, TaskTrackingData data) {
        if (data == null || data.getPrograms() == null) {
            return false;
        }
        for (Program program : data.getPrograms()) {
            if (program.getProjects() == null) {
                continue;
            }
            for (Project project : program.getProjects()) {
                if (project.getId().equals(projectId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
