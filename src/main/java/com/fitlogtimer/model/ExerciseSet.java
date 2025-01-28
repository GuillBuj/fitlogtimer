package com.fitlogtimer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class ExerciseSet {
    
    @Id
    @GeneratedValue
    private Long id;

    private double weight;
    private int repNumber;
    private boolean isMax;
    private String comment;   
}
