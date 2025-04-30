package com.fitlogtimer.dto.details;

import java.util.List;

import com.fitlogtimer.dto.listitem.SetGroupCleanExerciseListItemDTO;



public record ExerciseDetailsGroupedDTO(
    int id,
    String exercise,
    String type,
    List<SetGroupCleanExerciseListItemDTO> exerciseSets
) {
    
}
