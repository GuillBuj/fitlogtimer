package com.fitlogtimer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fitlogtimer.model.ExerciseSet;

import jakarta.transaction.Transactional;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Integer> {
    
    
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ExerciseSet")
    void deleteAllExerciseSets();

    //@Query(value = "ALTER TABLE exerciseSet AUTO_INCREMENT = 1", nativeQuery = true)  PASSAGE SUR MYSQL
    
    // @Query("SELECT e FROM ExerciseSet e WHERE e.sessionId = :sessionId ORDER BY e.id DESC")
    // Optional<ExerciseSet> findTopBySessionIdOrderByIdDesc(@Param("sessionId") int sessionId);
    
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE exercise_set ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetExerciseSetId();

    List<ExerciseSet> findByExerciseId(int exerciseId);

    @Query("SELECT exset FROM ExerciseSet exset " +
           "JOIN exset.session s " +
           "WHERE exset.exercise.id = :exerciseId " +
           "ORDER BY s.date DESC, s.id DESC")
    List<ExerciseSet> findByExerciseIdOrderBySessionDateAndIdDesc(@Param("exerciseId") int exerciseId);

}
