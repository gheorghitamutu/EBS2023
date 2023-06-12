package org.project.data;

import org.project.models.ProtoComplexSubscription;
import org.project.models.ProtoSimpleSubscription;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.project.cofiguration.GlobalConfiguration.SUBSCRIPTION_EQUAL_OPERATOR_PERCENTAGE;
import static org.project.data.RawData.CITIES;

public class SubscriptionGenerator {

    static final private List<String> subscribers = List.of(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
    );

    public static final Random RANDOM = new Random();

    public static int simplePublicationsGenerated = 0;
    public static int complexPublicationsGenerated = 0;

    private static SubscriptionGenerator instance = null;

    public ProtoSimpleSubscription.SimpleSubscription generateSimple() {
        simplePublicationsGenerated++;
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
                                .setTemperature(
                                        ProtoSimpleSubscription.ConditionDouble.newBuilder()
                                                .setOperatorValue(getRandomOperator().getNumber())
                                                .setValue(RANDOM.nextDouble() * 50 * (RANDOM.nextDouble() > 0.5 ? 1 : -1))
                                                .build()
                                )
                                .build()
                )
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public ProtoComplexSubscription.ComplexSubscription generateComplex() {
        complexPublicationsGenerated++;
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

    public static ProtoComplexSubscription.Operator getRandomOperator() {
        if (complexPublicationsGenerated % SUBSCRIPTION_EQUAL_OPERATOR_PERCENTAGE == 0) {
            return ProtoComplexSubscription.Operator.EQUAL;
        }
        return ProtoComplexSubscription.Operator.forNumber(RANDOM.nextInt(ProtoComplexSubscription.Operator.values().length));
    }

    public static SubscriptionGenerator getInstance() {
        if (instance == null) {
            instance = new SubscriptionGenerator();
        }
        return instance;
    }
}
