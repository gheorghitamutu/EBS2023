package org.project.data;

import java.util.*;

class RawData {

    public static final List<City> cityList = List.of(
            new City(City.Name.SAN_FRANCISCO),
            new City(City.Name.NEW_YORK),
            new City(City.Name.LONDON),
            new City(City.Name.PARIS),
            new City(City.Name.TOKYO)
    );

    public static final String[] CITIES = cityList.stream().map(City::ToString).toArray(String[]::new);

    public static final Map<String, List<String>> STATION_IDS = new HashMap<>() {{
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

    public static final String[] DIRECTIONS = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

    public static final Random RANDOM = new Random();
}
