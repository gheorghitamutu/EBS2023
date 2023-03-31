# EBS Homework

- [EBS Homework](#ebs-homework)
- [System description](#system-description)
  - [Project Structure](#project-structure)
    - [config](#config)
    - [constants](#constants)
    - [filters](#filters)
    - [generators](#generators)
    - [models](#models)
    - [output](#output)
    - [main](#main)
- [Tests](#tests)
  - [Iterative](#iterative)
  - [Simple Multithreading](#simple-multithreading)
    - [2 workers](#2-workers)
    - [4 workers](#4-workers)
    - [6 workers](#6-workers)
    - [Conclusion](#conclusion)
  - [MQTT (6 workers: 3 publishers/3 subscribers)](#mqtt-6-workers-3-publishers3-subscribers)
    - [Latency subscriber \<=\> publisher (using Mosquitto on WSL)](#latency-subscriber--publisher-using-mosquitto-on-wsl)


# System description

Python language was used.

## Project Structure
### config
Keeps all the configuration in place (number of subscriptions/publications, weights,
field names, etc).

    SUBSCRIPTIONS_COUNT = 500000
    PUBLICATIONS_COUNT = 500000
    
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

### constants

All the constants used in the project with several enum fields and boundaries.

    SIMPLE_SUBSCRIPTIONS_FILEPATH = "simple_subscriptions_file.txt"
    COMPLEX_SUBSCRIPTIONS_FILEPATH = "complex_subscriptions_file.txt"
    PUBLICATIONS_FILEPATH = "publications_file.txt"
    
    STATION_IDS = [i for i in range(1, 9)]
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
    
    
    STATION_ID_FIELD_NAME = "station_id"
    CITY_FIELD_NAME = "city"
    TEMPERATURE_FIELD_NAME = "temperature"
    AVERAGE_TEMPERATURE_FIELD_NAME = "average_temperature"
    RAIN_FIELD_NAME = "rain"
    AVERAGE_RAIN_FIELD_NAME = "average_rain"
    WIND_FIELD_NAME = "wind"
    AVERAGE_WIND_FIELD_NAME = "average_wind"
    DIRECTION_FIELD_NAME = "direction"
    DATE_FIELD_NAME = "date"

### filters

All the filters that based on the corresponding weigths applied to
subscriptions & publications.
    
    # example:

    class City(String):
        def __init__(self):
            super(City, self).__init__(CITY_FIELD_NAME)
            chance = random.binomial(1,     FREQUENCY_OF_EQUALS_OPERATION_PER_CITIES_SUBSCRIPTIONS,     size=None)
            if chance:
                self.operator = "="
            self.value = CITIES[random.randint(0, len(CITIES))]

### generators
Generaters of publications, simple subscriptions and complex subscriptions.

    # example:
    
    def generate_publication(counter):
        return Publication(
            counter,
            random.sample(CITIES, 1)[0],
            random.randint(MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE),
            random.uniform(MINIMUM_RAIN, MAXIMUM_RAIN),
            random.randint(MINIMUM_WIND_SPEED_IN_KMS,     MAXIMUM_WIND_SPEED_IN_KMS),
            random.sample(WIND_DIRECTIONS, 1)[0],
            get_random_data()
        )

### models

Models for publication, simple subscription and complex subscription along with their string serialization:

    # example:

    class Publication:
        def __init__(self, station_id, city, temp, rain, wind, direction,     date):
            self.station_id = station_id
            self.city = city
            self.temp = temp
            self.rain = rain
            self.wind = wind
            self.direction = direction
            self.date = date
    
        def __str__(self):
            return f'{{({STATION_ID_FIELD_NAME},{self.station_id}),    ({CITY_FIELD_NAME},\"{self.city}\"),' \
                   f'({TEMPERATURE_FIELD_NAME},{self.temp}),    ({RAIN_FIELD_NAME},{self.rain}),' \
                   f'({WIND_FIELD_NAME},{self.wind}),    ({DIRECTION_FIELD_NAME},\"{self.direction}\"),' \
                   f'({DATE_FIELD_NAME},{self.date})}}'

### output
Output folder if you want to serialize the data in files.

### main
There are 2 main files:
- one for iterative and multithreaded testing
- one for mqtt testing

# Tests
## Iterative
Execution time generating 500000 publications: 3.895294189453125 seconds.

Execution time generating 500000 simple subscriptions: 39.1939582824707 seconds.

Execution time generating 500000 complex subscriptions: 23.60188055038452 seconds.

## Simple Multithreading

### 2 workers
Execution time generating 500000 publications: 4.480447053909302 seconds.

Execution time generating 500000 simple subscriptions: 35.587207078933716 seconds.

Execution time generating 500000 complex subscriptions: 22.07077670097351 seconds.

### 4 workers
Execution time generating 500000 publications: 4.574521064758301 seconds.

Execution time generating 500000 simple subscriptions: 35.93336081504822 seconds.

Execution time generating 500000 complex subscriptions: 21.578312397003174 seconds.


### 6 workers
Execution time generating 500000 publications: 4.2702858448028564 seconds.

Execution time generating 500000 simple subscriptions: 36.258774757385254 seconds.

Execution time generating 500000 complex subscriptions: 21.566722631454468 seconds.

### Conclusion
The Python Global Interpreter Lock or GIL, in simple words, is a mutex (or a lock) 
that allows only one thread to hold the control of the Python interpreter.

Thus, doing this task multithreading while also having a lock on the final list doesn't
really help us with anything.

## MQTT (6 workers: 3 publishers/3 subscribers)
### Latency subscriber <=> publisher (using Mosquitto on WSL)

Each worker is on a different thread.

Messages count: 50.000

Latencies: [1604, 2698, 2134] 

Messages: [8897, 26806, 13976]

Medium latency worker #0: 0.1802854894908396

Medium latency worker #1: 0.10064910840856525

Medium latency worker #2: 0.1526903262736119

