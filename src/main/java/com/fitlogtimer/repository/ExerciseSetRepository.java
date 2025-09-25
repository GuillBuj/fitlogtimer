package com.fitlogtimer.repository;

import java.util.List;
import java.util.Optional;

import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateFor1RMDTO;
import com.fitlogtimer.dto.stats.MaxWeightWithDateDTO;
import com.fitlogtimer.dto.stats.MaxWithDateDTO;
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

    @Query("SELECT MAX(es.durationS) " +
            "FROM IsometricSet es " +
            "WHERE es.exercise.id = :exerciseId")
    Integer findMaxDurationByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT NEW com.fitlogtimer.dto.stats.MaxWithDateDTO(es.durationS, es.workout.date) " +
            "FROM IsometricSet es " +
            "WHERE es.exercise.id = :exerciseId " +
            "AND es.durationS = (SELECT MAX(es2.durationS) FROM IsometricSet es2 WHERE es2.exercise.id = :exerciseId) " +
            "ORDER BY es.workout.date ASC " +
            "LIMIT 1")
    Optional<MaxWithDateDTO> findMaxDurationWithDateByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT MAX(es.durationS) " +
            "FROM IsometricSet es " +
            "WHERE es.exercise.id = :exerciseId " +
            "AND FUNCTION('YEAR', es.workout.date) = :year")
    Integer findMaxDurationByExerciseIdAndYear(@Param("exerciseId") int exerciseId, @Param("year") int year);

    @Query("SELECT MAX(es.repNumber) " +
            "FROM ExerciseSet es " +
            "WHERE es.exercise.id = :exerciseId")
    Integer findMaxRepsByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT NEW com.fitlogtimer.dto.stats.MaxWithDateDTO(es.repNumber, es.workout.date) " +
            "FROM ExerciseSet es " +
            "WHERE es.exercise.id = :exerciseId " +
            "AND es.repNumber = (SELECT MAX(es2.repNumber) FROM ExerciseSet es2 WHERE es2.exercise.id = :exerciseId) " +
            "ORDER BY es.workout.date ASC " +
            "LIMIT 1")
    Optional<MaxWithDateDTO> findMaxRepsWithDateByExerciseId(@Param("exerciseId") int exerciseId);

    @Query("SELECT MAX(es.repNumber) " +
            "FROM ExerciseSet es " +
            "WHERE es.exercise.id = :exerciseId " +
            "AND FUNCTION('YEAR', es.workout.date) = :year")
    Integer findMaxRepsByExerciseIdAndYear(@Param("exerciseId") int exerciseId, @Param("year") int year);

//    @Query("SELECT MAX(es.repNumber) " +
//            "FROM ExerciseSet es " +
//            "WHERE es.exercise.id = :exerciseId " +
//            "AND FUNCTION('YEAR', es.workout.date) = :year")
//    Double findMaxRepsByExerciseIdAndYear(@Param("exerciseId") int exerciseId, @Param("year") int year);

//    @Query("""
//    SELECT NEW com.fitlogtimer.dto.stats.MaxWithDateDTO(es.repNumber, es.workout.date)
//    FROM ExerciseSet es
//    WHERE es.exercise.id = :exerciseId
//      AND FUNCTION('YEAR', es.workout.date) = :year
//    ORDER BY es.repNumber DESC, es.workout.date ASC
//    """)
//    List<MaxWeightWithDateDTO> findMaxRepsByExerciseIdAndRepsAndYear(
//            @Param("exerciseId") int exerciseId,
//            @Param("year") int year);

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


    // MAXS BY YEAR

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

