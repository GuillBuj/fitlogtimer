package com.fitlogtimer.repository;

import java.util.List;

import com.fitlogtimer.dto.stats.MaxWeightWithDateDTO;
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
    
    // @Query("SELECT e FROM ExerciseSet e WHERE e.workoutId = :workoutId ORDER BY e.id DESC")
    // Optional<ExerciseSet> findTopByWorkoutIdOrderByIdDesc(@Param("workoutId") int workoutId);
    
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE exercise_set ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetExerciseSetId();

    List<ExerciseSet> findByExerciseId(int exerciseId);

    @Query("SELECT MAX(e.durationS) " +
            "FROM ExerciseSet e " +
            "WHERE e.exercise.id = :exerciseId")
    Integer findMaxDurationByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT MAX(e.repNumber) " +
            "FROM ExerciseSet e " +
            "WHERE e.exercise.id = :exerciseId")
    Integer findMaxByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT exset FROM ExerciseSet exset " +
           "JOIN exset.workout s " +
           "WHERE exset.exercise.id = :exerciseId " +
           "ORDER BY s.date DESC, s.id DESC")
    List<ExerciseSet> findByExerciseIdOrderByWorkoutDateAndIdDesc(@Param("exerciseId") int exerciseId);

    @Query("SELECT MAX(fws.weight) " +
            "FROM FreeWeightSet fws " +
            "WHERE fws.exercise.id = :exerciseId AND fws.repNumber >=1" )
    Double findMaxWeightByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT MAX(fws.weight) " +
            "FROM FreeWeightSet fws " +
            "WHERE fws.exercise.id = :exerciseId " +
            "AND fws.repNumber >= 1 " +
            "AND FUNCTION('YEAR', fws.workout.date) = :year")
    Double findMaxWeightByExerciseIdAndYear(@Param("exerciseId") int exerciseId, @Param("year") int year);

    @Query("""
    SELECT NEW com.fitlogtimer.dto.stats.MaxWeightWithDateDTO(fws.weight, fws.workout.date)
    FROM FreeWeightSet fws
    WHERE fws.exercise.id = :exerciseId
      AND fws.repNumber = :repNumber
      AND FUNCTION('YEAR', fws.workout.date) = :year
    ORDER BY fws.weight DESC, fws.workout.date ASC
    """)
    List<MaxWeightWithDateDTO> findMaxWeightByExerciseIdAndRepsAndYear(
            @Param("exerciseId") int exerciseId,
            @Param("repNumber") int repNumber,
            @Param("year") int year);

    // récupère l'année du premier set d'un exercice donné
    @Query("SELECT MIN(FUNCTION('YEAR', fws.workout.date)) " +
            "FROM FreeWeightSet fws " +
            "WHERE fws.exercise.id = :exerciseId")
    Integer findFirstYearWithData(@Param("exerciseId") int exerciseId);

    @Query("""
           SELECT es
           FROM ExerciseSet es
           WHERE es.exercise.id = :exerciseId
           AND es.repNumber >= :minReps
           ORDER BY es.workout.date ASC
           """)
    List<ExerciseSet> findAllSetsByDateAndMinReps(@Param("exerciseId") int exerciseId,
                                                  @Param("minReps") int minReps);

    List<ExerciseSet> findByWorkoutIdIn(List<Integer> workoutIds);

}
