package org.project.filters;

import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoSimplePublication;

import java.util.List;
import java.util.function.Predicate;

public class ComplexPublicationFilter {
    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByUUID(Operator.Type type, String uuid) {
        switch (type) {
            case LOWER:
            case HIGHER:
                throw new IllegalArgumentException("Cannot filter by UUID with HIGHER/LOWER operator!");
            case EQUAL:
                return (cp) -> cp.getUuid().equals(uuid);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByCity(Operator.Type type, String city) {
        switch (type) {
            case LOWER:
            case HIGHER:
                throw new IllegalArgumentException("Cannot filter by city with HIGHER/LOWER operator!");
            case EQUAL:
                return (cp) -> cp.getCity().equals(city);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByAvgTemperature(Operator.Type type, double avgTemperature) {
        switch (type) {
            case LOWER:
                return (cp) -> cp.getAvgTemperature() < avgTemperature;
            case EQUAL:
                return (cp) -> cp.getAvgTemperature() == avgTemperature;
            case HIGHER:
                return (cp) -> cp.getAvgTemperature() > avgTemperature;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByAvgRain(Operator.Type type, double avgRain) {
        switch (type) {
            case LOWER:
                return (cp) -> cp.getAvgRain() < avgRain;
            case EQUAL:
                return (cp) -> cp.getAvgRain() == avgRain;
            case HIGHER:
                return (cp) -> cp.getAvgRain() > avgRain;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByAvgWind(Operator.Type type, double avgWind) {
        switch (type) {
            case LOWER:
                return (cp) -> cp.getAvgWind() < avgWind;
            case EQUAL:
                return (cp) -> cp.getAvgWind() == avgWind;
            case HIGHER:
                return (cp) -> cp.getAvgWind() > avgWind;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> composedFilter(List<Predicate<ProtoComplexPublication.ComplexPublication>> predicates) {
        return predicates.stream().reduce(Predicate::and).orElse(cp -> true);
    }
}