//    @Query("""
//    SELECT NEW com.fitlogtimer.dto.stats.MaxWithDateDTO(es.repNumber, es.workout.date)
//    FROM ExerciseSet es
//    WHERE es.exercise.id = :exerciseId
//      AND FUNCTION('YEAR', es.workout.date) = :year
//    ORDER BY es.repNumber DESC, es.workout.date ASC
//    """)
//    List<MaxWithDateDTO> findMaxRepsByExerciseIdAndYear(
//            @Param("exerciseId") int exerciseId,
//            @Param("year") int year);

    @Query("""
    SELECT NEW com.fitlogtimer.dto.stats.MaxWithDateDTO(es.repNumber, es.workout.date)
    FROM ExerciseSet es
    WHERE es.exercise.id = :exerciseId
      AND FUNCTION('YEAR', es.workout.date) = :year
    ORDER BY es.repNumber DESC, es.workout.date ASC
    LIMIT 1
    """)
    Optional<MaxWithDateDTO> findMaxRepsWithDateByExerciseIdAndYear(
            @Param("exerciseId") int exerciseId,
            @Param("year") int year);

    @Query("""
    SELECT NEW com.fitlogtimer.dto.stats.MaxWithDateDTO(es.durationS, es.workout.date)
    FROM IsometricSet es
    WHERE es.exercise.id = :exerciseId
      AND FUNCTION('YEAR', es.workout.date) = :year
    ORDER BY es.durationS DESC, es.workout.date ASC
    LIMIT 1
    """)
    Optional<MaxWithDateDTO> findMaxDurationWithDateByExerciseIdAndYear(
            @Param("exerciseId") int exerciseId,
            @Param("year") int year);

    @Query("SELECT MIN(FUNCTION('YEAR', es.workout.date)) " +
            "FROM ExerciseSet es " +
            "WHERE es.exercise.id = :exerciseId")
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

    @Query("SELECT new com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO(" +
            "fws.id, fws.exercise, fws.repNumber, fws.weight, " +
            "w.bodyWeight, w.id, w.date, " +
            "fws.weight / w.bodyWeight) " +
            "FROM FreeWeightSet fws " +
            "JOIN fws.workout w " +
            "WHERE fws.repNumber >= 1 " +
            "AND fws.exercise.id = :exerciseId " +
            "AND w.bodyWeight > 0 " +
            "ORDER BY (fws.weight / w.bodyWeight) DESC " +
            "LIMIT 1")
    Optional<ExerciseSetWithBodyWeightAndDateDTO> findTopMaxRatioSet(@Param("exerciseId") int exerciseId);

    //liste des meilleurs ratio sur poids max groupés par année
    //!peut retourner des doublons
    @Query("SELECT new com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO(" +
            "fws.id, fws.exercise, fws.repNumber, fws.weight, " +
            "w.bodyWeight, w.id, w.date, " +
            "fws.weight / w.bodyWeight) " +
            "FROM FreeWeightSet fws " +
            "JOIN fws.workout w " +
            "WHERE fws.repNumber >= 1 " +
            "AND fws.exercise.id = :exerciseId " +
            "AND w.bodyWeight > 0 " +
            "AND (fws.weight / w.bodyWeight) IN (" +
            "    SELECT MAX(fws2.weight / w2.bodyWeight) " +
            "    FROM FreeWeightSet fws2 " +
            "    JOIN fws2.workout w2 " +
            "    WHERE fws2.repNumber >= 1 " +
            "    AND fws2.exercise.id = :exerciseId " +
            "    AND w2.bodyWeight > 0 " +
            "    AND FUNCTION('YEAR', w2.date) = FUNCTION('YEAR', w.date)" +
            ") " +
            "ORDER BY w.date DESC")
    List<ExerciseSetWithBodyWeightAndDateDTO> findYearlyMaxRatioSets(@Param("exerciseId") int exerciseId);

    @Query("SELECT new com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateFor1RMDTO(" +
            "fws.id, fws.exercise, fws.repNumber, fws.weight, " +
            "w.bodyWeight, w.id, w.date, 0, 0) " +
            "FROM FreeWeightSet fws " +
            "JOIN fws.workout w " +
            "WHERE fws.repNumber >= 1 " +
            "AND fws.exercise.id = :exerciseId " +
            "AND w.bodyWeight > 0")
    List<ExerciseSetWithBodyWeightAndDateFor1RMDTO> findAllSetsFor1RM(@Param("exerciseId") int exerciseId);

    @Query("""
       SELECT es FROM ExerciseSet es
       WHERE es.exercise.id IN :ids
         AND es.id IN (
             SELECT MAX(e2.id) FROM ExerciseSet e2
             WHERE e2.exercise.id = es.exercise.id
         )
       """)
    List<ExerciseSet> findLastSetsForExerciseIds(@Param("ids") List<Integer> ids);


}
