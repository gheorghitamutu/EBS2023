package org.project.data;

import org.project.models.ProtoSimplePublication;

import java.time.Duration;
import java.util.*;

import static org.project.data.RawData.*;

public class PublicationGenerator {
    private static PublicationGenerator instance;

    public ProtoSimplePublication.SimplePublication generate() {
        final String city = CITIES[RANDOM.nextInt(CITIES.length)];
        final String stationId = STATION_IDS.get(city).get(RANDOM.nextInt(STATION_IDS.get(city).size()));
        double temperature = RANDOM.nextDouble() * 50;
        if (RANDOM.nextDouble() > 0.5) {
            temperature *= -1;
        }
        final double rain = RANDOM.nextDouble() * 0.5;
        final double wind = RANDOM.nextDouble() * 30;
        final String direction = DIRECTIONS[RANDOM.nextInt(DIRECTIONS.length)];
        final Date date = Date.from(
                new Date(System.currentTimeMillis()).toInstant()
                        .plus(Duration.ofDays(RANDOM.nextInt(30) + 1))
                        .plus(Duration.ofMinutes(RANDOM.nextInt(720) + 1)));

        return ProtoSimplePublication.SimplePublication.newBuilder()
                .setUuid(UUID.randomUUID().toString())
                .setStationId(stationId)
                .setCity(city)
                .setTemperature(temperature)
                .setRain(rain)
                .setWind(wind)
                .setDirection(direction)
                .setDateTimestamp(date.getTime())
                .setGenerationTimestamp(System.currentTimeMillis())
                .build();
    }

    public static PublicationGenerator getInstance() {
        if (instance == null) {
            instance = new PublicationGenerator();
        }
        return instance;
    }
}
