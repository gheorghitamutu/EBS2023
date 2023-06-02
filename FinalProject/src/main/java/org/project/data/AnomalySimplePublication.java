package org.project.data;

import org.project.models.ProtoSimplePublication;

public class AnomalySimplePublication {

    enum AnomalyType {
        NONE,
        TEMPERATURE,
        RAIN,
        WIND
    }

    public final static double MAX_TEMPERATURE = 44.0;
    public final static double MIN_TEMPERATURE = 25.0;
    public final static double MAX_WIND = 30.0;
    public final static double MAX_RAIN = 0.8;

    public static AnomalyType isAnomaly(ProtoSimplePublication.SimplePublication sp) {

        final var temperature = sp.getTemperature();
        if (temperature >= MAX_TEMPERATURE || temperature <= MIN_TEMPERATURE) {
            return AnomalyType.TEMPERATURE;
        }

        if (sp.getWind() >= MAX_WIND) {
            return AnomalyType.WIND;
        }

        if (sp.getRain() >= MAX_RAIN) {
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
