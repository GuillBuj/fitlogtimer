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

    public Double getAvgWeightPerRep() {
        if (repsCount == null || repsCount == 0 || weightTotal == null) return 0.0;
        return weightTotal / repsCount.doubleValue();
    }

    public Double getAvgRepsPerSet() {
        if (setsCount == null || setsCount == 0 || repsCount == null) return 0.0;
        return repsCount.doubleValue() / setsCount.doubleValue();
    }

    public String getFormattedAvgRepsPerSet() {
        return String.format("%.1f", getAvgRepsPerSet());
    }

    public String getFormattedAvgWeightPerRep() {
        return String.format("%.1f kg", getAvgWeightPerRep());
    }
}
