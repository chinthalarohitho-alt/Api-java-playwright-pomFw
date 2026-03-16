package com.api.framework.config;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads configuration using OWNER library and populates Settings class.
 */
public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private final EnvironmentConfig envConfig;

    public ConfigReader() {
        // Create config instance
        this.envConfig = ConfigFactory.create(EnvironmentConfig.class);
    }

    /**
     * Populates static Settings class with configuration values.
     * Call this once before test execution.
     */
    public static synchronized void PopulateSettings() {
        ConfigReader reader = new ConfigReader();
        reader.readAndPopulate();
    }

    /**
     * Reads configuration and populates Settings class.
     */
    private void readAndPopulate() {
        try {
            logger.info("Loading configuration...");

            // Environment Configuration
            Settings.Url = envConfig.Url();
            Settings.Username = envConfig.username();
            Settings.Password = envConfig.password();

            logger.info("✅ Configuration loaded successfully");
            logger.info("Environment: {}, URL: {}", Settings.EnvName, Settings.Url);

        } catch (Exception e) {
            logger.error("❌ Failed to load configuration", e);
            throw new RuntimeException("Configuration loading failed: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the EnvironmentConfig instance for direct access.
     */
    public EnvironmentConfig getConfig() {
        return envConfig;
    }
}
