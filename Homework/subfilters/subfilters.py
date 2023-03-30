from constants.constants import *
from config.config import FREQUENCY_OF_EQUALS_OPERATION_PER_CITIES_SUBSCRIPTIONS
from numpy import random  # binomial


class Field:
    """
        Base class used for filters
    """
    def __init__(self, name):
        self.name = name
        self.value = None
        self.value_type = None
        self.operator = None

    def __str__(self) -> str:
        if any([self.operator, self.value, self.value_type]) is None:
            raise Exception("Invalid input!")

        if self.value_type is FilterValueType.NUMERIC:
            return f'({self.name},{self.operator},{self.value});'

        return f'({self.name},{self.operator},"{self.value}");'


class Numeric(Field):
    def __init__(self, name):
        super(Numeric, self).__init__(name)
        self.operator = NUMERIC_OPERATORS[random.randint(0, len(NUMERIC_OPERATORS))]
        self.value_type = FilterValueType.NUMERIC


class String(Field):
    def __init__(self, name):
        super(String, self).__init__(name)
        self.operator = STRING_OPERATORS[random.randint(0, len(STRING_OPERATORS))]
        self.value_type = FilterValueType.STRING


class StationId(Numeric):
    def __init__(self):
        super(StationId, self).__init__(STATION_ID_FIELD_NAME)
        self.value = STATION_IDS[random.randint(0, len(STATION_IDS))]


class City(String):
    def __init__(self):
        super(City, self).__init__(CITY_FIELD_NAME)
        chance = random.binomial(1, FREQUENCY_OF_EQUALS_OPERATION_PER_CITIES_SUBSCRIPTIONS, size=None)
        if chance:
            self.operator = "="
        self.value = CITIES[random.randint(0, len(CITIES))]


class Temperature(Numeric):
    def __init__(self):
        super(Temperature, self).__init__(TEMPERATURE_FIELD_NAME)
        self.value = random.randint(MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE)


class AverageTemperature(Numeric):
    def __init__(self):
        super(AverageTemperature, self).__init__(AVERAGE_TEMPERATURE_FIELD_NAME)
        self.value = random.randint(MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE)


class Rain(Numeric):
    def __init__(self):
        super(Rain, self).__init__(RAIN_FIELD_NAME)
        self.value = float(random.randint(MINIMUM_TEMPERATURE, MAXIMUM_RAIN)) / 100
        self.operator = "="


class AverageRain(Numeric):
    def __init__(self):
        super(AverageRain, self).__init__(AVERAGE_RAIN_FIELD_NAME)
        self.value = float(random.randint(MINIMUM_TEMPERATURE, MAXIMUM_RAIN)) / 100


class Wind(Numeric):
    def __init__(self):
        super(Wind, self).__init__(WIND_FIELD_NAME)
        self.value = random.randint(MINIMUM_WIND_SPEED_IN_KMS, MAXIMUM_WIND_SPEED_IN_KMS)
        self.operator = "="


class AverageWind(Numeric):
    def __init__(self):
        super(AverageWind, self).__init__(AVERAGE_WIND_FIELD_NAME)
        self.value = random.randint(MINIMUM_WIND_SPEED_IN_KMS, MAXIMUM_WIND_SPEED_IN_KMS)
        self.operator = "="


class Direction(String):
    def __init__(self):
        super(Direction, self).__init__(DIRECTION_FIELD_NAME)
        self.value = WIND_DIRECTIONS[random.randint(0, len(WIND_DIRECTIONS))]


class Date(Numeric):
    def __init__(self):
        super(Date, self).__init__(DATE_FIELD_NAME)
        delta_date = END_DATE - START_DATE
        delta_days = delta_date.days
        days_number = random.randint(0, delta_days)
        date = START_DATE + days_number
        self.value = date
