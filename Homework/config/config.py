from constants.constants import *

SUBSCRIPTIONS_COUNT = 2000
PUBLICATIONS_COUNT = 4000

FREQUENCY_OF_EQUALS_OPERATION_PER_CITIES_SUBSCRIPTIONS = 0.7

frequency_weights = {
    FieldsType.STATION_ID: 0.7,
    FieldsType.CITY: 0.9,
    FieldsType.TEMPERATURE: 0.3,
    FieldsType.RAIN: 0.4,
    FieldsType.WIND: 0.5,
    FieldsType.DIRECTION: 0.6,
    FieldsType.DATE: 0.7
}

complex_subscriptions_frequency = {
    ComplexPublication.CITY: 1,
    ComplexPublication.AVERAGE_TEMPERATURE: 0.9,
    ComplexPublication.AVERAGE_RAIN: 0.1,
    ComplexPublication.AVERAGE_WIND: 0.2
}
