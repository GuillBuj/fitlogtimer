package com.fitlogtimer.mapper;

import org.springframework.stereotype.Component;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ExerciseSetFacadeMapper {

    private final FreeWeightSetMapper freeWeightSetMapper;
    private final ExerciseSetMappingHelper mappingHelper;

    public ExerciseSet toEntity(ExerciseSetCreateDTO dto) {
        return switch (dto.type()) {
            case ExerciseSetType.FREE_WEIGHT -> 
                freeWeightSetMapper.toFreeWeightSet(dto, mappingHelper);
            // mappings (à ajouter au fur et à mesure)
            
            default -> throw new IllegalArgumentException("Unsupported type: " + dto.type());
        };
    }
}
