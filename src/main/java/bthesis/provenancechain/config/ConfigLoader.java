package bthesis.provenancechain.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides utility for loading configuration settings from a JSON file.
 *
 * @author Tomas Zobac
 */
public class ConfigLoader {
    /**
     * Loads the application configuration from the specified JSON file.
     *
     * @return An instance of {@link Configuration} containing the loaded settings.
     * @throws RuntimeException if there's an error reading the configuration file.
     */
    public static Configuration loadConfig() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File("src/main/resources/configuration.json"), Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
