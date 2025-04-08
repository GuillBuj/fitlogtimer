package com.fitlogtimer.dto.listitem;

import java.time.LocalDate;
import java.util.List;

public record WorkoutListItemDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String type,
    String comment,
    List<String> exerciseShortNames){ 

        // public WorkoutListItemDTO withExerciseShortNames(List<String> shortNames) {
        //     return new WorkoutListItemDTO(
        //         this.id(),
        //         this.date(),
        //         this.bodyWeight(),
        //         this.type(),
        //         this.comment(),
        //         shortNames
        //     );
        // }
}
