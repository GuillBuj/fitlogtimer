package com.fitlogtimer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseSetDTO;
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
    public ExerciseSet saveExerciseSet(ExerciseSetDTO exerciseSetDTO) {
        // Extract Exercise ID
        Long exerciseId = exerciseSetDTO.exercise_id();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise with ID " + exerciseId + " does not exist"));
        Long sessionId = exerciseSetDTO.session_id();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session with ID " + sessionId + " does not exist"));

        // Map DTO to Entity
        ExerciseSet exerciseSet = new ExerciseSet();
        exerciseSet.setWeight(exerciseSetDTO.weight());
        exerciseSet.setRepNumber(exerciseSetDTO.repNumber());
        exerciseSet.setMax(exerciseSetDTO.isMax());
        exerciseSet.setComment(exerciseSetDTO.comment());
        exerciseSet.setExercise(exercise);
        exerciseSet.setSession(session);

        // Save the ExerciseSet
        return exerciseSetRepository.save(exerciseSet);
    }

    @Transactional
    public ExerciseSet createExerciseSet(ExerciseSet exerciseSet) {
        Session session = exerciseSet.getSession();

        if (session == null) {
            session = new Session();
            session = sessionRepository.save(session); // Crée une nouvelle session si elle n'existe pas
        } else {
            Optional<Session> existingSession = sessionRepository.findById(session.getId());
            if (existingSession.isPresent()) {
                session = existingSession.get(); // Si la session existe déjà, on l'utilise
            } else {
                session = sessionRepository.save(session); // Si elle n'existe pas, on la sauvegarde
            }
        }

        // Vérifie si l'ExerciseSet contient un Exercise détaché, et l'attache à la
        // session si nécessaire
        Exercise exercise = exerciseSet.getExercise();
        if (exercise != null && !entityManager.contains(exercise)) {
            exercise = entityManager.merge(exercise); // Attache l'exercice à la session Hibernate
        }

        exerciseSet.setSession(session);
        return exerciseSetRepository.save(exerciseSet); // Sauvegarde l'ExerciseSet avec l'Exercise attaché à la session
    }

}
