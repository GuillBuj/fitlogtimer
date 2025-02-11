package com.fitlogtimer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.SetInSessionDTO;
import com.fitlogtimer.dto.SetInSetsGrouped;
import com.fitlogtimer.dto.SetsGroupedWithNameDTO;
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

    public SessionDetailsDTO getSessionDetails(Long id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetInSessionDTO> setsDTO = getSetsDTO(session);
        
        return new SessionDetailsDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), setsDTO);
    }

    public SessionDetailsGroupedDTO getSessionDetailsGrouped(Long id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetGroupedDTO> groupedSetsDTO = groupConsecutiveSetsByExercise(getSetsDTO(session));
        
        return new SessionDetailsGroupedDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), groupedSetsDTO);
    }

    public SessionGroupedDTO getSessionGrouped(long id){
        Session session = sessionRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Session not found: " + id));

        List<SetGroupedDTO> groupedSets = groupConsecutiveSetsByExercise(getSetsDTO(session));

        List<SetsGroupedWithNameDTO> groupedSetsWithName = groupedSets.stream()
                .map(this::groupedSetToGroupedSetWithName)
                .toList();
        
        return new SessionGroupedDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), groupedSetsWithName);
    }

    
    public List<SetGroupedDTO> groupConsecutiveSetsByExercise(List<SetInSessionDTO> exerciseSets){
        List<SetGroupedDTO> groupedSets = new ArrayList<>();
        List<SetInSessionDTO> currentGroup = new ArrayList<>();

        for(int i=0; i < exerciseSets.size(); i++){
            SetInSessionDTO currentSet = exerciseSets.get(i);
            if (i==0 || currentSet.exercise_id() == exerciseSets.get(i-1).exercise_id()) {
                currentGroup.add(currentSet);
            } else {
                groupedSets.add(new SetGroupedDTO(currentGroup));
                currentGroup.clear();
                currentGroup.add(currentSet);
            }
        }

        if(!currentGroup.isEmpty()){
            groupedSets.add(new SetGroupedDTO(currentGroup));
        }

        return groupedSets;
    }

    public SetsGroupedWithNameDTO groupedSetToGroupedSetWithName(SetGroupedDTO entrySet){
        int exerciseId = entrySet.setGroup().get(0).exercise_id();
        String shortName = exerciseRepository.findById(exerciseId)
                        .map(Exercise::getShortName)
                        .orElseThrow(()->new NoSuchElementException("Exercise not found with id: " + exerciseId));

        List<SetInSetsGrouped> sets = entrySet.setGroup().stream()
            .map(set -> new SetInSetsGrouped(set.weight(), set.repNumber(), set.isMax()))
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
}
