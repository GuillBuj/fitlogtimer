package com.fitlogtimer.dto.stats;

public record YearlyStatCount(
        Long setsCount,
        Long repsCount,
        Double weightTotal
) {
    public Double getAvgRepsPerSet() {
        if (setsCount == null || setsCount == 0 || repsCount == null) return 0.0;
        return repsCount.doubleValue() / setsCount.doubleValue();
    }

    public String getFormattedWeight() {
        if (weightTotal == null) return "0 kg";
        long weightLong = weightTotal.longValue();
        String formatted = String.format("%,d", weightLong);
        return formatted.replace(",", " ") + " kg";
    }

    public String getFormattedAvgRepsPerSet() {
        return String.format("%.1f", getAvgRepsPerSet());
    }
    public Double getAvgWeightPerRep() {
        if (repsCount == null || repsCount == 0 || weightTotal == null) return 0.0;
        return weightTotal / repsCount.doubleValue();
    }
    public String getFormattedAvgWeightPerRep() {
        return String.format("%.1fkg", getAvgWeightPerRep());
    }
}
