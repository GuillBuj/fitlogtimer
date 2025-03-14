package com.fitlogtimer.service;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.SetBasicDTO;
import com.fitlogtimer.dto.SetsGroupedWithNameDTO;

@Service
public class SetsGroupCleaner {

    // public SetsGroupedFinalDTO cleanSetsGroup(SetsGroupedWithNameDTO sets){
    //     if(hasSameWeight(sets)){
    //         if(hasSameReps(sets)){
    //             return new SetsGroupedFinalDTO(
    //                 sets.exerciseNameShort(),
    //                 new SetsSameWeightAndRepsDTO(
    //                     sets.sets().size(),
    //                     sets.sets().get(0).repNumber(),
    //                     sets.sets().get(0).weight()));
    //         }
    //     }
    //     return new SetsGroupedFinalDTO(sets.exerciseNameShort(), sets.sets());
    // }
    
    public boolean hasSameWeight(SetsGroupedWithNameDTO sets){
        double firstWeight = sets.sets().get(0).weight();

        for(SetBasicDTO set : sets.sets()){
            if(set.weight() != firstWeight){
                return false;
            }
        }
        return true;
    }

    public boolean hasSameReps(SetsGroupedWithNameDTO sets){
        int firstNb = sets.sets().get(0).repNumber();

        for(SetBasicDTO set : sets.sets()){
            if(set.repNumber() != firstNb){
                return false;
            }
        }
        return true;
    }
}
