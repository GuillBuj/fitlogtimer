package com.fitlogtimer.service;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.dto.transition.SetsGroupedWithNameDTO;

@Service
public class SetsGroupCleaner {

    public boolean hasSameWeight(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        double firstWeight = ((SetBasicDTO) sets.sets().get(0)).weight();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            double currentWeight = ((SetBasicDTO) set).weight();
            if (currentWeight != firstWeight) {
                return false;
            }
        }

        return true;
    }

    public boolean hasSameReps(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        double firstReps = ((SetBasicDTO) sets.sets().get(0)).repNumber();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            double currentReps = ((SetBasicDTO) set).repNumber();
            if (currentReps != firstReps) {
                return false;
            }
        }

        return true;
    }
    
    // public boolean hasSameWeight(SetsGroupedWithNameDTO sets){
    //     double firstWeight = sets.sets().get(0).weight();

    //     for(SetBasicDTO set : sets.sets()){
    //         if(set.weight() != firstWeight){
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    // public boolean hasSameReps(SetsGroupedWithNameDTO sets){
    //     int firstNb = sets.sets().get(0).repNumber();

    //     for(SetBasicDTO set : sets.sets()){
    //         if(set.repNumber() != firstNb){
    //             return false;
    //         }
    //     }
    //     return true;
    // }
}
