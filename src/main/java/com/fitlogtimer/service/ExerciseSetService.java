package com.fitlogtimer.service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicWith1RMDTO;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanExerciseListItemDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postgroup.SetsSameRepsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameWeightDTO;
import com.fitlogtimer.dto.transition.SetsGroupedByWorkoutDTO;
import com.fitlogtimer.dto.transition.SetsGroupedForExDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.ExerciseSetMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;
import com.fitlogtimer.util.helper.SetBasicConverter;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

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

    private final ExerciseSetMapper exerciseSetMapper;
    
    private final ExerciseSetMappingHelper exerciseSetMappingHelper;
    private final SetBasicConverter setBasicConverter;

    
    @Transactional
    public ExerciseSet saveExerciseSet(ExerciseSetCreateDTO exerciseSetDTO) {
        
        ExerciseSet exerciseSet = exerciseSetMapper.toEntity(exerciseSetDTO, exerciseSetMappingHelper);
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
    public ExerciseDetailsGroupedDTO getSetsGroupedCleanedByWorkout(int id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exercise not found: " + id));
    
        List<SetsGroupedForExDTO> groupedSets = groupSetsByWorkout(getSetsByExerciseId(id));
    
        List<SetGroupCleanExerciseListItemDTO> finalGroupedSets = groupedSets.stream()
                .map(group -> {
                    Workout workout = workoutRepository.findById(group.idWorkout())
                            .orElseThrow(() -> new NotFoundException("Workout not found: " + group.idWorkout()));

                    List<SetBasicDTO> basicSets = setBasicConverter.convertSetBasicDTOList(group.setGroup());
    
                    Object cleanedSets = cleanSetsGroup(basicSets);
    
                    SetBasicWith1RMDTO maxSet = getMaxByDateForEx(group.setGroup());
    
                    return new SetGroupCleanExerciseListItemDTO(
                            workout.getDate(),
                            workout.getBodyWeight(),
                            workout.getComment(),
                            cleanedSets,
                            maxSet.oneRepMax()
                    );
                })
                .collect(Collectors.toList());
    
        return new ExerciseDetailsGroupedDTO(id, exercise.getName(), finalGroupedSets);
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
