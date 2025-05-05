package com.fitlogtimer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicElasticDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.dto.base.SetBasicIsometricDTO;
import com.fitlogtimer.dto.base.SetBasicMovementDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanWorkoutListItemDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentElasticDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentIsometricDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentMovementDTO;
import com.fitlogtimer.dto.postgroup.SetsSameBandsAndRepsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameBandsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameDurationRepsAndWeightDTO;
import com.fitlogtimer.dto.postgroup.SetsSameMovementDTO;
import com.fitlogtimer.dto.postgroup.SetsSameRepsElasticDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsSameRepsDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsSameWeightDTO;
import com.fitlogtimer.dto.transition.SetsGroupedWithNameDTO;

@Service
public class SetsGroupCleanerService {

    public SetGroupCleanWorkoutListItemDTO cleanSetsGroup(SetsGroupedWithNameDTO sets) {
        SetBasicInterfaceDTO firstSet = sets.sets().get(0);

        if (firstSet instanceof SetBasicDTO) {
            //log.info("*-*-* : SetBasicDTO");
            return cleanSetsGroupForSetBasic(sets);
        } else if (firstSet instanceof SetBasicElasticDTO) {
            //log.info("*-*-* : SetBasicElasticDTO");
            return cleanSetsGroupForSetBasicElastic(sets);
        } else if (firstSet instanceof SetBasicIsometricDTO) {
            //log.info("*-*-* : SetBasicIsometricDTO");
            return cleanSetsGroupForSetBasicIsometric(sets);
        } else if (firstSet instanceof SetBasicMovementDTO) {
            //log.info("*-*-* : SetBasicMovementDTO");
            return cleanSetsGroupForSetBasicMovement(sets);
        } 

        else 
        {
            throw new IllegalArgumentException("Unsupported set type: " + firstSet.getClass());
        }
    }
    
    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicIsometric(SetsGroupedWithNameDTO sets) {
        // ISOMETRIC
    
        List<SetBasicIsometricDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicIsometricDTO) set)
            .toList();
    
        if (hasSameDuration(sets)) {
            if (hasSameReps(sets)) {
                if (hasSameWeight(sets)){
                    return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameDurationRepsAndWeightDTO(
                        classicSets.size(),
                        classicSets.get(0).durationS(),
                        classicSets.get(0).repNumber(),
                        classicSets.get(0).weight()
                    )
                );}       
            } 
        }  
        
        return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentIsometricDTO(classicSets)
            );
        
    }

    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicMovement(SetsGroupedWithNameDTO sets) {
        // MOVEMENT
    
        List<SetBasicMovementDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicMovementDTO) set)
            .toList();
    
        if (hasSameMovementSets(sets)) {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameMovementDTO(
                    classicSets.size(),
                    classicSets.get(0).repNumber(),
                    classicSets.get(0).distance(),
                    classicSets.get(0).bands(),
                    classicSets.get(0).weight()
                ));
            }  
   
        
        return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentMovementDTO(classicSets)
            );
        
    }

    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicElastic(SetsGroupedWithNameDTO sets) {
        // ELASTIC
    
        List<SetBasicElasticDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicElasticDTO) set)
            .toList();
    
        if (hasSameBands(sets)) {
            if (hasSameReps(sets)) {
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameBandsAndRepsDTO(
                        classicSets.size(),
                        classicSets.get(0).repNumber(),
                        classicSets.get(0).bands()
                    )
                );
            } else {
                List<Integer> repsSet = classicSets.stream()
                    .map(SetBasicElasticDTO::repNumber)
                    .toList();
    
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameBandsDTO(
                        classicSets.get(0).bands(),
                        repsSet
                    )
                );
            }
        } else if (hasSameReps(sets)) {
            List<String> bandsList = classicSets.stream()
                .map(SetBasicElasticDTO::bands)
                .toList();
    
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameRepsElasticDTO(
                    classicSets.get(0).repNumber(),
                    bandsList
                )
            );
        } else {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentElasticDTO(classicSets)
            );
        }
    }
    
    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasic(SetsGroupedWithNameDTO sets) {
        // FREE_WEIGHT ou BODYWEIGHT
    
        List<SetBasicDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicDTO) set)
            .toList();
    
        if (hasSameWeight(sets)) {
            if (hasSameReps(sets)) {
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameWeightAndRepsDTO(
                        classicSets.size(),
                        classicSets.get(0).repNumber(),
                        classicSets.get(0).weight()
                    )
                );
            } else {
                List<Integer> repsSet = classicSets.stream()
                    .map(SetBasicDTO::repNumber)
                    .toList();
    
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameWeightDTO(
                        classicSets.get(0).weight(),
                        repsSet
                    )
                );
            }
        } else if (hasSameReps(sets)) {
            List<Double> weights = classicSets.stream()
                .map(SetBasicDTO::weight)
                .toList();
    
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameRepsDTO(
                    classicSets.get(0).repNumber(),
                    weights
                )
            );
        } else {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentDTO(classicSets)
            );
        }
    }
    
    public boolean hasSameWeight(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        SetBasicInterfaceDTO firstSet = sets.sets().get(0);
        
        if (firstSet instanceof SetBasicDTO){
            double firstWeight = ((SetBasicDTO) firstSet).weight();
            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentWeight = ((SetBasicDTO) set).weight();
                if (currentWeight != firstWeight) {
                    return false;
                }
            }}
        
        else if (firstSet instanceof SetBasicIsometricDTO){
            double firstWeight = ((SetBasicIsometricDTO) firstSet).weight();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentWeight = ((SetBasicIsometricDTO) set).weight();
                if (currentWeight != firstWeight) {
                    return false;
                }
            }}

        return true;
    }

    public boolean hasSameBands(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        String firstBands = ((SetBasicElasticDTO) sets.sets().get(0)).bands();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            String currentBands = ((SetBasicElasticDTO) set).bands();
            if (currentBands != firstBands) {
                return false;
            }
        }

        return true;
    }

    public boolean hasSameDuration(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        int firstDuration = ((SetBasicIsometricDTO) sets.sets().get(0)).durationS();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            int currentDuration = ((SetBasicIsometricDTO) set).durationS();
            if (currentDuration != firstDuration) {
                return false;
            }
        }

        return true;
    }

    public boolean hasSameMovementSets(SetsGroupedWithNameDTO sets){
        if (sets.sets().isEmpty()) return true;

        SetBasicInterfaceDTO firstSet = sets.sets().get(0);

        double firstReps = ((SetBasicMovementDTO) firstSet).repNumber();
        String firstDistance = ((SetBasicMovementDTO) firstSet).distance();
        String firstBands = ((SetBasicMovementDTO) firstSet).bands();
        double firstWeight = ((SetBasicMovementDTO) firstSet).weight();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            int currentReps = ((SetBasicMovementDTO) set).repNumber();
            String currentDistance = ((SetBasicMovementDTO) set).distance();
            String currentBands = ((SetBasicMovementDTO) set).bands();
            double currentWeight = ((SetBasicMovementDTO) set).weight();
            if (currentReps!=firstReps || currentDistance!=firstDistance || currentBands!=firstBands || currentWeight!=firstWeight) {
                return false;
            }
        }        

        return true;
    }

    public boolean hasSameReps(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;
        
        SetBasicInterfaceDTO firstSet = sets.sets().get(0);
        
        if (firstSet instanceof SetBasicDTO){
            double firstReps = ((SetBasicDTO) firstSet).repNumber();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentReps = ((SetBasicDTO) set).repNumber();
                if (currentReps != firstReps) {
                    return false;
                }
            }}
        else if (firstSet instanceof SetBasicElasticDTO){
            double firstReps = ((SetBasicElasticDTO) firstSet).repNumber();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentReps = ((SetBasicElasticDTO) set).repNumber();
                if (currentReps != firstReps) {
                    return false;
                }
            }}
        
        else if (firstSet instanceof SetBasicIsometricDTO){
            double firstReps = ((SetBasicIsometricDTO) firstSet).repNumber();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentReps = ((SetBasicIsometricDTO) set).repNumber();
                if (currentReps != firstReps) {
                    return false;
                }
            }}
        return true;
    }
}
