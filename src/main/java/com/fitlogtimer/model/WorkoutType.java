package com.fitlogtimer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class WorkoutType {
    @Id
    private String name;

    private String color;
}
