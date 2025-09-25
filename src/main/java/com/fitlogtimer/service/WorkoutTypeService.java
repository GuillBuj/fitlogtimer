package com.fitlogtimer.service;

import com.fitlogtimer.dto.listitem.WorkoutTypeListItemDTO;
import com.fitlogtimer.dto.update.WorkoutTypeUpdateDTO;
import com.fitlogtimer.mapper.WorkoutTypeMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.WorkoutType;
import com.fitlogtimer.repository.WorkoutRepository;
import com.fitlogtimer.repository.WorkoutTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutTypeService {

    private final WorkoutTypeRepository workoutTypeRepository;
    private final WorkoutTypeMapper workoutTypeMapper;
    private final WorkoutRepository workoutRepository;

    public boolean deleteWorkoutType(String name) {
        Optional<WorkoutType> workoutType = workoutTypeRepository.findByName(name);
        if (workoutType.isPresent()) {
            if (
                    !existsWithWorkoutType(workoutType.get())
            )
            {
                workoutTypeRepository.delete(workoutType.get());
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public boolean updateWorkoutType(WorkoutTypeUpdateDTO dto) {
        Optional<WorkoutType> optional = workoutTypeRepository.findByName(dto.name());
        if (optional.isEmpty()) {
            return false;
        }

        WorkoutType workoutType = optional.get();
        workoutTypeMapper.updateWorkoutTypeFromDTO(dto, workoutType);
        workoutTypeRepository.save(workoutType);

        return true;
    }

    public WorkoutTypeUpdateDTO getWorkoutTypeUpdateDTO(String name) {
        return workoutTypeRepository.findByName(name)
                .map(workoutTypeMapper::toWorkoutTypeUpdateDTO)
                .orElseThrow(() -> new IllegalArgumentException("Aucun WorkoutType trouvé pour le nom : " + name));
    }

    public Set<WorkoutTypeListItemDTO> getAllWorkoutTypeItems(){
        Set<WorkoutTypeListItemDTO> items = workoutTypeRepository.findAll().stream()
                .map(workoutTypeMapper::toWorkoutTypeListItemDTO)
                .collect(Collectors.toSet());

        log.info("Workout types found: {}", items.stream().map(WorkoutTypeListItemDTO::name).toList());

        return items;
    }

    public LinkedHashSet<String> getWorkoutTypeName() {
        return workoutTypeRepository.findAll().stream()
                .map(WorkoutType::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public WorkoutType findOrCreate(String name){

        if(name == null || name.isBlank()){
            log.error("WorkoutType name is null or empty");
            return null;
        }

        return workoutTypeRepository.findByName(name)
                .orElseGet(()-> {
                    log.info("Creating new workout type {}", name);
                    WorkoutType newType = new WorkoutType(name);
                    return workoutTypeRepository.save(newType);
                });
    }

    public boolean existsWithWorkoutType(WorkoutType workoutType) {
        return workoutRepository.existsByType(workoutType);
    }

    public Map<String, String> getAllExerciseColors(){
        return workoutTypeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        WorkoutType::getName,
                        workoutType -> workoutType.getColor() != null ? workoutType.getColor() : "#666666"));
    }
}
