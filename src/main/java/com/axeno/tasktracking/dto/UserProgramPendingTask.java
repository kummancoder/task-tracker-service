package com.axeno.tasktracking.dto;

public class UserProgramPendingTask {

    private String projectId;
    private String projectName;
    private String taskId;
    private String taskTitle;

    public UserProgramPendingTask(String projectId, String projectName, String taskId, String taskTitle) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
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
