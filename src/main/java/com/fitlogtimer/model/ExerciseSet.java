package com.fitlogtimer.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class ExerciseSet {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private double weight;
    private int repNumber;
    private boolean isMax;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;
}
