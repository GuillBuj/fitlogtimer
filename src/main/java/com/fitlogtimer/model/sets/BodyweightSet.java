package com.fitlogtimer.model.sets;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.enums.SetMode;
import com.fitlogtimer.model.ExerciseSet;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private SetMode setMode;

    private Integer durationS;

    @Override
    public String toString() {
        return super.toString() + ", bandes: " + bands + ", poids: " + weight + ", mode: " + setMode ;
    }
}
