package com.fitlogtimer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Exercise {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private String type;
    private String muscle;
    private String imageUrl;
}
