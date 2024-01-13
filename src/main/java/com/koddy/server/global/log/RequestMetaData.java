package com.koddy.server.global.log;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.koddy.server.global.log.RequestMetadataExtractor.getClientIP;
import static com.koddy.server.global.log.RequestMetadataExtractor.getHttpMethod;
import static com.koddy.server.global.log.RequestMetadataExtractor.getRequestUriWithQueryString;
import static com.koddy.server.global.log.RequestMetadataExtractor.getSeveralParamsViaParsing;

public class RequestMetaData {
    private final Map<String, Object> data = new LinkedHashMap<>();

    public RequestMetaData(final String taskId, final HttpServletRequest request) {
        data.put("Task ID", taskId);
        data.put("IP", getClientIP(request));
        data.put("HTTP Method", getHttpMethod(request));
        data.put("Request URI", getRequestUriWithQueryString(request));
        data.put("Params", getSeveralParamsViaParsing(request));
        data.put("Request Time", LocalDateTime.now());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final String key : data.keySet()) {
            sb.append("%s = %s, ".formatted(key, data.get(key)));
        }
        return sb.toString();
    }
}
