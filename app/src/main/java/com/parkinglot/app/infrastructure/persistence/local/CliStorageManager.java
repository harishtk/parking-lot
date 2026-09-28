package com.parkinglot.app.infrastructure.persistence.local;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CliStorageManager {

    private final Path appStorageDir;
    private final ObjectMapper objectMapper;

    public CliStorageManager(String appName) {
        this.appStorageDir = OS.current().getAppDataPath(appName);
        ensureDirectoryExists();

        this.objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    private void ensureDirectoryExists() {
        try {
            if (!Files.exists(appStorageDir)) {
                Files.createDirectories(appStorageDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create application storage directory: " + appStorageDir, e);
        }
    }

    public void saveData(String fileName, Object jsonObject) throws IOException {
        Path targetFile = appStorageDir.resolve(fileName);
        objectMapper.writeValue(targetFile.toFile(), jsonObject);
        System.out.println("Saved data to: file:///" + targetFile.toAbsolutePath().toString());
    }

    public <T> T loadData(String fileName, Class<T> clazz) throws IOException {
        Path targetFile = appStorageDir.resolve(fileName);
        if (!Files.exists(targetFile)) {
            return null;
        }
        return objectMapper.readValue(targetFile.toFile(), clazz);
    }

    public boolean deleteData(String fileName) throws IOException {
        Path targetFile = appStorageDir.resolve(fileName);
        return Files.deleteIfExists(targetFile);
    }

    public Path getStorageDirectory() {
        return appStorageDir;
    }
}
