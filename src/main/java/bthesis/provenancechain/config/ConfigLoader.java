package bthesis.provenancechain.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigLoader {
    public static Configuration loadConfig() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File("src/main/resources/configuration.json"), Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
