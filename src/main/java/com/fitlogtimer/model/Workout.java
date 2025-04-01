package com.fitlogtimer.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder 
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate date;
    private double bodyWeight;
    private String type;
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "workout_id")
    private List<ExerciseSet> setRecords;

    // pour savoir si le workout vient d'un import pour éventuellement supprimer si nouvel import de la même source
    private String tagImport;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("id: " + String.valueOf(id) + ", ");
        sb.append("date: " + String.valueOf(date) + ", ");
        sb.append("poids: " + String.valueOf(bodyWeight) + ", ");
        sb.append("type: " + type + ", ");
        sb.append("tag: " + tagImport + ", ");
        sb.append("commentaire: " + comment);
        return sb.toString();
    }

}
