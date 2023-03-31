from config.config import *
from models.subscription import Subscription
from models.complex_subscription import ComplexSubscription
from numpy import random


def generate_complex_subscription():
    while True:
        complex_subscription = ComplexSubscription()

        for field in ComplexPublication:

            probability_to_use_the_field = complex_subscriptions_frequency[field]
            if random.binomial(1, probability_to_use_the_field, size=None):
                complex_subscription.set_field(field)

        if complex_subscription.is_valid():
            break

    return complex_subscription


def generate_simple_subscription():
    while True:
        subscription = Subscription()

        for field in FieldsType:
            probability_of_using_the_filter = frequency_weights[field]
            if random.binomial(1, probability_of_using_the_filter, size=None):
                subscription.set_filter(field)

        if subscription.is_valid():
            break

    return subscription


def generate_simple_subscriptions():
    subscriptions = []

    for i in range(SUBSCRIPTIONS_COUNT):
        subscription = generate_simple_subscription()
        subscriptions.append(subscription)

    # for s in [str(s) for s in subscriptions]:
    #     print(s)

    with open(SIMPLE_SUBSCRIPTIONS_FILEPATH, "w") as f:
        f.writelines([str(s) for s in subscriptions])


def generate_complex_subscriptions():
    subscriptions = []

    for i in range(SUBSCRIPTIONS_COUNT):
        subscription = generate_complex_subscription()
        subscriptions.append(subscription)

    # for s in [str(s) for s in subscriptions]:
    #     print(s)

    with open(COMPLEX_SUBSCRIPTIONS_FILEPATH, "w") as f:
        f.writelines([str(s) for s in subscriptions])


if __name__ == "__main__":
    generate_simple_subscriptions()
    generate_complex_subscriptions()
