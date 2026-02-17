package com.axeno.tasktracking.dto;

public class ProjectPendingTask {

    private String taskId;
    private String taskTitle;

    public ProjectPendingTask(String taskId, String taskTitle) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }
}
