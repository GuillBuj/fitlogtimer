package com.fitlogtimer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.SetInSessionDTO;
import com.fitlogtimer.dto.SetInSessionOutDTO;
import com.fitlogtimer.dto.SetsGroupedFinalDTO;
import com.fitlogtimer.dto.SetsGroupedWithNameDTO;
import com.fitlogtimer.dto.postGroup.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postGroup.SetsSameRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postGroup.SetsSameWeightDTO;
import com.fitlogtimer.dto.sessionDisplay.SessionDetailsOutDTO;
import com.fitlogtimer.dto.sessionDisplay.SessionGroupedDTO;
import com.fitlogtimer.dto.SetsGroupedDTO;
import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.LastSetDTO;
import com.fitlogtimer.dto.SessionOutDTO;
import com.fitlogtimer.dto.SetBasicDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.SessionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    public List<Session> getAllSessions() {
        return sessionRepository.findAllByOrderByDateDesc();
    }
    
    public SessionOutDTO saveSession(Session session) {
        Session savedSession = sessionRepository.save(session);
        return new SessionOutDTO(savedSession.getId(), savedSession.getDate(), savedSession.getBodyWeight(), savedSession.getComment());
    }

    public boolean deleteSession(int sessionId) {
        if (sessionRepository.existsById(sessionId)) {
            sessionRepository.deleteById(sessionId);
            return true;
        }
        return false;
    }

    public SessionDetailsOutDTO getSessionDetailsBrut(int id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetInSessionOutDTO> setsDTO = getSetsOutDTO(session);
        
        return new SessionDetailsOutDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), setsDTO);
    }
    
    public SessionGroupedDTO getSessionGrouped(int id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetsGroupedDTO> groupedSets = groupConsecutiveSetsByExercise(getSetsDTO(session));

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
    
    public List<SetsGroupedDTO> groupConsecutiveSetsByExercise(List<SetInSessionDTO> exerciseSets){
        List<SetsGroupedDTO> groupedSets = new ArrayList<>();
        List<SetInSessionDTO> currentGroup = new ArrayList<>();

        for(int i=0; i < exerciseSets.size(); i++){
            SetInSessionDTO currentSet = exerciseSets.get(i);

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

    public List<SetInSessionOutDTO> getSetsOutDTO(Session session){
        return session.getSetRecords().stream()
                .map(exerciseSet -> new SetInSessionOutDTO(
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

        Optional<Session> optionalSession = sessionRepository.findById(id);
        if (optionalSession.isEmpty()) {return null;}
    
        Session session = optionalSession.get();
    
        List<ExerciseSet> sets = session.getSetRecords();
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
    
        Optional<Session> optionalSession = sessionRepository.findById(id);
        if (optionalSession.isEmpty()) {
            return new ExerciseSetInDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBoolean, defaultComment, id);
        }
    
        Session session = optionalSession.get();
    
        List<ExerciseSet> sets = session.getSetRecords();
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
