package com.fitlogtimer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlogtimer.model.Exercise;

import lombok.NoArgsConstructor;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

}
