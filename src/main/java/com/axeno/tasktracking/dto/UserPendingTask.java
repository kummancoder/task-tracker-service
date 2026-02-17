package com.axeno.tasktracking.dto;

public class UserPendingTask {

    private String programId;
    private String programName;
    private String projectId;
    private String projectName;
    private String taskId;
    private String taskTitle;

    public UserPendingTask(String programId, String programName,
            String projectId, String projectName,
            String taskId, String taskTitle) {
        this.programId = programId;
        this.programName = programName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
    }

    public String getProgramId() {
        return programId;
    }

    public String getProgramName() {
        return programName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }
}
