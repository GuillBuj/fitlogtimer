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
@DiscriminatorValue(ExerciseSetType.MOVEMENT)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MovementSet extends ExerciseSet{

    private String bands;
    private String distance; // String car A/R tapis, A/R chambre, etc

    @Override
    public String toString(){
        return super.toString() + ", bandes: " + bands + ", distance: " + distance;
    }

}
