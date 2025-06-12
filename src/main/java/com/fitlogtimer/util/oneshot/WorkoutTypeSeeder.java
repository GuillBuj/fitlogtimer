//package com.fitlogtimer.util.oneshot;
//
//import com.fitlogtimer.constants.WorkoutColorConstants;
//import com.fitlogtimer.model.Workout;
//import com.fitlogtimer.model.WorkoutType;
//import com.fitlogtimer.repository.WorkoutRepository;
//import com.fitlogtimer.repository.WorkoutTypeRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Objects;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class WorkoutTypeSeeder implements CommandLineRunner {
//
//    private final WorkoutRepository workoutRepository;
//    private final WorkoutTypeRepository workoutTypeRepository;
//
//    @Override
//    public void run(String... args) {
//        workoutRepository.findAll().stream()
//                .map(Workout::getRawType) // getName sécurisée
//                .peek(type -> log.info("WorkoutType: {}", type))
//                .filter(Objects::nonNull)
//                .distinct()
//                .forEach(typeName -> {
//                    log.info(typeName);
//                    if (!workoutTypeRepository.existsByName(typeName)) {
//                        workoutTypeRepository.save(new WorkoutType(typeName, WorkoutColorConstants.getColorForWorkoutType(typeName), ""));
//                        log.info("{} added", typeName);
//                    }
//                });
//        log.info("!!! All workouts have been seeded !!!");
//    }
//}

