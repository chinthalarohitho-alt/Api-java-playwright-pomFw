package com.api.framework.base;

import com.api.framework.config.ScenarioContext;
import com.microsoft.playwright.*;

import java.util.HashMap;
import java.util.Map;

public class BaseAPI {

    public void createRequestContext(ScenarioContext context, String baseUrl) {
        Playwright playwright = Playwright.create();
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIRequestContext requestContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(headers)
        );
        
        context.setPlaywright(playwright);
        context.setRequestContext(requestContext);
    }

    public void closeRequestContext(ScenarioContext context) {
        try {
            if (context.getRequestContext() != null) {
                context.getRequestContext().dispose();
            }
        } catch (Exception e) { /* Ignore */ }

        try {
            if (context.getPlaywright() != null) {
                context.getPlaywright().close();
            }
        } catch (Exception e) { /* Ignore */ }
    }
}
