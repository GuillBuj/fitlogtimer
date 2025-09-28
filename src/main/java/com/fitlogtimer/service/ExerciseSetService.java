package com.fitlogtimer.service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicElasticDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.dto.base.SetBasicIsometricDTO;
import com.fitlogtimer.dto.base.SetBasicMovementDTO;
import com.fitlogtimer.dto.base.SetBasicWith1RMDTO;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanExerciseListItemDTO;
import com.fitlogtimer.dto.transition.SetsGroupedByWorkoutDTO;
import com.fitlogtimer.dto.transition.SetsGroupedForExDTO;
import com.fitlogtimer.dto.transition.SetsGroupedWithNameDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.ExerciseSetFacadeMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.BodyweightSet;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.model.sets.IsometricSet;
import com.fitlogtimer.model.sets.MovementSet;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ExerciseSetService {
    
    private final ExerciseSetRepository exerciseSetRepository;

    private final ExerciseRepository exerciseRepository;

    private final WorkoutRepository workoutRepository;

    private final ExerciseSetFacadeMapper exerciseSetFacadeMapper;

    private final SetsGroupCleanerPlusService setsGroupCleanerPlusService;

    
    @Transactional
    public ExerciseSet saveExerciseSet(@Valid ExerciseSetCreateDTO exerciseSetDTO) {
        log.info("*-*-* exerciseSetDTO à ajouter: {}", exerciseSetDTO);
        ExerciseSet exerciseSet = exerciseSetFacadeMapper.toEntity(exerciseSetDTO);
        log.info("*-*-* exerciseSet à ajouter post-mapping: {}", exerciseSet);
        return exerciseSetRepository.save(exerciseSet);
    }

    @Transactional
    public boolean deleteExerciseSet(int id){
        
        if (exerciseSetRepository.existsById(id)) {
            exerciseSetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<ExerciseSet> getSetsByExerciseId(int id){
        log.info("*-*-* Sets: {}", exerciseSetRepository.findByExerciseIdOrderByWorkoutDateAndIdDesc(id));
        return exerciseSetRepository.findByExerciseIdOrderByWorkoutDateAndIdDesc(id);
    }

    public SetsGroupedByWorkoutDTO getSetsGroupedByWorkout(int id){
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(()->new NotFoundException("Exercise not found: " + id));

        List<SetsGroupedForExDTO> groupedSets = groupSetsByWorkout(getSetsByExerciseId(id));

        return new SetsGroupedByWorkoutDTO(id, exercise.getName(), groupedSets); 
    }

    //groupe par séance à partir d'une liste d'exercices
    public List<SetsGroupedForExDTO> groupSetsByWorkout(List<ExerciseSet> exerciseSets) {
        
        List<SetsGroupedForExDTO> groupedSets = new ArrayList<>();
        List<SetBasicInterfaceDTO> currentGroup = new ArrayList<>();
    
        if (exerciseSets.isEmpty()) {
            log.info("EMPTY SETS");
            return groupedSets;
        }
    
        int currentWorkoutId = exerciseSets.get(0).getWorkout().getId();
    
        for (ExerciseSet currentSet : exerciseSets) {
            int workoutId = currentSet.getWorkout().getId();
    
            if (workoutId != currentWorkoutId) {
                groupedSets.add(new SetsGroupedForExDTO(currentWorkoutId, new ArrayList<>(currentGroup)));
                currentGroup.clear();
                currentWorkoutId = workoutId;
            }
            if (currentSet instanceof FreeWeightSet freeWeightSet) {
                int repNumber = freeWeightSet.getRepNumber();
                double weight = freeWeightSet.getWeight();
                currentGroup.add(new SetBasicWith1RMDTO(repNumber, weight, StatsService.calculateOneRepMax(repNumber, weight)));
            }
            if(currentSet instanceof ElasticSet elasticSet){
                int repNumber = elasticSet.getRepNumber();
                String bands = elasticSet.getBands();
                currentGroup.add(new SetBasicElasticDTO(repNumber, bands));
            }
            if(currentSet instanceof IsometricSet isometricSet){
                int durationS = isometricSet.getDurationS();
                int repNumber = isometricSet.getRepNumber();
                double weight = isometricSet.getWeight();
                currentGroup.add(new SetBasicIsometricDTO(durationS, repNumber, weight));
            }
            if (currentSet instanceof BodyweightSet bodyweightSet) {
                int repNumber = bodyweightSet.getRepNumber();
                double weight = bodyweightSet.getWeight();
                currentGroup.add(new SetBasicDTO(repNumber, weight));
            }
            if (currentSet instanceof MovementSet movementSet) {
                int repNumber = movementSet.getRepNumber();
                double weight = movementSet.getWeight();
                String distance = movementSet.getDistance();
                String bands = movementSet.getBands();
                currentGroup.add(new SetBasicMovementDTO(repNumber, distance, bands, weight));
            }
        }
    
        if (!currentGroup.isEmpty()) {
            groupedSets.add(new SetsGroupedForExDTO(currentWorkoutId, currentGroup));
        }
    
        return groupedSets;
    }
    
    //set max 1RMest dans une liste de sets groupés par séance
    public SetBasicWith1RMDTO getMaxByDateForEx(List<SetBasicWith1RMDTO> setsGroupedByWorkout) {
        return setsGroupedByWorkout.stream()
                .max(Comparator.comparingDouble(SetBasicWith1RMDTO::oneRepMax))
                .orElseThrow(() -> new IllegalArgumentException("No sets available"));
    }

    public ExerciseDetailsGroupedDTO getSetsGroupedCleanedByWorkout(int id, Set<String> types) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exercise not found: " + id));
    
        List<SetsGroupedForExDTO> groupedSets = groupSetsByWorkout(getSetsByExerciseId(id));
    
        List<SetGroupCleanExerciseListItemDTO> finalGroupedSets = groupedSets.stream()
            .map(group -> {
                Workout workout = workoutRepository.findById(group.idWorkout())
                        .orElseThrow(() -> new NotFoundException("Workout not found: " + group.idWorkout()));
    
                SetsGroupedWithNameDTO setsGrouped = new SetsGroupedWithNameDTO(
                    exercise.getShortName(),
                    new ArrayList<>(group.setGroup())
                );
    
                return setsGroupCleanerPlusService.cleanWithMeta(
                    workout.getDate(),
                    workout.getBodyWeight(),
                    workout.getTypeName(),
                    setsGrouped,
                    types
            );
            })
            .filter(dto -> dto.sets() != null)
            .collect(Collectors.toList());
    
        return new ExerciseDetailsGroupedDTO(id, exercise.getName(), exercise.getType(), finalGroupedSets);
    }

    public Set<String> extractTypes(ExerciseDetailsGroupedDTO exerciseDetailsGroupedDTO){
        return exerciseDetailsGroupedDTO.exerciseSets().stream()
            .map(SetGroupCleanExerciseListItemDTO::type)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public List<ExerciseSet> findLastSetsForExerciseIds(List<Integer> exerciseIds){
        return exerciseSetRepository.findLastSetsForExerciseIds(exerciseIds);
    }
}
