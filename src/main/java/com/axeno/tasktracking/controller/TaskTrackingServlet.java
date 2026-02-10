package com.axeno.tasktracking.controller;

import com.axeno.tasktracking.dto.ApiResponse;
import com.axeno.tasktracking.dto.PendingTaskInfo;
import com.axeno.tasktracking.model.*;
import com.axeno.tasktracking.utils.ResponseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/*")
public class TaskTrackingServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private TaskTrackingData taskTrackingData;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String path = request.getPathInfo();

        if (!"/import".equals(path)) {
            ResponseUtil.sendJson(response, 404,
                    new ApiResponse<>(false, "Invalid endpoint", null));
            return;
        }

        try {
            String requestBody = request.getReader()
                    .lines()
                    .collect(Collectors.joining());

            if (requestBody == null || requestBody.trim().isEmpty()) {
                ResponseUtil.sendJson(response, 400,
                        new ApiResponse<>(false, "Request body is empty", null));
                return;
            }

            TaskTrackingData data = gson.fromJson(requestBody, TaskTrackingData.class);

            if (data == null || data.getPrograms() == null) {
                ResponseUtil.sendJson(response, 400,
                        new ApiResponse<>(false, "Invalid JSON structure", null));
                return;
            }

            taskTrackingData = data;

            ResponseUtil.sendJson(response, 200,
                    new ApiResponse<>(true, "Data imported successfully", null));

        } catch (JsonSyntaxException e) {
            ResponseUtil.sendJson(response, 400,
                    new ApiResponse<>(false, "Malformed JSON", null));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (taskTrackingData == null) {
            ResponseUtil.sendJson(response, 400,
                    new ApiResponse<>(false, "No data imported yet", null));
            return;
        }

        String path = request.getPathInfo();

        try {

            // Programs by Individual
            if ("/programs".equals(path)) {

                String individualId = request.getParameter("individualId");

                if (individualId == null || individualId.isEmpty()) {
                    ResponseUtil.sendJson(response, 400,
                            new ApiResponse<>(false, "individualId is required", null));
                    return;
                }

                List<Program> programs = taskTrackingData.getPrograms().stream()
                        .filter(program -> program.getProjects() != null &&
                                program.getProjects().stream()
                                        .anyMatch(project -> project.getEnrolledIndividuals() != null &&
                                                project.getEnrolledIndividuals().contains(individualId)))
                        .collect(Collectors.toList());

                ResponseUtil.sendJson(response, 200,
                        new ApiResponse<>(true, "Programs fetched", programs));
            }

            // Projects by Individual
            else if ("/projects".equals(path)) {

                String individualId = request.getParameter("individualId");

                if (individualId == null || individualId.isEmpty()) {
                    ResponseUtil.sendJson(response, 400,
                            new ApiResponse<>(false, "individualId is required", null));
                    return;
                }

                List<Project> projects = taskTrackingData.getPrograms().stream()
                        .flatMap(program -> program.getProjects() != null
                                ? program.getProjects().stream()
                                : Collections.<Project>emptyList().stream())
                        .filter(project -> project.getEnrolledIndividuals() != null &&
                                project.getEnrolledIndividuals().contains(individualId))
                        .collect(Collectors.toList());

                ResponseUtil.sendJson(response, 200,
                        new ApiResponse<>(true, "Projects fetched", projects));
            }

            // Pending Task Details
            else if ("/tasks/pending".equals(path)) {

                String individualId = request.getParameter("individualId");
                String programId = request.getParameter("programId");
                String projectId = request.getParameter("projectId");

                List<PendingTaskInfo> pendingTasks = new ArrayList<>();

                for (Program program : taskTrackingData.getPrograms()) {

                    if (programId != null && !program.getId().equals(programId)) {
                        continue;
                    }

                    if (program.getProjects() == null)
                        continue;

                    for (Project project : program.getProjects()) {

                        if (projectId != null && !project.getId().equals(projectId)) {
                            continue;
                        }

                        if (project.getTasks() == null)
                            continue;

                        for (Task task : project.getTasks()) {

                            if (task.getStatus() == null ||
                                    !task.getStatus().name().equalsIgnoreCase("PENDING")) {
                                continue;
                            }

                            if (individualId != null) {
                                if (task.getOwners() == null ||
                                        !task.getOwners().contains(individualId)) {
                                    continue;
                                }
                            }

                            pendingTasks.add(new PendingTaskInfo(
                                    program.getId(),
                                    program.getName(),
                                    project.getId(),
                                    project.getName(),
                                    task.getId(),
                                    task.getTitle()));
                        }
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("count", pendingTasks.size());
                result.put("tasks", pendingTasks);

                ResponseUtil.sendJson(response, 200,
                        new ApiResponse<>(true, "Pending tasks fetched", result));
            }

            // Program Name by Project
            else if (path.startsWith("/programs/by-project/")) {

                String projectId = path.substring(path.lastIndexOf("/") + 1);

                String programName = null;

                for (Program program : taskTrackingData.getPrograms()) {
                    if (program.getProjects() == null)
                        continue;

                    for (Project project : program.getProjects()) {
                        if (project.getId().equals(projectId)) {
                            programName = program.getName();
                            break;
                        }
                    }
                }

                ResponseUtil.sendJson(response, 200,
                        new ApiResponse<>(true, "Program name fetched", programName));
            }

            else {
                ResponseUtil.sendJson(response, 404,
                        new ApiResponse<>(false, "Invalid endpoint", null));
            }

        } catch (Exception e) {
            ResponseUtil.sendJson(response, 500,
                    new ApiResponse<>(false, "Server error", null));
        }
    }
}