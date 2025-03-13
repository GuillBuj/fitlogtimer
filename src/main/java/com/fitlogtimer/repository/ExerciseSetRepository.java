package com.fitlogtimer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitlogtimer.model.ExerciseSet;

import jakarta.transaction.Transactional;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Integer> {
    
    
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ExerciseSet")
    void deleteAllExerciseSets();

    //@Query(value = "ALTER TABLE exerciseSet AUTO_INCREMENT = 1", nativeQuery = true)  PASSAGE SUR MYSQL
    
    
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE exercise_set ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetExerciseSetId();

    List<ExerciseSet> findByExerciseId(int exerciseId);

}
