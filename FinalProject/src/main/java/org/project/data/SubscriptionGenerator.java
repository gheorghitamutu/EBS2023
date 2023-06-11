package org.project.data;

import org.project.models.ProtoComplexSubscription;
import org.project.models.ProtoSimpleSubscription;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.project.data.RawData.CITIES;

public class SubscriptionGenerator {

    static final private List<String> subscribers = List.of(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
    );

    public static final Random RANDOM = new Random();

    private static SubscriptionGenerator instance = null;

    public ProtoSimpleSubscription.SimpleSubscription generateSimple() {
        return ProtoSimpleSubscription.SimpleSubscription.newBuilder()
                .setSubscriberId(subscribers.get(RANDOM.nextInt(subscribers.size())))
                .setSubscriptionId(randomUUID().toString())
                .setConditions(
                        ProtoSimpleSubscription.SimplePublicationCondition.newBuilder()
                                .setCity(
                                        ProtoSimpleSubscription.ConditionString.newBuilder()
                                                .setOperatorValue(ProtoSimpleSubscription.Operator.EQUAL_VALUE)
                                                .setValue(CITIES[RANDOM.nextInt(CITIES.length)])
                                                .build()
                                )
                                .build()
                )
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public ProtoComplexSubscription.ComplexSubscription generateComplex() {
        return ProtoComplexSubscription.ComplexSubscription.newBuilder()
                .setSubscriberId(subscribers.get(RANDOM.nextInt(subscribers.size())))
                .setSubscriptionId(randomUUID().toString())
                .setConditions(
                        ProtoComplexSubscription.ComplexPublicationCondition.newBuilder()
                                .setCity(
                                        ProtoComplexSubscription.ConditionString.newBuilder()
                                                .setOperatorValue(ProtoComplexSubscription.Operator.EQUAL_VALUE)
                                                .setValue(CITIES[RANDOM.nextInt(CITIES.length)])
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
