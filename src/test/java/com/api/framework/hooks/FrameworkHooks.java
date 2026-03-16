package com.api.framework.hooks;

import com.api.framework.base.BaseAPI;
import com.api.framework.config.ConfigReader;
import com.api.framework.config.ScenarioContext;
import com.api.framework.config.Settings;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class FrameworkHooks extends BaseAPI {

    private final ScenarioContext context;
    private static boolean resultsCleaned = false;

    public FrameworkHooks(ScenarioContext context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        if (!resultsCleaned) {
            deleteDirectory(new java.io.File("allure-results"));
            resultsCleaned = true;
        }
        ConfigReader.PopulateSettings();
        createRequestContext(context, Settings.Url);
    }

    private void deleteDirectory(java.io.File directoryToBeDeleted) {
        if (!directoryToBeDeleted.exists()) return;
        java.io.File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (java.io.File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    @After
    public void tearDown(Scenario scenario) {
        closeRequestContext(context);
    }
}
