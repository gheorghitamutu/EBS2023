import enum
from datetime import datetime, timedelta

SUBSCRIPTIONS_FILEPATH = "./output/subscriptions_file.txt"
PUBLICATIONS_FILEPATH = "./output/publications_file.txt"

STATION_IDS = (i for i in range(1, 9))
CITIES = ("Arad", "Timisoara", "Bucuresti", "Sibiu", "Brasov")
WIND_DIRECTIONS = ("N", "S", "W", "E", "NW", "NE", "SW", "SE")
NUMERIC_OPERATORS = (">", "<=", "<", "<=", "=")
STRING_OPERATORS = ("=", "!=")

MAXIMUM_TEMPERATURE = 45
MINIMUM_TEMPERATURE = -25

MAXIMUM_PROBABILITY = 1
MINIMUM_PROBABILITY = 0

MAXIMUM_WIND_SPEED_IN_KMS = 140
MINIMUM_WIND_SPEED_IN_KMS = 0

END_DATE = datetime.now()
START_DATE = END_DATE - timedelta(days=730)

MINIMUM_RAIN = 0.0
MAXIMUM_RAIN = 100.0


class FieldsType(enum.Enum):
    STATION_ID = 1
    CITY = 2
    TEMPERATURE = 3
    RAIN = 4
    WIND = 5
    DIRECTION = 6
    DATE = 7


class ComplexPublication(enum.Enum):
    CITY = 8
    AVERAGE_TEMPERATURE = 9
    AVERAGE_RAIN = 10
    AVERAGE_WIND = 11


class FilterValueType(enum.Enum):
    NUMERIC = 1
    STRING = 2


STATION_ID_FIELD_NAME = "stationId"
CITY_FIELD_NAME = "city"
TEMPERATURE_FIELD_NAME = "temp"
AVERAGE_TEMPERATURE_FIELD_NAME = "avg_temp"
RAIN_FIELD_NAME = "rain"
AVERAGE_RAIN_FIELD_NAME = "avg_rain"
WIND_FIELD_NAME = "wind"
AVERAGE_WIND_FIELD_NAME = "avg_wind"
DIRECTION_FIELD_NAME = "direction"
DATE_FIELD_NAME = "date"
