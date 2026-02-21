package com.fitlogtimer.dto.stats;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record TopFreeWeightSetsItemDTO(
        double weight,
        int nbReps,
        LocalDate date,
        double bodyWeight,
        int workoutId
) {
    public TopFreeWeightSetsItemDTO(
            double weight,
            int nbReps,
            LocalDate date,
            double bodyWeight,
            int workoutId
    ) {
        this.weight = weight;
        this.nbReps = nbReps;
        this.date = date;
        this.bodyWeight = bodyWeight;
        this.workoutId = workoutId;
    }

    public String getColorClass() {
        if (date == null) return "color-ancient";

        long mois = ChronoUnit.MONTHS.between(date, LocalDate.now());

        if (mois <= 1) return "color-fresh";
        if (mois <= 3) return "color-recent";
        if (mois <= 6) return "color-moderate";
        if (mois <= 12) return "color-aged";
        if (mois <= 24) return "color-old";
        return "color-ancient";
    }
}
