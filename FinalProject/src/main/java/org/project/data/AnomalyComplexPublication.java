package org.project.data;

import org.project.models.ProtoComplexPublication;

public class AnomalyComplexPublication {

    enum AnomalyType {
        NONE,
        TEMPERATURE,
        RAIN,
        WIND
    }

    public final static double MAX_AVG_TEMPERATURE = 44.0;
    public final static double MIN_AVG_TEMPERATURE = 25.0;
    public final static double MAX_AVG_WIND = 30.0;
    public final static double MAX_AVG_RAIN = 0.8;

    public static AnomalyType isAnomaly(ProtoComplexPublication.ComplexPublication cp) {

        final var temperature = cp.getAvgTemperature();
        if (temperature >= MAX_AVG_TEMPERATURE || temperature <= MIN_AVG_TEMPERATURE) {
            return AnomalyType.TEMPERATURE;
        }

        if (cp.getAvgWind() >= MAX_AVG_WIND) {
            return AnomalyType.WIND;
        }

        if (cp.getAvgRain() >= MAX_AVG_RAIN) {
            return AnomalyType.RAIN;
        }

        return AnomalyType.NONE;
    }

    public static String ToString(AnomalyType anomalyType) {
        switch (anomalyType) {
            case TEMPERATURE:
                return "Temperature";
            case WIND:
                return "Wind";
            case RAIN:
                return "Rain";
            default:
                return "None";
        }
    }
}
