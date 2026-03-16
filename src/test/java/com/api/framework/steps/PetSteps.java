package com.api.framework.steps;

import com.api.framework.StepsMethods.PetAPI;
import com.api.framework.config.ScenarioContext;

import com.api.framework.utils.APIUtils;
import io.cucumber.java.en.*;
import java.util.HashMap;
import java.util.Map;

public class PetSteps {

    private final PetAPI petAPI;
    private final ScenarioContext context;
    public Object payload;

    public PetSteps(ScenarioContext context) {
        this.petAPI = new PetAPI(context);
        this.context = context;
    }

    @Given("get the scheme {string}")
    public void get_the_scheme(String payloadKeyword) {
        payload = APIUtils.getPayload(payloadKeyword);
    }

    @When("post request send with endpoint {string}")
    public void postRequestSendWithEndpointAndPayload(String endpoint) {
        context.setResponse(petAPI.post(endpoint, payload));
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        APIUtils.validateStatusCode(context.getResponse(), statusCode);
    }

    @Then("the json path {string} should be {string}")
    public void theJsonPathShouldBe(String path, String expectedValue) {
        String body = new String(context.getResponse().body(), java.nio.charset.StandardCharsets.UTF_8);
        if (body == null || body.trim().isEmpty() || !body.trim().startsWith("{") && !body.trim().startsWith("[")) {
            org.testng.Assert.fail("Response body is empty or not JSON: " + body);
        }
        String fullPath = path.startsWith("$") ? path : "$." + path;
        APIUtils.validateString(context.getResponse(), fullPath, expectedValue);
    }

    @When("post request send with endpoint {string} and with the payload {string}")
    public void post_request_send_with_endpoint_and_with_the_payload(String endpoint, String payload) {
        if (payload.contains("=")) {
            // Form encoded
             com.microsoft.playwright.options.FormData formData = com.microsoft.playwright.options.FormData.create();
             for (String pair : payload.split("&")) {
                 String[] kv = pair.split("=");
                 formData.set(kv[0], kv.length > 1 ? kv[1] : "");
             }
             context.setResponse(context.getRequestContext().post(endpoint, com.microsoft.playwright.options.RequestOptions.create()
                     .setHeader("Content-Type", "application/x-www-form-urlencoded")
                     .setForm(formData)));
        } else {
            context.setResponse(petAPI.post(endpoint, payload));
        }
    }

    @Given("update the json path {string} with a random numeric value")
    public void update_the_json_path_with_a_random_numeric_value(String path) {
        String randomId = String.valueOf((int) (Math.random() * 1000000) + 1000);
        String fullPath = path.startsWith("$") ? path : "$." + path;
        payload = APIUtils.updateTheJsonValue(payload, fullPath, randomId);
    }

    @Given("update the json path {string} with a random string value")
    public void update_the_json_path_with_a_random_string_value(String path) {
        String randomStr = "user_" + (int) (Math.random() * 100000);
        String fullPath = path.startsWith("$") ? path : "$." + path;
        payload = APIUtils.updateTheJsonValue(payload, fullPath, randomStr);
    }

    @Given("update the json path {string} with value {string}")
    public void update_the_json_path_with_value(String path, String value) {
        String fullPath = path.startsWith("$") ? path : "$." + path;
        payload = APIUtils.updateTheJsonValue(payload, fullPath, value);
    }

    @When("get request send with endpoint {string}")
    public void getRequestSendWithEndpoint(String endpoint) {
        context.setResponse(petAPI.get(endpoint));
    }

    @When("put request send with endpoint {string}")
    public void putRequestSendWithEndpoint(String endpoint) {
        context.setResponse(petAPI.put(endpoint, payload));
    }

    @When("delete request send with endpoint {string}")
    public void deleteRequestSendWithEndpoint(String endpoint) {
        context.setResponse(petAPI.delete(endpoint));
    }

    @Then("the json path {string} should contain {string}")
    public void theJsonPathShouldContain(String path, String expectedValue) {
        String body = new String(context.getResponse().body(), java.nio.charset.StandardCharsets.UTF_8);
        if (body == null || body.trim().isEmpty()) {
             org.testng.Assert.fail("Response body is empty, cannot check containment.");
        }
        String fullPath = path.startsWith("$") ? path : "$." + path;
        String actualValue = com.jayway.jsonpath.JsonPath.read(body, fullPath).toString();
        org.testng.Assert.assertTrue(actualValue.contains(expectedValue), "Value " + actualValue + " does not contain " + expectedValue);
    }

    @Then("the response field {string} should contain {string}")
    public void theResponseFieldShouldContain(String path, String expectedValue) {
        theJsonPathShouldContain(path, expectedValue);
    }
    @Then("the response field {string} should be {string}")
    public void the_response_field_should_be_str(String path, String expectedValue) {
        String fullPath = path.startsWith("$") ? path : "$." + path;
        APIUtils.validateField(context.getResponse(), fullPath, expectedValue);
    }

    @Then("the response field {string} should be {int}")
    public void the_response_field_should_be_int(String path, int expectedValue) {
        String fullPath = path.startsWith("$") ? path : "$." + path;
        APIUtils.validateField(context.getResponse(), fullPath, expectedValue);
    }

    @Then("the response field {string} should be of type {string}")
    public void the_response_field_should_be_of_type(String path, String type) {
        String fullPath = path.startsWith("$") ? path : "$." + path;
        APIUtils.validateType(context.getResponse(), fullPath, type);
    }

    @Then("the response field {string} should match pattern {string}")
    public void the_response_field_should_match_pattern(String path, String pattern) {
        String fullPath = path.startsWith("$") ? path : "$." + path;
        APIUtils.validateMatches(context.getResponse(), fullPath, pattern);
    }

    @Then("the response array {string} should not be empty")
    public void the_response_array_should_not_be_empty(String path) {
        String body = new String(context.getResponse().body(), java.nio.charset.StandardCharsets.UTF_8);
        String fullPath = path.startsWith("$") ? path : "$." + path;
        java.util.List<?> list = com.jayway.jsonpath.JsonPath.read(body, fullPath);
        org.testng.Assert.assertFalse(list.isEmpty(), "Array at " + path + " is empty!");
    }
}
