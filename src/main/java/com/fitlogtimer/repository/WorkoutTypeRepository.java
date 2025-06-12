package com.fitlogtimer.repository;

import com.fitlogtimer.model.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Integer> {

    Optional<WorkoutType> findByName(String name);
    boolean existsByName(String name);
}
