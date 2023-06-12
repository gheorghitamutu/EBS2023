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
    public static final Random RANDOM_CITY = new Random();

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
                                /*
                                .setCity(
                                        ProtoSimpleSubscription.ConditionString.newBuilder()
                                                .setOperatorValue(getSimpleRandomOperator(true).getNumber())
                                                .setValue(CITIES[RANDOM_CITY.nextInt(CITIES.length)])
                                                .build()
                                )
                                */
                                .setTemperature(
                                        ProtoSimpleSubscription.ConditionDouble.newBuilder()
                                                .setOperatorValue(getSimpleRandomOperator(false, true).getNumber())
                                                .setValue(RANDOM.nextDouble() * 50 * (RANDOM.nextDouble() > 0.5 ? 1 : -1))
                                                // .setValue(0)
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
                                                .setOperatorValue(getComplexRandomOperator(true).getNumber())
                                                .setValue(CITIES[RANDOM.nextInt(CITIES.length)])
                                                .build()
                                )
                                .build()
                )
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public ProtoSimpleSubscription.Operator getSimpleRandomOperator(boolean onlyEqual, boolean noNone) {
        if (simplePublicationsGenerated % SUBSCRIPTION_EQUAL_OPERATOR_PERCENTAGE == 0) {
            return ProtoSimpleSubscription.Operator.EQUAL;
        }
        if (onlyEqual) {
            return ProtoSimpleSubscription.Operator.NONE;
        }

        var r = RANDOM.nextInt(ProtoSimpleSubscription.Operator.values().length - 1);
        while(noNone) {
            if (r == ProtoSimpleSubscription.Operator.NONE.getNumber()) {
                r = RANDOM.nextInt(ProtoSimpleSubscription.Operator.values().length - 1);
            } else {
                noNone = false;
            }
        }
        return ProtoSimpleSubscription.Operator.forNumber(r);
    }

    public static ProtoComplexSubscription.Operator getComplexRandomOperator(boolean onlyEqual) {
        if (complexPublicationsGenerated % SUBSCRIPTION_EQUAL_OPERATOR_PERCENTAGE == 0) {
            return ProtoComplexSubscription.Operator.EQUAL;
        }
        if (onlyEqual) {
            return ProtoComplexSubscription.Operator.NONE;
        }
        var r = RANDOM.nextInt(ProtoComplexSubscription.Operator.values().length - 1);
        return ProtoComplexSubscription.Operator.forNumber(r);
    }

    public static SubscriptionGenerator getInstance() {
        if (instance == null) {
            instance = new SubscriptionGenerator();
        }
        return instance;
    }
}
