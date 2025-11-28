package com.fitlogtimer.dto.stats;

public record ExerciseStatCountWeightDTO(
        int exerciseId,
        String exerciseName,
        Long setsCount,
        Long repsCount,
        Double weightTotal
) {
    public String getFormattedWeight() {
        if (weightTotal == null) return "0 kg";

        long weightLong = weightTotal.longValue();
        String formatted = String.format("%,d", weightLong);
        return formatted.replace(",", " ") + " kg";
    }
}
