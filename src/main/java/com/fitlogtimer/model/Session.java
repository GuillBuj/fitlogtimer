package com.fitlogtimer.model;

import java.sql.Date;
import java.util.List;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Session {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private Date date;
    private int bodyWeight;
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "session_id") // Clé étrangère dans ExerciseSet
    private List<ExerciseSet> setRecords;
}
