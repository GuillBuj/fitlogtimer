package com.fitlogtimer.repository;

import com.fitlogtimer.model.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Integer> {
}
