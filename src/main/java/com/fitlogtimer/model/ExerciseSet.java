package com.fitlogtimer.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private double weight;
    private int repNumber;
    private boolean isMax;
    private String comment;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("id: " + String.valueOf(id) + ", ");
        sb.append("ex: " + exercise.getShortName());
        sb.append("reps: " + String.valueOf(repNumber) + ", ");
        sb.append("poids: " + String.valueOf(weight) + ", ");
        return sb.toString();
    }
}
