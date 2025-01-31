package com.fitlogtimer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ExerciseSet saveExerciseSet(ExerciseSet exerciseSet) {
        Optional<Session> sessionOpt = sessionRepository.findById(exerciseSet.getSession().getId());

        if (!sessionOpt.isPresent()) {
            // If no session is found, create a new session and let Hibernate handle the id
            // and version
            Session newSession = exerciseSet.getSession(); // Don't manually set the id or version
            sessionRepository.save(newSession); // Hibernate will manage the id and version
            exerciseSet.setSession(newSession); // Link the new session to the exercise set
        } else {
            // If session exists, just link it without altering its state
            exerciseSet.setSession(sessionOpt.get());
        }

        return exerciseSetRepository.save(exerciseSet); // Save the exercise set with the session
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
