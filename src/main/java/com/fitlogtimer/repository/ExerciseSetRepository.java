package com.fitlogtimer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fitlogtimer.dto.ExerciseSetFor1RMCalcDTO;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateFor1RMDTO;
import com.fitlogtimer.dto.stats.*;
import org.springframework.data.domain.Pageable;
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
            "WHERE es.exercise.id = :exerciseId " +
            "AND (es.setMode != com.fitlogtimer.enums.SetMode.TIME_TRIAL " +
            "OR es.setMode IS NULL)")
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
            "AND (es.setMode != com.fitlogtimer.enums.SetMode.TIME_TRIAL " +
            "OR es.setMode IS NULL)" +
            "AND FUNCTION('YEAR', es.workout.date) = :year")
    Integer findMaxRepsByExerciseIdAndYear(@Param("exerciseId") int exerciseId, @Param("year") int year);

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


    @Query("SELECT MAX(fws.weight) " +
            "FROM FreeWeightSet fws " +
            "WHERE fws.exercise.id = :exerciseId " +
            "AND fws.repNumber >= 1 " +
            "AND FUNCTION('YEAR', fws.workout.date) = :year " +
            "AND FUNCTION('MONTH', fws.workout.date) = :month")
    Double findMaxWeightByExerciseIdAndMonth(
            @Param("exerciseId") int exerciseId,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT MAX(fws.weight) " +
            "FROM FreeWeightSet fws " +
            "WHERE fws.exercise.id = :exerciseId " +
            "AND fws.repNumber >= 1 " +
            "AND ISO_YEAR(fws.workout.date) = :year " +
            "AND ISO_WEEK(fws.workout.date) = :week")
    Double findMaxWeightByExerciseIdAndWeek(
            @Param("exerciseId") int exerciseId,
            @Param("year") int year,
            @Param("week") int week);


    // MAXS BY YEAR

    @Query("""
    SELECT NEW com.fitlogtimer.dto.stats.MaxWeightWithDateDTO(fws.weight, fws.workout.date, fws.workout.id)
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

    @Query("SELECT MAX(FUNCTION('YEAR', es.workout.date)) " +
            "FROM ExerciseSet es " +
            "WHERE es.exercise.id = :exerciseId")
    Integer findLastYearWithData(@Param("exerciseId") int exerciseId);

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

    @Query("SELECT new com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO(" +
            "fws.id, fws.exercise, fws.repNumber, fws.weight, " +
            "w.bodyWeight, w.id, w.date, " +
            "fws.weight / w.bodyWeight) " +
            "FROM FreeWeightSet fws " +
            "JOIN fws.workout w " +
            "WHERE fws.repNumber >= :minRepsOrWeight " +
            "AND fws.exercise.id = :exerciseId " +
            "AND w.bodyWeight > 0 " +
            "ORDER BY w.date ASC")
    List<ExerciseSetWithBodyWeightAndDateDTO> findAllRatioSetsByDateAndMinReps(
            @Param("exerciseId") int exerciseId,
            @Param("minRepsOrWeight") int minRepsOrWeight);

    @Query(value = """
    WITH ranked AS (
        SELECT
            fs.weight AS weight,
            w.body_weight AS "bodyWeight",
            w.id AS "workoutId",
            YEAR(w.date) AS "year",
            CASE
                WHEN MONTH(w.date) <= 6 THEN 1
                ELSE 2
            END AS semester,
            QUARTER(w.date) AS quarter,
            MONTH(w.date) AS month_num,

            ROW_NUMBER() OVER (
                PARTITION BY
                    YEAR(w.date),
                    CASE
                        WHEN :periodType = 'SEMESTER'
                        THEN CASE WHEN MONTH(w.date) <= 6 THEN 1 ELSE 2 END
                    END,
                    CASE
                        WHEN :periodType = 'QUARTER'
                        THEN QUARTER(w.date)
                    END,
                    CASE
                        WHEN :periodType = 'MONTH'
                        THEN MONTH(w.date)
                    END
                ORDER BY
                    fs.weight DESC,
                    w.date ASC,
                    w.id ASC
            ) AS rn

        FROM EXERCISE_SET fs
        JOIN WORKOUT w ON w.id = fs.workout_id

        WHERE fs.exercise_id = :exerciseId
          AND fs.type = 'FREE_WEIGHT'
          AND fs.weight IS NOT NULL
    )
    SELECT
        weight,
        "bodyWeight",
        "workoutId",
        "year",
        CASE WHEN :periodType = 'SEMESTER' THEN semester END AS "semester",
        CASE WHEN :periodType = 'QUARTER' THEN quarter END AS "quarter",
        CASE WHEN :periodType = 'MONTH' THEN month_num END AS month_num
    FROM ranked
    WHERE rn = 1
    ORDER BY
        "year" DESC,
        "semester" DESC,
        "quarter" DESC,
        month_num DESC
    """, nativeQuery = true)
    List<PeriodMaxNativeProjection> findPeriodMaxList(
            @Param("exerciseId") int exerciseId,
            @Param("periodType") String periodType
    );

    @Query(value = """
    WITH ranked AS (
        SELECT
            fs.weight AS "maxValue",
            w.body_weight AS "bodyWeight",
            fs.weight / w.body_weight AS ratio,
            w.id AS "workoutId",
            YEAR(w.date) AS "year",

            CASE
                WHEN MONTH(w.date) <= 6 THEN 1
                ELSE 2
            END AS semester,

            QUARTER(w.date) AS quarter,
            MONTH(w.date) AS month_num,

            ROW_NUMBER() OVER (
                PARTITION BY
                    YEAR(w.date),
                    CASE
                        WHEN :periodType = 'SEMESTER'
                        THEN CASE WHEN MONTH(w.date) <= 6 THEN 1 ELSE 2 END
                    END,
                    CASE
                        WHEN :periodType = 'QUARTER'
                        THEN QUARTER(w.date)
                    END,
                    CASE
                        WHEN :periodType = 'MONTH'
                        THEN MONTH(w.date)
                    END
                ORDER BY
                    fs.weight / w.body_weight DESC,
                    w.date ASC,
                    w.id ASC,
                    fs.id ASC
            ) AS rn

        FROM EXERCISE_SET fs
        JOIN WORKOUT w ON w.id = fs.workout_id

        WHERE fs.exercise_id = :exerciseId
          AND fs.type = 'FREE_WEIGHT'
          AND fs.weight IS NOT NULL
          AND w.body_weight > 0
    )

    SELECT
        "maxValue",
        "bodyWeight",
        ratio,
        "workoutId",
        "year",
        CASE WHEN :periodType = 'SEMESTER' THEN semester END AS "semester",
        CASE WHEN :periodType = 'QUARTER' THEN quarter END AS "quarter",
        CASE WHEN :periodType = 'MONTH' THEN month_num END AS "monthNum"

    FROM ranked
    WHERE rn = 1

    ORDER BY
        "year" DESC,
        "semester" DESC,
        "quarter" DESC,
        "monthNum" DESC
    """, nativeQuery = true)
    List<PeriodMaxRatioNativeProjection> findPeriodMaxRatioList(
            @Param("exerciseId") int exerciseId,
            @Param("periodType") String periodType
    );

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

    @Query("""
    SELECT NEW com.fitlogtimer.dto.ExerciseSetFor1RMCalcDTO(
        fws.weight,
        fws.repNumber,
        w.bodyWeight,
        w.id,
        w.date
    )
    FROM FreeWeightSet fws
    JOIN fws.workout w
    WHERE fws.exercise.id = :exerciseId
      AND fws.repNumber >= 1
      AND w.bodyWeight > 0
    """)
    List<ExerciseSetFor1RMCalcDTO> findAllSetsFor1RMCalc(@Param("exerciseId") int exerciseId);

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
    SELECT new com.fitlogtimer.dto.ExerciseSetFor1RMCalcDTO(
            fws.weight,
            fws.repNumber,
            w.bodyWeight,
            w.id,
            w.date
        )
        FROM FreeWeightSet fws
        JOIN fws.workout w
        WHERE fws.exercise.id = :exerciseId
          AND fws.repNumber >= 1
          AND w.bodyWeight > 0
          AND w.date BETWEEN :start AND :end
    """)
    List<ExerciseSetFor1RMCalcDTO> findAllSetsByExBetweenDates(
            @Param("exerciseId") int exerciseId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
       SELECT es FROM ExerciseSet es
       WHERE es.exercise.id IN :ids
         AND es.id IN (
             SELECT MAX(e2.id) FROM ExerciseSet e2
             WHERE e2.exercise.id = es.exercise.id
         )
       """)
    List<ExerciseSet> findLastSetsForExerciseIds(@Param("ids") List<Integer> ids);

    @Query("SELECT e.id FROM Exercise e WHERE e.shortName IN :shortNames")
    List<Integer> findIdsByShortNames(@Param("shortNames") List<String> shortNames);


    /* ***************/
    /* STATS VOLUME */
    /* ***************/

    @Query("SELECT new com.fitlogtimer.dto.stats.ExerciseStatCountBasicDTO(" +
            "es.exercise.id, " +
            "es.exercise.name, " +
            "COUNT(es), " +
            "SUM(es.repNumber))" +
            "FROM ExerciseSet es " +
            "WHERE es.repNumber >= 1 " +
            "GROUP BY es.exercise.id, es.exercise.name")
    List<ExerciseStatCountBasicDTO> getBasicCountsForAllExercises();

    @Query("SELECT new com.fitlogtimer.dto.stats.ExerciseStatCountWeightDTO(" +
            "fws.exercise.id, " +
            "fws.exercise.name, " +
            "COUNT(fws), " +
            "SUM(fws.repNumber), " +
            "CAST(SUM(fws.repNumber * fws.weight) AS double))" +
            "FROM FreeWeightSet fws " +
            "WHERE fws.repNumber >= 1 " +
            "GROUP BY fws.exercise.id, fws.exercise.name")
    List<ExerciseStatCountWeightDTO> getBasicCountsForWeightExercises();

    @Query("SELECT new com.fitlogtimer.dto.stats.ExerciseStatCountWeightDTO(" +
            "fws.exercise.id, " +
            "fws.exercise.name, " +
            "COUNT(fws), " +
            "SUM(fws.repNumber), " +
            "CAST(SUM(fws.repNumber * fws.weight) AS double)," +
            "YEAR(fws.workout.date))" +
            "FROM FreeWeightSet fws " +
            "WHERE fws.repNumber >= 1 " +
            "GROUP BY fws.exercise.id, fws.exercise.name, YEAR(fws.workout.date)")
    List<ExerciseStatCountWeightDTO> getYearlyBasicCountsForWeightExercises();


    @Query("""
    SELECT new com.fitlogtimer.dto.stats.TopFreeWeightSetsItemDTO(
        fws.weight,
        fws.repNumber,
        w.date,
        w.bodyWeight,
        w.id
    )
    FROM FreeWeightSet fws
    JOIN fws.workout w
    WHERE fws.exercise.id = :exerciseId
      AND fws.weight IS NOT NULL
    ORDER BY fws.weight DESC,
             fws.repNumber DESC,
             w.bodyWeight ASC,
             w.date ASC
""")
    List<TopFreeWeightSetsItemDTO> findTopFreeWeightRecords(
            @Param("exerciseId") int exerciseId,
            Pageable pageable
    );

}
