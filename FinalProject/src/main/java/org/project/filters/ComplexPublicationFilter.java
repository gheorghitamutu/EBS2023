package org.project.filters;

import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoComplexSubscription;

import java.util.List;
import java.util.function.Predicate;

public class ComplexPublicationFilter {


    public static Predicate<ProtoComplexPublication.ComplexPublication> filter(ProtoComplexSubscription.ComplexSubscription subscription) {
        return complexPublication -> {
            boolean isValid = filterByCity(ProtoComplexSubscription.Operator.values()[subscription.getConditions().getCity().getOperatorValue()], subscription.getConditions().getCity().getValue()).test(complexPublication);
            if (!filterByAvgRain(ProtoComplexSubscription.Operator.values()[subscription.getConditions().getAverageRain().getOperatorValue()], subscription.getConditions().getAverageRain().getValue()).test(complexPublication)) {
                isValid = false;
            }
            if (!filterByAvgTemperature(ProtoComplexSubscription.Operator.values()[subscription.getConditions().getAverageTemperature().getOperatorValue()], subscription.getConditions().getAverageTemperature().getValue()).test(complexPublication)) {
                isValid = false;
            }
            if (!filterByAvgWind(ProtoComplexSubscription.Operator.values()[subscription.getConditions().getAverageWind().getOperatorValue()], subscription.getConditions().getAverageWind().getValue()).test(complexPublication)) {
                isValid = false;
            }

            return isValid;
        };
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByUUID(ProtoComplexSubscription.Operator type, String uuid) {
        switch (type) {
            case NONE:
                return (cp) -> true;
            case LOWER_THAN:
            case GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by UUID with HIGHER/LOWER operator!");
            case EQUAL:
                return (cp) -> cp.getUuid().equals(uuid);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByCity(ProtoComplexSubscription.Operator type, String city) {
        switch (type) {
            case NONE:
                return (cp) -> true;
            case LOWER_THAN:
            case GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by city with HIGHER/LOWER operator!");
            case EQUAL:
                return (cp) -> cp.getCity().equals(city);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByAvgTemperature(ProtoComplexSubscription.Operator type, double avgTemperature) {
        switch (type) {
            case NONE:
                return (cp) -> true;
            case LOWER_THAN:
                return (cp) -> cp.getAvgTemperature() < avgTemperature;
            case EQUAL_OR_LOWER_THAN:
                return (cp) -> cp.getAvgTemperature() <= avgTemperature;
            case EQUAL:
                return (cp) -> cp.getAvgTemperature() == avgTemperature;
            case EQUAL_OR_GREATER_THAN:
                return (cp) -> cp.getAvgTemperature() >= avgTemperature;
            case GREATER_THAN:
                return (cp) -> cp.getAvgTemperature() > avgTemperature;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByAvgRain(ProtoComplexSubscription.Operator type, double avgRain) {
        switch (type) {
            case NONE:
                return (cp) -> true;
            case LOWER_THAN:
                return (cp) -> cp.getAvgRain() < avgRain;
            case EQUAL_OR_LOWER_THAN:
                return (cp) -> cp.getAvgRain() <= avgRain;
            case EQUAL:
                return (cp) -> cp.getAvgRain() == avgRain;
            case EQUAL_OR_GREATER_THAN:
                return (cp) -> cp.getAvgRain() >= avgRain;
            case GREATER_THAN:
                return (cp) -> cp.getAvgRain() > avgRain;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> filterByAvgWind(ProtoComplexSubscription.Operator type, double avgWind) {
        switch (type) {
            case NONE:
                return (cp) -> true;
            case LOWER_THAN:
                return (cp) -> cp.getAvgWind() < avgWind;
            case EQUAL_OR_LOWER_THAN:
                return (cp) -> cp.getAvgWind() <= avgWind;
            case EQUAL:
                return (cp) -> cp.getAvgWind() == avgWind;
            case EQUAL_OR_GREATER_THAN:
                return (cp) -> cp.getAvgWind() >= avgWind;
            case GREATER_THAN:
                return (cp) -> cp.getAvgWind() > avgWind;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoComplexPublication.ComplexPublication> composedFilter(List<Predicate<ProtoComplexPublication.ComplexPublication>> predicates) {
        return predicates.stream().reduce(Predicate::and).orElse(cp -> true);
    }
}
