package com.fitlogtimer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutType {
    @Id
    private String name;

    private String color;

    private String description;

    public WorkoutType(String name) {
        this.name = name;
        this.color = "lightgray";
        this.description = "";
    }
}
