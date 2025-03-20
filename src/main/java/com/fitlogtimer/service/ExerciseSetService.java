package com.fitlogtimer.service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.ExerciseSetOutDTO;
import com.fitlogtimer.dto.SetBasicDTO;
import com.fitlogtimer.dto.SetBasicWith1RMDTO;
import com.fitlogtimer.dto.SetMaxByDateForExDTO;
import com.fitlogtimer.dto.SetsByExGroupedDTO;
import com.fitlogtimer.dto.SetsGroupedBySessionDTO;
import com.fitlogtimer.dto.SetsGroupedFinalForExDTO;
import com.fitlogtimer.dto.SetsGroupedForExDTO;
import com.fitlogtimer.dto.postGroup.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postGroup.SetsSameRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.SessionRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ExerciseSetService {
    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SessionRepository sessionRepository;


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ExerciseSetOutDTO saveExerciseSet(ExerciseSetInDTO exerciseSetDTO) {
        int exerciseId = exerciseSetDTO.exercise_id();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise with ID " + exerciseId + " does not exist"));
        int sessionId = exerciseSetDTO.session_id();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session with ID " + sessionId + " does not exist"));

        ExerciseSet exerciseSet = new ExerciseSet();
        exerciseSet.setExercise(exercise);
        exerciseSet.setWeight(exerciseSetDTO.weight());
        exerciseSet.setRepNumber(exerciseSetDTO.repNumber());
        exerciseSet.setMax(exerciseSetDTO.isMax());
        exerciseSet.setComment(exerciseSetDTO.comment());
        exerciseSet.setSession(session);

        ExerciseSet savedExercise = exerciseSetRepository.save(exerciseSet);
        return new ExerciseSetOutDTO(
            savedExercise.getId(),
            savedExercise.getExercise().getId(),
            savedExercise.getWeight(),
            savedExercise.getRepNumber(),
            savedExercise.isMax(),
            savedExercise.getComment(),
            savedExercise.getSession().getId()
        );
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
        return exerciseSetRepository.findByExerciseIdOrderBySessionDateAndIdDesc(id);
    }

    public SetsGroupedBySessionDTO getSetsGroupedBySession(int id){
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(()->new NotFoundException("Exercise not found: " + id));

        List<SetsGroupedForExDTO> groupedSets = groupSetsBySession(getSetsByExerciseId(id));

        return new SetsGroupedBySessionDTO(id, exercise.getName(), groupedSets); 
    }

    public SetBasicWith1RMDTO getMaxByDateForEx(List<SetsGroupedForExDTO> sets) {
        return sets.stream()
                .flatMap(group -> group.setGroup().stream())
                .max(Comparator.comparingDouble(SetBasicWith1RMDTO::oneRepMax))
                .orElseThrow(() -> new IllegalArgumentException("No sets available"));
    }

    public SetsByExGroupedDTO getSetsGroupedCleanedBySession(int id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exercise not found: " + id));
    
        List<SetsGroupedForExDTO> groupedSets = groupSetsBySession(getSetsByExerciseId(id));
    
        List<SetsGroupedFinalForExDTO> finalGroupedSets = groupedSets.stream()
                .map(group -> {
                    Session session = sessionRepository.findById(group.idSession())
                            .orElseThrow(() -> new NotFoundException("Session not found: " + group.idSession()));

                    List<SetBasicDTO> basicSets = group.setGroup().stream()
                            .map(set -> new SetBasicDTO(set.repNumber(), set.weight()))
                            .collect(Collectors.toList());
                    
                    Object cleanedSets = cleanSetsGroup(basicSets);
    
                    return new SetsGroupedFinalForExDTO(
                            session.getDate(),
                            session.getBodyWeight(),
                            session.getComment(),
                            cleanedSets
                    );
                })
                .collect(Collectors.toList());
    
        return new SetsByExGroupedDTO(id, exercise.getName(), finalGroupedSets);
    }
    
    public List<SetsGroupedForExDTO> groupSetsBySession(List<ExerciseSet> exerciseSets) {
        List<SetsGroupedForExDTO> groupedSets = new ArrayList<>();
        List<SetBasicWith1RMDTO> currentGroup = new ArrayList<>();
    
        if (exerciseSets.isEmpty()) {
            return groupedSets;
        }
    
        int currentSessionId = exerciseSets.get(0).getSession().getId();
    
        for (ExerciseSet currentSet : exerciseSets) {
            int sessionId = currentSet.getSession().getId();
    
            if (sessionId != currentSessionId) {
                groupedSets.add(new SetsGroupedForExDTO(currentSessionId, new ArrayList<>(currentGroup)));
                currentGroup.clear();
                currentSessionId = sessionId;
            }
            int repNumber = currentSet.getRepNumber();
            double weight = currentSet.getWeight();
            
            currentGroup.add(new SetBasicWith1RMDTO(repNumber, weight, StatsService.calculateOneRepMax(repNumber, weight)));
        }
    
        if (!currentGroup.isEmpty()) {
            groupedSets.add(new SetsGroupedForExDTO(currentSessionId, currentGroup));
        }
    
        return groupedSets;
    }

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
