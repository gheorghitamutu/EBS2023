package org.project.filters;

import org.project.models.ProtoSimplePublication;
import org.project.models.ProtoSimpleSubscription;

import java.util.List;
import java.util.function.Predicate;

public class SimplePublicationFilter {

    public static Predicate<ProtoSimplePublication.SimplePublication> filter(ProtoSimpleSubscription.SimpleSubscription subscription) {
        return simplePublication -> {
            boolean isValid = filterByCity(ProtoSimpleSubscription.Operator.values()[subscription.getConditions().getCity().getOperatorValue()], subscription.getConditions().getCity().getValue()).test(simplePublication);
            if (!filterByRain(ProtoSimpleSubscription.Operator.values()[subscription.getConditions().getRain().getOperatorValue()], subscription.getConditions().getRain().getValue()).test(simplePublication)) {
                isValid = false;
            }
            if (!filterByTemperature(ProtoSimpleSubscription.Operator.values()[subscription.getConditions().getTemperature().getOperatorValue()], subscription.getConditions().getTemperature().getValue()).test(simplePublication)) {
                isValid = false;
            }
            if (!filterByWind(ProtoSimpleSubscription.Operator.values()[subscription.getConditions().getWind().getOperatorValue()], subscription.getConditions().getWind().getValue()).test(simplePublication)) {
                isValid = false;
            }

            return isValid;
        };
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByStationId(ProtoSimpleSubscription.Operator type, String stationId) {
        switch (type) {
            case NONE:
                return (sp) -> true;
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

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByCity(ProtoSimpleSubscription.Operator type, String city) {
        switch (type) {
            case NONE:
                return (sp) -> true;
            case LOWER_THAN:
            case GREATER_THAN:
            case EQUAL_OR_LOWER_THAN:
            case EQUAL_OR_GREATER_THAN:
                throw new IllegalArgumentException("Cannot filter by city with HIGHER/LOWER operator!");
            case EQUAL:
                return (sp) -> sp.getCity().equals(city);
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByTemperature(ProtoSimpleSubscription.Operator type, double temperature) {
        switch (type) {
            case NONE:
                return (sp) -> true;
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

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByRain(ProtoSimpleSubscription.Operator type, double rain) {
        switch (type) {
            case NONE:
                return (sp) -> true;
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

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByWind(ProtoSimpleSubscription.Operator type, double wind) {
        switch (type) {
            case NONE:
                return (sp) -> true;
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

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByDirection(ProtoSimpleSubscription.Operator type, String direction) {
        switch (type) {
            case NONE:
                return (sp) -> true;
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

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByDate(ProtoSimpleSubscription.Operator type, long timestamp) {
        switch (type) {
            case NONE:
                return (sp) -> true;
            case LOWER_THAN:
                return (sp) -> sp.getDateTimestamp() < timestamp;
            case EQUAL_OR_LOWER_THAN:
                return (sp) -> sp.getDateTimestamp() <= timestamp;
            case EQUAL:
                return (sp) -> sp.getDateTimestamp() == timestamp;
            case EQUAL_OR_GREATER_THAN:
                return (sp) -> sp.getDateTimestamp() >= timestamp;
            case GREATER_THAN:
                return (sp) -> sp.getDateTimestamp() > timestamp;
            default:
                throw new IllegalArgumentException("Unknown operator!");
        }
    }

    public static Predicate<ProtoSimplePublication.SimplePublication> filterByUuid(ProtoSimpleSubscription.Operator type, String uuid) {
        switch (type) {
            case NONE:
                return (sp) -> true;
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
