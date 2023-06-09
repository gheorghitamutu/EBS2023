package org.project.data;

import org.project.models.ProtoSimplePublication;

import java.time.Duration;
import java.util.*;

public class PublicationGenerator {
    private static PublicationGenerator instance;

    private static final List<City> cityList = List.of(
            new City(City.Name.SAN_FRANCISCO),
            new City(City.Name.NEW_YORK),
            new City(City.Name.LONDON),
            new City(City.Name.PARIS),
            new City(City.Name.TOKYO)
    );

    private static final String[] CITIES = cityList.stream().map(City::ToString).toArray(String[]::new);

    private static final Map<String, List<String>> STATION_IDS = new HashMap<>() {{
        put(CITIES[0], new ArrayList<>() {{
            add("0");
            add("1");
        }});
        put(CITIES[1], new ArrayList<>() {{
            add("0");
            add("1");
        }});
        put(CITIES[2], new ArrayList<>() {{
            add("0");
            add("1");
        }});
        put(CITIES[3], new ArrayList<>() {{
            add("0");
            add("1");
        }});
        put(CITIES[4], new ArrayList<>() {{
            add("0");
            add("1");
        }
        });
    }};

    private static final String[] DIRECTIONS = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

    private static final Random RANDOM = new Random();

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
                .setTimestamp(date.getTime())
                .build();
    }

    public static PublicationGenerator getInstance() {
        if (instance == null) {
            instance = new PublicationGenerator();
        }
        return instance;
    }
}
