package com.axeno.tasktracking.utils;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    private static final Gson gson = new Gson();

    public static void sendJson(HttpServletResponse response,
            int statusCode, Object body) throws IOException {

        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(gson.toJson(body));
    }
}