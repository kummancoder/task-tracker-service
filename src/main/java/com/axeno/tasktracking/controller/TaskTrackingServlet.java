package com.axeno.tasktracking.controller;

import com.axeno.tasktracking.dto.*;
import com.axeno.tasktracking.model.*;
import com.axeno.tasktracking.utils.JsonFileHandler;
import com.axeno.tasktracking.utils.ResponseUtil;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/*")
@MultipartConfig
public class TaskTrackingServlet extends HttpServlet {

    private TaskTrackingData taskTrackingData;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String path = request.getPathInfo();

        if (!"/import".equals(path)) {
            ResponseUtil.sendJson(response, 404,
                    new ApiResponse<>(false, "Invalid endpoint", null));
            return;
        }

        try {
            Part filePart = request.getPart("file");

            if (filePart == null) {
                ResponseUtil.sendJson(response, 400,
                        new ApiResponse<>(false, "File part 'file' is missing", null));
                return;
            }

            JsonFileHandler.saveFile(filePart);
            TaskTrackingData data = JsonFileHandler.loadData();

            if (data == null || data.getPrograms() == null) {
                ResponseUtil.sendJson(response, 400,
                        new ApiResponse<>(false, "Invalid JSON structure in file", null));
                return;
            }

            taskTrackingData = data;

            ResponseUtil.sendJson(response, 200,
                    new ApiResponse<>(true, "Data imported successfully", null));

        } catch (JsonSyntaxException e) {
            ResponseUtil.sendJson(response, 400,
                    new ApiResponse<>(false, "Malformed JSON", null));
        } catch (Exception e) {
            ResponseUtil.sendJson(response, 500,
                    new ApiResponse<>(false, "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (taskTrackingData == null) {
            try {
                taskTrackingData = JsonFileHandler.loadData();
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJson(response, 500,
                        new ApiResponse<>(false, "Failed to load cached data: " + e.getMessage(), null));
                return;
            }
        }

        if (taskTrackingData == null) {
            ResponseUtil.sendJson(response, 400,
                    new ApiResponse<>(false, "No data imported yet. Please upload data via /import endpoint.", null));
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

                List<IndividualPrograms> programs = taskTrackingData.getPrograms().stream()
                        .filter(program -> program.getProjects() != null &&
                                program.getProjects().stream()
                                        .anyMatch(project -> project.getEnrolledIndividuals() != null &&
                                                project.getEnrolledIndividuals().contains(individualId)))
                        .map(program -> new IndividualPrograms(program.getId(), program.getName()))
                        .collect(Collectors.toList());

                ResponseUtil.sendJson(response, 200,
                        new ApiResponse<>(true, "Programs fetched", programs));
            }

            // Pending Task Details
            else if ("/tasks/pending".equals(path)) {

                String individualId = request.getParameter("individualId");
                String programId = request.getParameter("programId");
                String projectId = request.getParameter("projectId");

                if (projectId != null && !projectId.isEmpty()) {
                    List<com.axeno.tasktracking.dto.ProjectPendingTask> pendingTasks = new ArrayList<>();
                    String resolvedProgramId = null;
                    String programName = null;
                    String projectName = null;

                    for (Program program : taskTrackingData.getPrograms()) {
                        if (program.getProjects() == null)
                            continue;

                        for (Project project : program.getProjects()) {
                            if (project.getId().equals(projectId)) {
                                resolvedProgramId = program.getId();
                                programName = program.getName();
                                projectName = project.getName();

                                if (project.getTasks() != null) {
                                    for (Task task : project.getTasks()) {
                                        if (task.getStatus() == null
                                                || !task.getStatus().name().equalsIgnoreCase("PENDING")) {
                                            continue;
                                        }
                                        if (individualId != null) {
                                            if (task.getOwners() == null || !task.getOwners().contains(individualId)) {
                                                continue;
                                            }
                                        }
                                        pendingTasks.add(new com.axeno.tasktracking.dto.ProjectPendingTask(
                                                task.getId(),
                                                task.getTitle()));
                                    }
                                }
                                break;
                            }
                        }
                        if (resolvedProgramId != null)
                            break;
                    }

                    if (resolvedProgramId != null) {
                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("programId", resolvedProgramId);
                        result.put("programName", programName);
                        result.put("projectId", projectId);
                        result.put("projectName", projectName);
                        result.put("count", pendingTasks.size());
                        result.put("tasks", pendingTasks);

                        ResponseUtil.sendJson(response, 200,
                                new ApiResponse<>(true, "Pending tasks fetched", result));
                    } else {
                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("projectId", projectId);
                        result.put("count", 0);
                        result.put("tasks", Collections.emptyList());

                        ResponseUtil.sendJson(response, 200,
                                new ApiResponse<>(true, "Project not found or no pending tasks", result));

                    }

                } else if (programId != null && !programId.isEmpty()) {
                    List<UserProgramPendingTask> pendingTasks = new ArrayList<>();
                    String programName = null;

                    for (Program program : taskTrackingData.getPrograms()) {
                        if (program.getId().equals(programId)) {
                            programName = program.getName();
                            if (program.getProjects() == null)
                                continue;

                            for (Project project : program.getProjects()) {
                                if (project.getTasks() == null)
                                    continue;

                                for (Task task : project.getTasks()) {
                                    if (task.getStatus() == null
                                            || !task.getStatus().name().equalsIgnoreCase("PENDING")) {
                                        continue;
                                    }
                                    if (individualId != null) {
                                        if (task.getOwners() == null || !task.getOwners().contains(individualId)) {
                                            continue;
                                        }
                                    }
                                    pendingTasks.add(new UserProgramPendingTask(
                                            project.getId(),
                                            project.getName(),
                                            task.getId(),
                                            task.getTitle()));
                                }
                            }
                            break;
                        }
                    }

                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("programId", programId);
                    result.put("programName", programName);
                    result.put("count", pendingTasks.size());
                    result.put("tasks", pendingTasks);

                    ResponseUtil.sendJson(response, 200,
                            new ApiResponse<>(true, "Pending tasks fetched", result));

                } else {
                    List<UserPendingTask> pendingTasks = new ArrayList<>();

                    for (Program program : taskTrackingData.getPrograms()) {
                        if (program.getProjects() == null)
                            continue;

                        for (Project project : program.getProjects()) {
                            if (project.getTasks() == null)
                                continue;

                            for (Task task : project.getTasks()) {
                                if (task.getStatus() == null || !task.getStatus().name().equalsIgnoreCase("PENDING")) {
                                    continue;
                                }
                                if (individualId != null) {
                                    if (task.getOwners() == null || !task.getOwners().contains(individualId)) {
                                        continue;
                                    }
                                }

                                pendingTasks.add(new UserPendingTask(
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