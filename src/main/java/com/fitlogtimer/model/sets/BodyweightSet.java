package com.fitlogtimer.model.sets;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.model.ExerciseSet;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(ExerciseSetType.BODYWEIGHT)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper =  true)
public class BodyweightSet extends ExerciseSet{
    
    private Double weight;

    private String bands;

    @Override
    public String toString() {
        return super.toString() + ", bandes: " + bands + ", poids: " + weight;
    }
}
