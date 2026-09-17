package com.fitlogtimer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlogtimer.model.Exercise;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    Exercise findByShortName(String shortName);
    List<Exercise> findByShortNameIn(Collection<String> shortNames);
}
