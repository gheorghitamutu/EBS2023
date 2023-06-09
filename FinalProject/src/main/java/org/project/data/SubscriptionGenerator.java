package org.project.data;

import org.project.models.ProtoComplexSubscription;
import org.project.models.ProtoSimpleSubscription;

import static java.util.UUID.randomUUID;

public class SubscriptionGenerator {
    private static SubscriptionGenerator instance = null;

    public ProtoSimpleSubscription.SimpleSubscription generateSimple() {
        return ProtoSimpleSubscription.SimpleSubscription.newBuilder()
                .setSubscriptionId(randomUUID().toString())
                .setConditions(
                        ProtoSimpleSubscription.SimplePublicationCondition.newBuilder()
                                .setCity(
                                        ProtoSimpleSubscription.ConditionString.newBuilder()
                                                .setOperatorValue(ProtoSimpleSubscription.Operator.EQUAL_VALUE)
                                                .setValue(new City(City.Name.SAN_FRANCISCO).ToString())
                                                .build()
                                )
                                .build()
                )
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public ProtoComplexSubscription.ComplexSubscription generateComplex() {
        return ProtoComplexSubscription.ComplexSubscription.newBuilder()
                .setSubscriptionId(randomUUID().toString())
                .setConditions(
                        ProtoComplexSubscription.ComplexPublicationCondition.newBuilder()
                                .setCity(
                                        ProtoComplexSubscription.ConditionString.newBuilder()
                                                .setOperatorValue(ProtoComplexSubscription.Operator.EQUAL_VALUE)
                                                .setValue(new City(City.Name.SAN_FRANCISCO).ToString())
                                                .build()
                                )
                                .build()
                )
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public static SubscriptionGenerator getInstance() {
        if (instance == null) {
            instance = new SubscriptionGenerator();
        }
        return instance;
    }
}
