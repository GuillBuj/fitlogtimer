package com.fitlogtimer.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.ExerciseSetOutDTO;
import com.fitlogtimer.dto.SetBasicDTO;
import com.fitlogtimer.dto.SetBasicWith1RMDTO;
import com.fitlogtimer.model.ExerciseSet;

@Component
public class ExerciseSetMapper {
        
    public List<SetBasicWith1RMDTO> toListSetBasicWith1RMDTO(List<SetBasicDTO> setBasicDTOs) {
                
        return setBasicDTOs.stream()
                        .map(SetBasicWith1RMDTO::new)
                        .collect(Collectors.toList());
    }

    public List<SetBasicDTO> toListSetBasicDTO(List<SetBasicWith1RMDTO> setBasicWith1RMDtos) {
        
        return setBasicWith1RMDtos.stream()
                .map(SetBasicDTO::new)
                .collect(Collectors.toList());
    }

    public ExerciseSetOutDTO toExerciseSetOutDTO(ExerciseSet exercise){
        
        return new ExerciseSetOutDTO(
            exercise.getId(),
            exercise.getExercise().getId(),
            exercise.getWeight(),
            exercise.getRepNumber(),
            exercise.isMax(),
            exercise.getComment(),
            exercise.getWorkout().getId()
        );
    }
    
}
