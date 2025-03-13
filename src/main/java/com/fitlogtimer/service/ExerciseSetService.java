package com.fitlogtimer.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.ExerciseSetOutDTO;
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
        return exerciseSetRepository.findByExerciseId(id);
    }

}
