package com.fitlogtimer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.SetInWorkoutDTO;
import com.fitlogtimer.dto.SetInWorkoutOutDTO;
import com.fitlogtimer.dto.SetsGroupedFinalDTO;
import com.fitlogtimer.dto.SetsGroupedWithNameDTO;
import com.fitlogtimer.dto.postGroup.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postGroup.SetsSameRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightDTO;
import com.fitlogtimer.dto.workoutDisplay.WorkoutDetailsOutDTO;
import com.fitlogtimer.dto.workoutDisplay.WorkoutGroupedDTO;
import com.fitlogtimer.dto.SetsGroupedDTO;
import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.LastSetDTO;
import com.fitlogtimer.dto.WorkoutInDTO;
import com.fitlogtimer.dto.WorkoutOutDTO;
import com.fitlogtimer.dto.SetBasicDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.WorkoutMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private WorkoutMapper workoutMapper;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAllByOrderByDateDesc();
    }
    
    @Transactional
    public WorkoutOutDTO createWorkout(WorkoutInDTO workoutInDTO) {
        
        Workout savedWorkout = workoutRepository.save(workoutMapper.toWorkout(workoutInDTO));
        
        return new WorkoutOutDTO(savedWorkout.getId(), savedWorkout.getDate(), savedWorkout.getBodyWeight(), savedWorkout.getComment());
    }

    @Transactional
    public boolean deleteWorkout(int workoutId) {
        if (workoutRepository.existsById(workoutId)) {
            workoutRepository.deleteById(workoutId);
            return true;
        }
        return false;
    }

    public WorkoutDetailsOutDTO getWorkoutDetailsBrut(int id){
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Workout not found: " + id));

        List<SetInWorkoutOutDTO> setsDTO = getSetsOutDTO(workout);
        
        return new WorkoutDetailsOutDTO(id, workout.getDate(), workout.getBodyWeight(), workout.getComment(), setsDTO);
    }
    
    public WorkoutGroupedDTO getWorkoutGrouped(int id){
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Workout not found: " + id));

        List<SetsGroupedDTO> groupedSets = groupConsecutiveSetsByExercise(getSetsDTO(workout));

        List<SetsGroupedWithNameDTO> groupedSetsWithName = groupedSets.stream()
                .map(this::groupedSetToGroupedSetWithName)
                .toList();

        List<SetsGroupedFinalDTO> finalGroupedSets = groupedSetsWithName.stream()
                .map(this::cleanSetsGroup)
                .toList();
        
        WorkoutGroupedDTO workoutGroupedDTO = new WorkoutGroupedDTO(id, workout.getDate(), workout.getBodyWeight(), workout.getComment(), finalGroupedSets);
        System.out.println(workoutGroupedDTO);
        return workoutGroupedDTO;
    }
    
    public List<SetsGroupedDTO> groupConsecutiveSetsByExercise(List<SetInWorkoutDTO> exerciseSets){
        List<SetsGroupedDTO> groupedSets = new ArrayList<>();
        List<SetInWorkoutDTO> currentGroup = new ArrayList<>();

        for(int i=0; i < exerciseSets.size(); i++){
            SetInWorkoutDTO currentSet = exerciseSets.get(i);

            if (i==0 || currentSet.exercise_id() == exerciseSets.get(i-1).exercise_id()) {
                currentGroup.add(currentSet);
            } else {
                groupedSets.add(new SetsGroupedDTO(new ArrayList<>(currentGroup)));
                currentGroup.clear();
                currentGroup.add(currentSet);
            }
            System.out.println(i + " after: groupedSets: " + groupedSets);
            System.out.println(i + " after: currentGroup: " + currentGroup);

        }

        if(!currentGroup.isEmpty()){
            groupedSets.add(new SetsGroupedDTO(currentGroup));
        }

        return groupedSets;
    }

    public SetsGroupedWithNameDTO groupedSetToGroupedSetWithName(SetsGroupedDTO entrySet){
        int exerciseId = entrySet.setGroup().get(0).exercise_id();
        String shortName = exerciseRepository.findById(exerciseId)
                        .map(Exercise::getShortName)
                        .orElseThrow(()->new NoSuchElementException("Exercise not found with id: " + exerciseId));

        List<SetBasicDTO> sets = entrySet.setGroup().stream()
            .map(set -> new SetBasicDTO(set.repNumber(), set.weight()))
            .toList();

        return new SetsGroupedWithNameDTO(shortName, sets);        
    }

    public List<SetInWorkoutDTO> getSetsDTO(Workout workout){
        return workout.getSetRecords().stream()
                .map(exerciseSet -> new SetInWorkoutDTO(
                    exerciseSet.getId(),
                    exerciseSet.getExercise().getId(),
                    exerciseSet.getWeight(),
                    exerciseSet.getRepNumber(),
                    exerciseSet.isMax(),
                    exerciseSet.getComment()))
                .collect(Collectors.toList());
    }

    public List<SetInWorkoutOutDTO> getSetsOutDTO(Workout workout){
        return workout.getSetRecords().stream()
                .map(exerciseSet -> new SetInWorkoutOutDTO(
                    exerciseSet.getId(),
                    exerciseSet.getExercise().getShortName(),
                    exerciseSet.getWeight(),
                    exerciseSet.getRepNumber(),
                    exerciseSet.isMax(),
                    exerciseSet.getComment()))
                .collect(Collectors.toList());
    }

    public SetsGroupedFinalDTO cleanSetsGroup(SetsGroupedWithNameDTO sets){
        if(hasSameWeight(sets)){
            if(hasSameReps(sets)){
                return new SetsGroupedFinalDTO(
                    sets.exerciseNameShort(),
                    new SetsSameWeightAndRepsDTO(
                        sets.sets().size(),
                        sets.sets().get(0).repNumber(),
                        sets.sets().get(0).weight()));
            } else{
                List<Integer> repsSet = new ArrayList<>();
                for(int i=0; i<sets.sets().size(); i++){
                    repsSet.add(sets.sets().get(i).repNumber());
                }
                return new SetsGroupedFinalDTO(
                    sets.exerciseNameShort(),
                    new SetsSameWeightDTO(
                        sets.sets().get(0).weight(),
                        repsSet));
            }
        } else if(hasSameReps(sets)){
            List<Double> weights = new ArrayList<>();
            for(int i=0; i<sets.sets().size(); i++){
                weights.add(sets.sets().get(i).weight());
            }
            return new SetsGroupedFinalDTO(
                sets.exerciseNameShort(),
                new SetsSameRepsDTO(
                    sets.sets().get(0).repNumber(),
                    weights
                ));
        } else {
            List<SetBasicDTO> setBasicDTOs = sets.sets().stream()
                .map(set -> new SetBasicDTO(set.repNumber(), set.weight()))
                .collect(Collectors.toList());
                return new SetsGroupedFinalDTO(sets.exerciseNameShort(), new SetsAllDifferentDTO(setBasicDTOs));
        }
    }
    
    public boolean hasSameWeight(SetsGroupedWithNameDTO sets){
        double firstWeight = sets.sets().get(0).weight();

        for(SetBasicDTO set : sets.sets()){
            if(set.weight() != firstWeight){
                return false;
            }
        }
        return true;
    }

    public boolean hasSameReps(SetsGroupedWithNameDTO sets){
        int firstNb = sets.sets().get(0).repNumber();

        for(SetBasicDTO set : sets.sets()){
            if(set.repNumber() != firstNb){
                return false;
            }
        }
        return true;
    }

    public LastSetDTO getLastSetDTO(int id){

        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        if (optionalWorkout.isEmpty()) {return null;}
    
        Workout workout = optionalWorkout.get();
    
        List<ExerciseSet> sets = workout.getSetRecords();
        if (sets == null || sets.isEmpty()) {return null;}
    
        ExerciseSet lastSet = sets.get(sets.size() - 1);
    
        if (lastSet == null || lastSet.getExercise() == null) {return null;}
    
        return new LastSetDTO(
            lastSet.getExercise().getId(),
            lastSet.getExercise().getName(),
            lastSet.getRepNumber(),
            lastSet.getWeight()
        );
    }

    public ExerciseSetInDTO setFormByLastSetDTO(int id) {

        int defaultExerciseId = 1;
        double defaultWeight = 0.0;
        int defaultRepNumber = 0;
        boolean defaultBoolean = false;
        String defaultComment = "";
    
        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        if (optionalWorkout.isEmpty()) {
            return new ExerciseSetInDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBoolean, defaultComment, id);
        }
    
        Workout workout = optionalWorkout.get();
    
        List<ExerciseSet> sets = workout.getSetRecords();
        if (sets == null || sets.isEmpty()) {
            return new ExerciseSetInDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBoolean, defaultComment, id);
        }
    
        ExerciseSet lastSet = sets.get(sets.size() - 1);
    
        if (lastSet == null || lastSet.getExercise() == null) {
            return new ExerciseSetInDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBoolean, defaultComment, id);
        }
    
        return new ExerciseSetInDTO(
            lastSet.getExercise().getId(),
            lastSet.getWeight(),
            lastSet.getRepNumber(),
            defaultBoolean,
            defaultComment,
            id
        );
    }

}
