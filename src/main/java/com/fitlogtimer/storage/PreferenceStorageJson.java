package com.fitlogtimer.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fitlogtimer.model.preference.ExerciseListPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PreferenceStorageJson {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final Path filePath = Paths.get("data/exercise_preferences.json");

    public Map<String, ExerciseListPreference> load() throws IOException {
        if (!Files.exists(filePath)) return new HashMap<>();
        log.info("Loading preferences from file: {}", filePath);
        return mapper.readValue(Files.newBufferedReader(filePath), new TypeReference<>() {});
    }

    public void saveAll(Map<String, ExerciseListPreference> lists) throws IOException {
        log.info("Saving all preferences to file: {}", filePath);
        mapper.writeValue(filePath.toFile(), lists);
    }

    public void saveList(ExerciseListPreference list) throws IOException {
        Map<String, ExerciseListPreference> all = load();
        all.put(list.getName(), list);
        saveAll(all);
    }

    public ExerciseListPreference get(String name) throws IOException {
        return load().get(name);
    }
}

