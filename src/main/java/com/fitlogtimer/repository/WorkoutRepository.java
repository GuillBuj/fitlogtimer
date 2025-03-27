package com.fitlogtimer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitlogtimer.model.Workout;

import jakarta.transaction.Transactional;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Workout")
    void deleteAllWorkouts();

    void deleteById(int id);

    boolean existsById(int id);

    List<Workout> findAllByOrderByDateDesc();
    
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE workout ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetWorkoutId();
}