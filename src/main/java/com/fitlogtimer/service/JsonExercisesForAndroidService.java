package com.fitlogtimer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlogtimer.dto.listitem.WorkoutTypeListItemDTO;
import com.fitlogtimer.dto.preference.AndroidExportDTO;
import com.fitlogtimer.dto.preference.ExercisePreferenceDTO;
import com.fitlogtimer.dto.preference.JsonExerciseForAndroidDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.sets.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsonExercisesForAndroidService {

    private final ExercisePreferenceService exercisePreferenceService;
    private final ExerciseSetService exerciseSetService;
    private final ExerciseService exerciseService;
    private final WorkoutTypeService workoutTypeService;
    private final GoogleDriveService googleDriveService;
    private final WorkoutService workoutService;

    public List<JsonExerciseForAndroidDTO> createJsonExerciseForAndroidList() throws IOException {
        List<ExercisePreferenceDTO> exercisePreferenceDTOList = exercisePreferenceService.getDefaultPreferenceDTOs();

        List<Integer> exerciseIds = exercisePreferenceDTOList.stream()
                .map(ExercisePreferenceDTO::exerciseId)
                .toList();

        Map<Integer, Exercise> exerciseMap = exerciseService.getAllExercises().stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        Map<Integer, ExerciseSet> lastSets = exerciseSetService.findLastSetsForExerciseIds(exerciseIds).stream()
                .collect(Collectors.toMap(es -> es.getExercise().getId(), es -> es));

        List<JsonExerciseForAndroidDTO> result = new ArrayList<>();
        for (int i = 0; i < exercisePreferenceDTOList.size(); i++) {
            ExercisePreferenceDTO exercisePreferenceDTO = exercisePreferenceDTOList.get(i);
            Exercise exercise = exerciseMap.get(exercisePreferenceDTO.exerciseId());

            ExerciseSet lastSet = lastSets.get(exercisePreferenceDTO.exerciseId());

            double defaultWeight = 0.0;
            int defaultReps = 0;
            String defaultBands = "";
            int defaultDurationS = 0;
            String defaultDistance = "";

            if(lastSet != null) {
                defaultReps = lastSet.getRepNumber();
                if(lastSet instanceof FreeWeightSet freeWeightSet) {
                    defaultWeight = freeWeightSet.getWeight() != null ? freeWeightSet.getWeight() : 0.0;
                }
                if(lastSet instanceof ElasticSet elasticSet) {
                    defaultBands = elasticSet.getBands() != null ? elasticSet.getBands() : "";
                }
                if(lastSet instanceof IsometricSet isometricSet) {
                    defaultDurationS = isometricSet.getDurationS();
                    defaultWeight = isometricSet.getWeight() != null ? isometricSet.getWeight() : 0.0;
                }
                if (lastSet instanceof BodyweightSet bodyweightSet) {
                    defaultWeight = bodyweightSet.getWeight() != null ? bodyweightSet.getWeight() : 0.0;
                    defaultBands = bodyweightSet.getBands() != null ? bodyweightSet.getBands() : "";
                }
                if (lastSet instanceof MovementSet movementSet) {
                    defaultDistance = movementSet.getDistance() != null ? movementSet.getDistance() : "";
                    defaultBands = movementSet.getBands() != null ? movementSet.getBands() : "";
                    defaultWeight = movementSet.getWeight() != null ? movementSet.getWeight() : 0.0;
                }
            }

            result.add(new JsonExerciseForAndroidDTO(
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getShortName(),
                    exercisePreferenceDTO.order(),
                    defaultWeight,
                    defaultReps,
                    defaultBands,
                    defaultDurationS,
                    defaultDistance,
                    exercise.getType()
            ));
        }

        return result;
    }

    public LinkedHashSet<String> getWorkoutTypeNamesByRecent() {
        return new LinkedHashSet<>(workoutService.listWorkoutTypesByRecent());
    }

    public String createJsonForAndroid() throws IOException {

        List<JsonExerciseForAndroidDTO> exercises = createJsonExerciseForAndroidList();

        LinkedHashSet<String> workoutTypeNames = getWorkoutTypeNamesByRecent();

        List<Map<String, String>> workoutTypesFormatted = workoutTypeNames.stream()
                .map(typeName -> {
                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put("name", typeName);
                    return typeMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> androidExport = new HashMap<>();
        androidExport.put("exercises", exercises);
        androidExport.put("workoutTypes", workoutTypesFormatted);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(androidExport);
        log.info("Json for Android: {}", json);
        return json;
    }
}
