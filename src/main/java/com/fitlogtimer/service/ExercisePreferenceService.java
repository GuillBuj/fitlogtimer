package com.fitlogtimer.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlogtimer.dto.preference.ExercisePreferenceDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.preference.ExerciseListPreference;
import com.fitlogtimer.model.preference.ExercisePreference;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.storage.PreferenceStorageJson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ExercisePreferenceService {

    private final ExerciseRepository exerciseRepository;
    private final PreferenceStorageJson preferenceStorage;

    public ExerciseListPreference getListByName(String name) throws IOException {
        log.info("* Getting preferences list {}", name);

        return preferenceStorage.get(name);
    }

    public void saveList(String name, List<ExercisePreference> preferences) throws IOException {
        log.info("* Saving preferences list {}", name);

        ExerciseListPreference list = new ExerciseListPreference(name, preferences);
        preferenceStorage.saveList(list);
    }

    public List<Exercise> getVisibleExercises(String listName) throws IOException {
        log.info("* Getting visible exercises from {} preferences list", listName);

        ExerciseListPreference preferenceList = preferenceStorage.get(listName);
        if (preferenceList == null) return List.of();

        Map<Integer, Exercise> allMap = exerciseRepository.findAll().stream()
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));

        return preferenceList.getExercises().stream()
                .filter(ExercisePreference::isVisible)
                .sorted(Comparator.comparingInt(ExercisePreference::getOrder))
                .map(pref -> allMap.get(pref.getExerciseId()))
                .filter(Objects::nonNull)
                .toList();
    }

    public void updateAllExercisesList() throws IOException {
        log.info("* Updating preferences list with all exercises");

        Map<String, ExerciseListPreference> lists = preferenceStorage.load();
        List<Exercise> allExercises = exerciseRepository.findAll();

        ExerciseListPreference listAll = lists.getOrDefault("all", new ExerciseListPreference("all", new ArrayList<>()));
        Set<Integer> existingIds = listAll.getExercises().stream()
                .map(ExercisePreference::getExerciseId)
                .collect(Collectors.toSet());

        int nextOrder = listAll.getExercises().stream()
                .mapToInt(ExercisePreference::getOrder)
                .max()
                .orElse(0) + 1;

        for (Exercise exercise : allExercises) {
            if (!existingIds.contains(exercise.getId())) {
                listAll.getExercises().add(new ExercisePreference(exercise.getId(), nextOrder++, true));
            }
        }

        lists.put("all", listAll);
        preferenceStorage.saveAll(lists);
    }

    public List<Exercise> getAllExercises() {
        log.info("* Getting all exercises");

        return exerciseRepository.findAll();
    }

    public List<ExercisePreferenceDTO> getDefaultPreferenceDTOs() {
        log.info("* Getting default ExercisePreferenceDTOs");

        List<Exercise> allExercises = exerciseRepository.findAll();

        List<ExercisePreferenceDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < allExercises.size(); i++) {
            Exercise exercise = allExercises.get(i);
            ExercisePreferenceDTO dto = new ExercisePreferenceDTO(
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getShortName(),
                    exercise.getColor(),
                    true,
                    i
            );
            dtoList.add(dto);
        }

        return dtoList;
    }

    public void createNewList(String listName, List<Integer> exerciseIds, List<Integer> visibleIds) throws IOException {
        log.info("* Creating new preferences list: {}", listName);

        Set<Integer> visibleSet = visibleIds != null
                ? new HashSet<>(visibleIds)
                : Collections.emptySet();

        List<ExercisePreference> preferences = new ArrayList<>();

        for (int order = 0; order < exerciseIds.size(); order++) {
            int id = exerciseIds.get(order);
            boolean visible = visibleSet.contains(id);
            preferences.add(new ExercisePreference(id, order, visible));
        }

        Map<String, ExerciseListPreference> allLists = preferenceStorage.load();
        allLists.put(listName, new ExerciseListPreference(listName, preferences));
        preferenceStorage.saveAll(allLists);
    }
}
