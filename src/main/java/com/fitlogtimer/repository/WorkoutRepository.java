package com.fitlogtimer.repository;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.model.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fitlogtimer.model.Workout;

import jakarta.transaction.Transactional;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Workout")
    void deleteAllWorkouts();

    void deleteById(int id);

    void deleteByTagImport(String tagImport);

    boolean existsById(int id);

    boolean existsByDateAndTagImport(LocalDate date, String tagImport);
    Workout findByDateAndTagImport(LocalDate date, String tagImport);

    Page<Workout> findAllByOrderByDateDesc(Pageable pageable);
    List<Workout> findAllByOrderByDateDesc();
    
    @Query("SELECT DISTINCT exercise.shortName FROM Exercise exercise " +
       "JOIN ExerciseSet exerciseset ON exercise.id = exerciseset.exercise.id " +
       "WHERE exerciseset.workout.id = :workoutId")
    List<String> findExerciseShortNamesByWorkoutId(@Param("workoutId") int workoutId);

    @Query("SELECT DISTINCT es.exercise FROM ExerciseSet es WHERE es.workout.id = :workoutId")
    List<Exercise> findExercisesByWorkoutId(@Param("workoutId") int workoutId);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE workout ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetWorkoutId();
}