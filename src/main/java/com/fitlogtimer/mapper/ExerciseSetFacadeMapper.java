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
    private final ElasticSetMapper elasticSetMapper;
    private final IsometricSetMapper isometricSetMapper;
    private final BodyweightSetMapper bodyweightSetMapper;
    private final ExerciseSetMappingHelper mappingHelper;

    public ExerciseSet toEntity(ExerciseSetCreateDTO dto) {
        return switch (dto.type()) {
            case ExerciseSetType.FREE_WEIGHT -> 
                freeWeightSetMapper.toFreeWeightSet(dto, mappingHelper);
            
            case ExerciseSetType.ELASTIC -> 
                elasticSetMapper.toElasticSet(dto, mappingHelper);

            case ExerciseSetType.ISOMETRIC ->
                isometricSetMapper.toIsometricSet(dto, mappingHelper);

            case ExerciseSetType.BODYWEIGHT ->
                bodyweightSetMapper.toBodyweightSet(dto, mappingHelper);
            // mappings (à ajouter au fur et à mesure)
            
            default -> throw new IllegalArgumentException("Unsupported type: " + dto.type());
        };
    }
}
