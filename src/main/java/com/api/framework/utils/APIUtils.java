package com.api.framework.utils;
import com.api.framework.config.Settings;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.DocumentContext;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Allure;

import org.testng.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Consolidated utility class for API testing.
 * Provides generic methods for validation and payload management.
 */
public final class APIUtils {

    private static final String MAPPING_FILE = "src/main/java/com/api/framework/config/payload_map.txt";
    private static final Map<String, String> payloadMap = new HashMap<>();

    static {
        loadMappings();
    }

    private APIUtils() {}

    // --- Payload Management ---

    private static void loadMappings() {
        try {
            if (Files.exists(Paths.get(MAPPING_FILE))) {
                Properties properties = new Properties();
                properties.load(Files.newInputStream(Paths.get(MAPPING_FILE)));
                for (String key : properties.stringPropertyNames()) {
                    payloadMap.put(key, properties.getProperty(key));
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to load payload mappings: " + e.getMessage());
        }
    }

    public static String getPayload(String keyword) {
        return getPayload(keyword, new HashMap<>());
    }

    public static String getPayload(String keyword, Map<String, String> variables) {
        String filePath = payloadMap.get(keyword);
        if (filePath == null) {
            throw new RuntimeException("No payload mapping found for keyword: " + keyword);
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                content = content.replace("${" + entry.getKey() + "}", entry.getValue());
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read payload file: " + filePath, e);
        }
    }

    // --- Generic Validations ---

    public static void validateStatusCode(APIResponse response, int expectedCode) {
        Assert.assertEquals(response.status(), expectedCode, "Unexpected status code!");
    }

    public static void validateField(APIResponse response, String jsonPath, Object expectedValue) {
        String body = new String(response.body(), StandardCharsets.UTF_8);
        Object actualValue = JsonPath.read(body, jsonPath);
        
        // If expectedValue is a string that looks like a number, but actual is Number, convert for comparison
        if (actualValue instanceof Number && expectedValue instanceof String) {
            try {
                Double expectedDouble = Double.parseDouble((String) expectedValue);
                Assert.assertEquals(((Number) actualValue).doubleValue(), expectedDouble, "Mismatch for field: " + jsonPath);
                return;
            } catch (NumberFormatException e) { /* fallback to string compare */ }
        }
        
        // If expectedValue is a string that looks like a boolean
        if (actualValue instanceof Boolean && expectedValue instanceof String) {
            Boolean expectedBool = Boolean.parseBoolean((String) expectedValue);
            Assert.assertEquals(actualValue, expectedBool, "Mismatch for field: " + jsonPath);
            return;
        }

        Assert.assertEquals(actualValue.toString(), expectedValue.toString(), "Mismatch for field: " + jsonPath);
    }

    public static void validateString(APIResponse response, String jsonPath, String expectedValue) {
        String body = new String(response.body(), StandardCharsets.UTF_8);
        Object actualValue = JsonPath.read(body, jsonPath);
        Assert.assertEquals(actualValue.toString(), expectedValue, "Mismatch for field: " + jsonPath);
    }

    public static void validateType(APIResponse response, String jsonPath, String expectedType) {
        String body = new String(response.body(), StandardCharsets.UTF_8);
        Object actualValue = JsonPath.read(body, jsonPath);
        
        switch (expectedType.toLowerCase()) {
            case "string":
                Assert.assertTrue(actualValue instanceof String, "Field " + jsonPath + " is not a String!");
                break;
            case "integer":
            case "number":
                Assert.assertTrue(actualValue instanceof Number, "Field " + jsonPath + " is not a Number!");
                break;
            case "boolean":
                Assert.assertTrue(actualValue instanceof Boolean, "Field " + jsonPath + " is not a Boolean!");
                break;
            case "array":
            case "list":
                Assert.assertTrue(actualValue instanceof java.util.List, "Field " + jsonPath + " is not a List/Array!");
                break;
            case "object":
            case "map":
                Assert.assertTrue(actualValue instanceof java.util.Map, "Field " + jsonPath + " is not an Object/Map!");
                break;
            default:
                throw new IllegalArgumentException("Unsupported type for validation: " + expectedType);
        }
    }

    public static void validateMatches(APIResponse response, String jsonPath, String regex) {
        String body = new String(response.body(), StandardCharsets.UTF_8);
        Object actualValue = JsonPath.read(body, jsonPath);
        Assert.assertTrue(actualValue.toString().matches(regex), 
            "Field " + jsonPath + " value [" + actualValue + "] does not match pattern: " + regex);
    }
    
    // --- Generic HTTP Methods ---

    public static APIResponse get(APIRequestContext requestContext, String endpoint) {
        String url = Settings.Url + endpoint;
        APIResponse response = requestContext.get(url);
        logActivity("GET", url, null, response);
        return response;
    }

    public static APIResponse post(APIRequestContext requestContext, String endpoint, Object payload) {
        String url = Settings.Url + endpoint;
        APIResponse response = requestContext.post(url, com.microsoft.playwright.options.RequestOptions.create().setData(payload));
        logActivity("POST", url, payload, response);
        return response;
    }

    public static APIResponse put(APIRequestContext requestContext, String endpoint, Object payload) {
        String url = Settings.Url + endpoint;
        APIResponse response = requestContext.put(url, com.microsoft.playwright.options.RequestOptions.create().setData(payload));
        logActivity("PUT", url, payload, response);
        return response;
    }

    public static APIResponse delete(APIRequestContext requestContext, String endpoint) {
        String url = Settings.Url + endpoint;
        APIResponse response = requestContext.delete(url);
        logActivity("DELETE", url, null, response);
        return response;
    }

    private static void logActivity(String method, String url, Object payload, APIResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Request Details ===\n");
        sb.append("Method: ").append(method).append("\n");
        sb.append("URL: ").append(url).append("\n");
        if (payload != null) {
            sb.append("Request Body: ").append(payload.toString()).append("\n");
        }
        
        sb.append("\n=== Response Details ===\n");
        sb.append("Status: ").append(response.status()).append(" ").append(response.statusText()).append("\n");
        try {
            String body = new String(response.body(), StandardCharsets.UTF_8);
            sb.append("Response Body: ").append(body).append("\n");
        } catch (Exception e) {
            sb.append("Response Body: [Error reading body: ").append(e.getMessage()).append("]\n");
        }

        Allure.addAttachment("API Activity (" + method + " " + url + ")", "text/plain", sb.toString());
    }

    public static Object updateTheJsonValue(Object payload, String path, String value) {
        DocumentContext context;
        if (payload instanceof String) {
            context = JsonPath.parse((String) payload);
        } else {
            context = JsonPath.parse(payload);
        }
        return context.set(path, value).jsonString();
    }

}
