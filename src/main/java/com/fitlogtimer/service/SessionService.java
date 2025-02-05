package com.fitlogtimer.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.ExerciseSetInSessionDTO;
import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionOutDTO;
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

        List<ExerciseSetInSessionDTO> setsDTO= session.getSetRecords().stream()
                .map(exerciseSet -> new ExerciseSetInSessionDTO(
                    exerciseSet.getId(),
                    exerciseSet.getExercise().getId(),
                    exerciseSet.getWeight(),
                    exerciseSet.getRepNumber(),
                    exerciseSet.isMax(),
                    exerciseSet.getComment()))
                .collect(Collectors.toList());
        
        return new SessionDetailsDTO(id, session.getDate(), session.getBodyWeight(), session.getComment(), setsDTO);
    }
}
