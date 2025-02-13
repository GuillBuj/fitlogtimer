package com.fitlogtimer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.SetInSessionDTO;
import com.fitlogtimer.dto.SetInSetsGroupedDTO;
import com.fitlogtimer.dto.SetsGroupedFinalDTO;
import com.fitlogtimer.dto.SetsGroupedWithNameDTO;
import com.fitlogtimer.dto.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.SetGroupedDTO;
import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionDetailsGroupedDTO;
import com.fitlogtimer.dto.SessionGroupedDTO;
import com.fitlogtimer.dto.SessionOutDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.SessionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

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

    public SessionDetailsDTO getSessionDetails(int id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetInSessionDTO> setsDTO = getSetsDTO(session);
        
        return new SessionDetailsDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), setsDTO);
    }

    public SessionDetailsGroupedDTO getSessionDetailsGrouped(int id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));
        
        System.out.println("Sets before: " + getSetsDTO(session));
        List<SetGroupedDTO> groupedSetsDTO = groupConsecutiveSetsByExercise(getSetsDTO(session));
        System.out.println("Sets after group: " + getSetsDTO(session));
        return new SessionDetailsGroupedDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), groupedSetsDTO);
    }

    public SessionGroupedDTO getSessionGrouped(int id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetGroupedDTO> groupedSets = groupConsecutiveSetsByExercise(getSetsDTO(session));

        List<SetsGroupedWithNameDTO> groupedSetsWithName = groupedSets.stream()
                .map(this::groupedSetToGroupedSetWithName)
                .toList();

        List<SetsGroupedFinalDTO> finalGroupedSets = groupedSetsWithName.stream()
                .map(this::cleanSetsGroup)
                .toList();
        
        SessionGroupedDTO sessionGroupedDTO = new SessionGroupedDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), finalGroupedSets);
        System.out.println(sessionGroupedDTO);
        return sessionGroupedDTO;
    }

    
    // public List<SetGroupedDTO> groupConsecutiveSetsByExercise(List<SetInSessionDTO> exerciseSets){
    //     List<SetGroupedDTO> groupedSets = new ArrayList<>();
    //     List<SetInSessionDTO> currentGroup = new ArrayList<>();

    //     for(int i=0; i < exerciseSets.size(); i++){
    //         SetInSessionDTO currentSet = exerciseSets.get(i);

    //         if (i==0 || currentSet.exercise_id() == exerciseSets.get(i-1).exercise_id()) {
    //             currentGroup.add(currentSet);
    //         } else {
    //             groupedSets.add(new SetGroupedDTO(currentGroup));
    //             currentGroup.clear();
    //             currentGroup.add(currentSet);
    //         }
    //         System.out.println(i + " after: groupedSets: " + groupedSets);
    //         System.out.println(i + " after: currentGroup: " + currentGroup);

    //     }

    //     if(!currentGroup.isEmpty()){
    //         groupedSets.add(new SetGroupedDTO(currentGroup));
    //     }

    //     return groupedSets;
    // }

    public List<SetGroupedDTO> groupConsecutiveSetsByExercise(List<SetInSessionDTO> exerciseSets) {
        List<SetGroupedDTO> groupedSets = new ArrayList<>();
        List<SetInSessionDTO> currentGroup = new ArrayList<>();
    
        for (int i = 0; i < exerciseSets.size(); i++) {
            SetInSessionDTO currentSet = exerciseSets.get(i);
    
            // Si c'est le premier set ou le même exercice que le précédent
            if (i == 0 || currentSet.exercise_id() == exerciseSets.get(i - 1).exercise_id()) {
                currentGroup.add(currentSet); // On continue d'ajouter au groupe
            } else {
                // Si l'exercice a changé, on ajoute le groupe courant
                groupedSets.add(new SetGroupedDTO(new ArrayList<>(currentGroup)));
                currentGroup.clear(); // On vide le groupe pour en créer un nouveau
                currentGroup.add(currentSet); // On commence un nouveau groupe avec le set actuel
            }
    
            // Debug
            System.out.println(i + " after: groupedSets: " + groupedSets);
            System.out.println(i + " after: currentGroup: " + currentGroup);
        }
    
        // Ajouter le dernier groupe s'il n'est pas vide
        if (!currentGroup.isEmpty()) {
            groupedSets.add(new SetGroupedDTO(currentGroup));
        }
    
        return groupedSets;
    }


    // public List<SetGroupedDTO> groupConsecutiveSetsByExercise(List<SetInSessionDTO> exerciseSets) {
    //     List<SetGroupedDTO> groupedSets = new ArrayList<>();
    //     List<SetInSessionDTO> currentGroup = new ArrayList<>();
        
    //     // Vérifie s'il y a des sets à traiter
    //     if (exerciseSets == null || exerciseSets.isEmpty()) {
    //         return groupedSets;
    //     }
    
    //     SetInSessionDTO previousSet = null; // Utilise une variable pour garder l'exercice précédent
    
    //     for (SetInSessionDTO currentSet : exerciseSets) {
    //         // Si on est sur le premier set ou si l'exercice est le même que le précédent
    //         if (previousSet == null || currentSet.exercise_id() == previousSet.exercise_id()) {
    //             currentGroup.add(currentSet); // On ajoute le set dans le groupe en cours
    //         } else {
    //             groupedSets.add(new SetGroupedDTO(currentGroup)); // On ajoute le groupe précédent
    //             currentGroup.clear(); // On vide le groupe courant
    //             currentGroup.add(currentSet); // On commence un nouveau groupe
    //         }
            
    //         // Mise à jour de previousSet pour la prochaine itération
    //         previousSet = currentSet;
    //     }
    
        // Ajouter le dernier groupe s'il existe
    //     if (!currentGroup.isEmpty()) {
    //         groupedSets.add(new SetGroupedDTO(currentGroup));
    //     }
    
    //     return groupedSets;
    // }
    


    public SetsGroupedWithNameDTO groupedSetToGroupedSetWithName(SetGroupedDTO entrySet){
        int exerciseId = entrySet.setGroup().get(0).exercise_id();
        String shortName = exerciseRepository.findById(exerciseId)
                        .map(Exercise::getShortName)
                        .orElseThrow(()->new NoSuchElementException("Exercise not found with id: " + exerciseId));

        List<SetInSetsGroupedDTO> sets = entrySet.setGroup().stream()
            .map(set -> new SetInSetsGroupedDTO(set.weight(), set.repNumber(), set.isMax()))
            .toList();

        return new SetsGroupedWithNameDTO(shortName, sets);        
    }

    public List<SetInSessionDTO> getSetsDTO(Session session){
        return session.getSetRecords().stream()
                .map(exerciseSet -> new SetInSessionDTO(
                    exerciseSet.getId(),
                    exerciseSet.getExercise().getId(),
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
            }
        }
        return new SetsGroupedFinalDTO(sets.exerciseNameShort(), sets.sets());
    }
    
    // public SetsGroupedFinalDTO cleanSetsGroup(SetsGroupedWithNameDTO sets) {
    //     if (sets.sets().isEmpty()) {
    //         return new SetsGroupedFinalDTO(sets.exerciseNameShort(), new ArrayList<>()); // Évite un null
    //     }
    
    //     boolean sameWeight = hasSameWeight(sets);
    //     boolean sameReps = hasSameReps(sets);
    
    //     System.out.println("Processing " + sets.exerciseNameShort());
    //     System.out.println("Same Weight: " + sameWeight);
    //     System.out.println("Same Reps: " + sameReps);
    
    //     if (sameWeight && sameReps) {
    //         // Fusionner en un seul set
    //         return new SetsGroupedFinalDTO(
    //                 sets.exerciseNameShort(),
    //                 new SetsSameWeightAndRepsDTO(
    //                         sets.sets().size(),
    //                         sets.sets().get(0).repNumber(),
    //                         sets.sets().get(0).weight()));
    //     }
    
    //     // 🔥 Corrige le retour des sets non fusionnés en utilisant une liste correcte
    //     List<SetInSetsGroupedDTO> nonMergedSets = new ArrayList<>(sets.sets());
    //     return new SetsGroupedFinalDTO(sets.exerciseNameShort(), nonMergedSets);
    // }
    

    public boolean hasSameWeight(SetsGroupedWithNameDTO sets){
        double firstWeight = sets.sets().get(0).weight();

        for(SetInSetsGroupedDTO set : sets.sets()){
            if(set.weight() != firstWeight){
                return false;
            }
        }
        return true;
    }

    public boolean hasSameReps(SetsGroupedWithNameDTO sets){
        int firstNb = sets.sets().get(0).repNumber();

        for(SetInSetsGroupedDTO set : sets.sets()){
            if(set.repNumber() != firstNb){
                return false;
            }
        }
        return true;
    }
}
