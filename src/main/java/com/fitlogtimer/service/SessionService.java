package com.fitlogtimer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.ExerciseSetInSessionDTO;
import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionDetailsGroupedDTO;
import com.fitlogtimer.dto.SessionOutDTO;
import com.fitlogtimer.dto.SetInSetsGrouped;
import com.fitlogtimer.dto.SetsGroupedWithNameDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.repository.SessionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public SessionOutDTO saveSession(Session session) {
        Session savedSession = sessionRepository.save(session);
        return new SessionOutDTO(savedSession.getId(), savedSession.getDate(), savedSession.getBodyWeight(), savedSession.getComment());
    }

    // public Session createSessionIfNotExists(Session session) {

    //     Optional<Session> existingSession = sessionRepository.findById(session.getId());
    //     if (existingSession.isPresent()) {
    //         return existingSession.get();
    //     } else {
    //         return sessionRepository.save(session);
    //     }
    // }

    public SessionDetailsDTO getSessionDetails(Long id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<ExerciseSetInSessionDTO> setsDTO = getSetsDTO(session);
        
        return new SessionDetailsDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), setsDTO);
    }

    public SessionDetailsGroupedDTO getSessionDetailsGrouped(Long id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<List<ExerciseSetInSessionDTO>> groupedSetsDTO = groupConsecutiveSetsByExercise(getSetsDTO(session));
        
        return new SessionDetailsGroupedDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), groupedSetsDTO);
    }
    
    public List<List<ExerciseSetInSessionDTO>> groupConsecutiveSetsByExercise(List<ExerciseSetInSessionDTO> exerciseSets){
        List<List<ExerciseSetInSessionDTO>> groupedSets = new ArrayList<>();
        List<ExerciseSetInSessionDTO> currentGroup = new ArrayList<>();

        for(int i=0; i < exerciseSets.size(); i++){
            ExerciseSetInSessionDTO currentSet = exerciseSets.get(i);
            if (i==0 || currentSet.exercise_id() == exerciseSets.get(i-1).exercise_id()) {
                currentGroup.add(currentSet);
            } else {
                groupedSets.add(new ArrayList<>(currentGroup));
                currentGroup.clear();
                currentGroup.add(currentSet);
            }
        }

        if(!currentGroup.isEmpty()){
            groupedSets.add(currentGroup);
        }

        return groupedSets;
    }

    // public List<SetsGroupedWithNameDTO> groupSetsWithExerciseName(List<ExerciseSetInSessionDTO> exerciseSets, Map<Long, String> exerciseNames) {
    // List<SetsGroupedWithNameDTO> groupedSetsWithName = new ArrayList<>();
    // List<SetInSetsGrouped> currentGroup = new ArrayList<>();
    // Long currentExerciseId = null;

    // // Parcours de la liste des sets
    // for (ExerciseSetInSessionDTO currentSet : exerciseSets) {
    //     // Si c'est le premier set ou si l'exercice a changé
    //     if (currentExerciseId == null || !currentExerciseId.equals(currentSet.exercise_id())) {
    //         // Si un groupe existe déjà, on l'ajoute à la liste
    //         if (!currentGroup.isEmpty()) {
    //             groupedSetsWithName.add(new SetsGroupedWithNameDTO(currentExerciseId.intValue(), new ArrayList<>(currentGroup)));
    //         }
    //         // Démarrer un nouveau groupe avec le nouvel exercice
    //         currentExerciseId = currentSet.exercise_id();
    //         currentGroup.clear();
    //     }

    //     // Ajouter le set au groupe actuel
    //     currentGroup.add(new SetInSetsGrouped(currentSet.getId(), currentSet.getWeight(), 
    //             currentSet.getRepNumber(), currentSet.isMax(), currentSet.getComment()));
    // }

    // // Ajouter le dernier groupe s'il y en a un
    // if (!currentGroup.isEmpty()) {
    //     groupedSetsWithName.add(new SetsGroupedWithNameDTO(currentExerciseId.intValue(), new ArrayList<>(currentGroup)));
    // }

    // return groupedSetsWithName;
//}


    public List<ExerciseSetInSessionDTO> getSetsDTO(Session session){
        return session.getSetRecords().stream()
                .map(exerciseSet -> new ExerciseSetInSessionDTO(
                    exerciseSet.getId(),
                    exerciseSet.getExercise().getId(),
                    exerciseSet.getWeight(),
                    exerciseSet.getRepNumber(),
                    exerciseSet.isMax(),
                    exerciseSet.getComment()))
                .collect(Collectors.toList());
    }
}
