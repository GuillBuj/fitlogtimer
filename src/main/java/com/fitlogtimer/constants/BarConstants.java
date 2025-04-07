package com.fitlogtimer.constants;

public class BarConstants {

    public static final double STRAIGHT_BAR_WEIGHT = 8.0;
    public static final double EZ_BAR_WEIGHT = 6.0;

    public enum BarType {
        STRAIGHT(STRAIGHT_BAR_WEIGHT),
        EZ(EZ_BAR_WEIGHT);
        
        private final double weight;
        
        BarType(double weight) {
            this.weight = weight;
        }
        
        public double getWeight() {
            return weight;
        }
    }

    public static double getWeight(BarType barType) {
        return barType.getWeight();
    }
}
