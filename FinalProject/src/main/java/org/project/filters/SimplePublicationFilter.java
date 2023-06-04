package org.project.filters;

import org.project.data.City;
import org.project.models.ProtoSimplePublication;

import java.util.List;
import java.util.function.Predicate;

public class SimplePublicationFilter {

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByStationId(Operator.Type type, String stationId) {
        switch (type) {
            case LOWER_THAN:
            case GREATER_THAN:
            case EQUAL_OR_LOWER_THAN:
            case EQUAL_OR_GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by stationId with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getStationId().equals(stationId);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByCity(Operator.Type type, City city) {
        switch (type) {
            case LOWER_THAN:
            case GREATER_THAN:
            case EQUAL_OR_LOWER_THAN:
            case EQUAL_OR_GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by city with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getCity().equals(city.ToString());
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByTemperature(Operator.Type type, double temperature) {
        switch (type) {
            case LOWER_THAN:
                return (sp) -> sp.getTemperature() < temperature;
            case EQUAL_OR_LOWER_THAN:
                return (sp) -> sp.getTemperature() <= temperature;
            case EQUAL:
                return (sp) -> sp.getTemperature() == temperature;
            case EQUAL_OR_GREATER_THAN:
                return (sp) -> sp.getTemperature() >= temperature;
            case GREATER_THAN:
                return (sp) -> sp.getTemperature() > temperature;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByRain(Operator.Type type, double rain) {
        switch (type) {
            case LOWER_THAN:
                return (sp) -> sp.getRain() < rain;
            case EQUAL_OR_LOWER_THAN:
                return (sp) -> sp.getRain() <= rain;
            case EQUAL:
                return (sp) -> sp.getRain() == rain;
            case EQUAL_OR_GREATER_THAN:
                return (sp) -> sp.getRain() >= rain;
            case GREATER_THAN:
                return (sp) -> sp.getRain() > rain;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByWind(Operator.Type type, double wind) {
        switch (type) {
            case LOWER_THAN:
                return (sp) -> sp.getWind() < wind;
            case EQUAL_OR_LOWER_THAN:
                return (sp) -> sp.getWind() <= wind;
            case EQUAL:
                return (sp) -> sp.getWind() == wind;
            case EQUAL_OR_GREATER_THAN:
                return (sp) -> sp.getWind() >= wind;
            case GREATER_THAN:
                return (sp) -> sp.getWind() > wind;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByDirection(Operator.Type type, String direction) {
        switch (type) {
            case LOWER_THAN:
            case GREATER_THAN:
            case EQUAL_OR_LOWER_THAN:
            case EQUAL_OR_GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by direction with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getDirection().equals(direction);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByDate(Operator.Type type, String date) {
        switch (type) {
            case LOWER_THAN:
            case GREATER_THAN:
            case EQUAL_OR_LOWER_THAN:
            case EQUAL_OR_GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by date with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getDate().equals(date);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByUuid(Operator.Type type, String uuid) {
        switch (type) {
            case LOWER_THAN:
            case GREATER_THAN:
            case EQUAL_OR_LOWER_THAN:
            case EQUAL_OR_GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by uuid with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getUuid().equals(uuid);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> composedFilter(List<Predicate<ProtoSimplePublication.SimplePublication>> predicates) {
        return predicates.stream().reduce(Predicate::and).orElse(sp -> true);
    }
}
