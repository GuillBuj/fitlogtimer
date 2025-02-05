package com.fitlogtimer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.SessionRepository;

@Service
public class DBService {
    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public void clearExerciseSetAndSession(){
        exerciseSetRepository.deleteAllExerciseSets();
        sessionRepository.deleteAllSessions();        
        
        sessionRepository.resetSessionId();
        exerciseSetRepository.resetExerciseSetId();
    }    

}
