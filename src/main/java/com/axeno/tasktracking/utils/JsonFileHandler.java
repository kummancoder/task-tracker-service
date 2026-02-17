package com.axeno.tasktracking.utils;

import com.axeno.tasktracking.model.TaskTrackingData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class JsonFileHandler {

    private static final String DATA_DIR = "tasktracker";
    private static final String DATA_FILE = "data.json";
    private static final Gson gson = new Gson();

    private static Path getDataFilePath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, DATA_DIR, DATA_FILE);
    }

    public static void saveFile(Part filePart) throws IOException {
        Path dataFilePath = getDataFilePath();
        Path parentDir = dataFilePath.getParent();

        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static TaskTrackingData loadData() throws IOException {
        Path dataFilePath = getDataFilePath();

        if (!Files.exists(dataFilePath)) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(dataFilePath)) {
            return gson.fromJson(reader, TaskTrackingData.class);
        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse JSON data", e);
        }
    }
}
