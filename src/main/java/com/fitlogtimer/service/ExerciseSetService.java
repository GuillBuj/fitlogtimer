package com.fitlogtimer.service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.ExerciseSetOutDTO;
import com.fitlogtimer.dto.SetBasicDTO;
import com.fitlogtimer.dto.SetBasicWith1RMDTO;
import com.fitlogtimer.dto.SetsByExGroupedDTO;
import com.fitlogtimer.dto.SetsGroupedByWorkoutDTO;
import com.fitlogtimer.dto.SetsGroupedFinalForExDTO;
import com.fitlogtimer.dto.SetsGroupedForExDTO;
import com.fitlogtimer.dto.postGroup.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postGroup.SetsSameRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.ExerciseSetMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExerciseSetService {
    
    private final ExerciseSetRepository exerciseSetRepository;

    private final ExerciseRepository exerciseRepository;

    private final WorkoutRepository workoutRepository;

    private final ExerciseSetMapper exerciseSetMapper;

    // @PersistenceContext
    // private EntityManager entityManager;

    @Transactional
    public ExerciseSetOutDTO saveExerciseSet(ExerciseSetInDTO exerciseSetDTO) {
        int exerciseId = exerciseSetDTO.exercise_id();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise with ID " + exerciseId + " does not exist"));
        int workoutId = exerciseSetDTO.workout_id();
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout with ID " + workoutId + " does not exist"));

        ExerciseSet exerciseSet = new ExerciseSet();
        exerciseSet.setExercise(exercise);
        exerciseSet.setWeight(exerciseSetDTO.weight());
        exerciseSet.setRepNumber(exerciseSetDTO.repNumber());
        exerciseSet.setMax(exerciseSetDTO.isMax());
        exerciseSet.setComment(exerciseSetDTO.comment());
        exerciseSet.setWorkout(workout);

        ExerciseSet savedExercise = exerciseSetRepository.save(exerciseSet);
        return exerciseSetMapper.toExerciseSetOutDTO(savedExercise);
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
        List<SetBasicWith1RMDTO> currentGroup = new ArrayList<>();
    
        if (exerciseSets.isEmpty()) {
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
            int repNumber = currentSet.getRepNumber();
            double weight = currentSet.getWeight();
            
            currentGroup.add(new SetBasicWith1RMDTO(repNumber, weight, StatsService.calculateOneRepMax(repNumber, weight)));
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

    //affichage propre
    public SetsByExGroupedDTO getSetsGroupedCleanedByWorkout(int id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exercise not found: " + id));
    
        List<SetsGroupedForExDTO> groupedSets = groupSetsByWorkout(getSetsByExerciseId(id));
    
        List<SetsGroupedFinalForExDTO> finalGroupedSets = groupedSets.stream()
                .map(group -> {
                    Workout workout = workoutRepository.findById(group.idWorkout())
                            .orElseThrow(() -> new NotFoundException("Workout not found: " + group.idWorkout()));

                    List<SetBasicDTO> basicSets = exerciseSetMapper.toListSetBasicDTO(group.setGroup());
    
                    Object cleanedSets = cleanSetsGroup(basicSets);
    
                    SetBasicWith1RMDTO maxSet = getMaxByDateForEx(group.setGroup());
    
                    return new SetsGroupedFinalForExDTO(
                            workout.getDate(),
                            workout.getBodyWeight(),
                            workout.getComment(),
                            cleanedSets,
                            maxSet.oneRepMax()
                    );
                })
                .collect(Collectors.toList());
    
        return new SetsByExGroupedDTO(id, exercise.getName(), finalGroupedSets);
    }
    
    //renvoie une version propre d'une liste de sets pour affichage
    public Object cleanSetsGroup(List<SetBasicDTO> sets){
        
        if(hasSameWeight(sets)){
            if(hasSameReps(sets)){
                return new SetsSameWeightAndRepsDTO(sets.size(), sets.get(0).repNumber(), sets.get(0).weight());
            } else{
                List<Integer> repsSet = new ArrayList<>();
                for(int i=0; i<sets.size(); i++){
                    repsSet.add(sets.get(i).repNumber());
                }
                return new SetsSameWeightDTO(sets.get(0).weight(), repsSet);
            }
        } else if(hasSameReps(sets)){
            List<Double> weights = new ArrayList<>();
            for(int i=0; i<sets.size(); i++){
                weights.add(sets.get(i).weight());
            }
            return new SetsSameRepsDTO(sets.get(0).repNumber(), weights);
        } else {
            List<SetBasicDTO> setBasicDTOs = sets.stream()
                .map(set -> new SetBasicDTO(set.repNumber(), set.weight()))
                .collect(Collectors.toList());
                return new SetsAllDifferentDTO(setBasicDTOs);
        }
    }

    public boolean hasSameWeight(List<SetBasicDTO> sets){
        double firstWeight = sets.get(0).weight();

        for(SetBasicDTO set : sets){
            if(set.weight() != firstWeight){
                return false;
            }
        }
        return true;
    }

    public boolean hasSameReps(List<SetBasicDTO> sets){
        int firstNb = sets.get(0).repNumber();

        for(SetBasicDTO set : sets){
            if(set.repNumber() != firstNb){
                return false;
            }
        }
        return true;
    }

}
