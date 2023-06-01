package org.project.filters;

import com.google.common.collect.Maps;
import org.project.models.ProtoSimplePublication;

import java.util.List;
import java.util.function.Predicate;

public class SimplePublicationFilter {

    final List<Predicate<ProtoSimplePublication.SimplePublication>> predicates;

    public SimplePublicationFilter(Maps.EntryTransformer<Operator.Type, String, Predicate<ProtoSimplePublication.SimplePublication>> predicates) {

        Maps.EntryTransformer<Operator.Type, String, Predicate<ProtoSimplePublication.SimplePublication>> filterByCity = (type, city) -> type != null ? filterByCity(type, city) : null;
        this.predicates = predicates;
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByStationId(Operator.Type type, String stationId) {
        switch (type) {
            case LOWER:
            case HIGHER:
                throw new IllegalArgumentException("Cannot filter by stationId with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getStationId().equals(stationId);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByCity(Operator.Type type, String city) {
        switch (type) {
            case LOWER:
            case HIGHER:
                throw new IllegalArgumentException("Cannot filter by city with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getCity().equals(city);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByTemperature(Operator.Type type, double temperature) {
        switch (type) {
            case LOWER:
                return (sp) -> sp.getTemperature() < temperature;
            case EQUAL:
                return (sp) -> sp.getTemperature() == temperature;
            case HIGHER:
                return (sp) -> sp.getTemperature() > temperature;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByRain(Operator.Type type, double rain) {
        switch (type) {
            case LOWER:
                return (sp) -> sp.getRain() < rain;
            case EQUAL:
                return (sp) -> sp.getRain() == rain;
            case HIGHER:
                return (sp) -> sp.getRain() > rain;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByWind(Operator.Type type, double wind) {
        switch (type) {
            case LOWER:
                return (sp) -> sp.getWind() < wind;
            case EQUAL:
                return (sp) -> sp.getWind() == wind;
            case HIGHER:
                return (sp) -> sp.getWind() > wind;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByDirection(Operator.Type type, String direction) {
        switch (type) {
            case LOWER:
            case HIGHER:
                throw new IllegalArgumentException("Cannot filter by direction with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getDirection().equals(direction);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByDate(Operator.Type type, String date) {
        switch (type) {
            case LOWER:
            case HIGHER:
                throw new IllegalArgumentException("Cannot filter by date with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getDate().equals(date);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByUuid(Operator.Type type, String uuid) {
        switch (type) {
            case LOWER:
            case HIGHER:
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
