from constants.constants import STATION_ID_FIELD_NAME, CITY_FIELD_NAME, TEMPERATURE_FIELD_NAME, RAIN_FIELD_NAME, \
    WIND_FIELD_NAME, DIRECTION_FIELD_NAME, DATE_FIELD_NAME


class Publication:
    def __init__(self, station_id, city, temp, rain, wind, direction, date):
        self.station_id = station_id
        self.city = city
        self.temp = temp
        self.rain = rain
        self.wind = wind
        self.direction = direction
        self.date = date

    def __str__(self):
        return f'{{({STATION_ID_FIELD_NAME},{self.station_id}),({CITY_FIELD_NAME},\"{self.city}\"),' \
               f'({TEMPERATURE_FIELD_NAME},{self.temp}),({RAIN_FIELD_NAME},{self.rain}),' \
               f'({WIND_FIELD_NAME},{self.wind}),({DIRECTION_FIELD_NAME},\"{self.direction}\"),' \
               f'({DATE_FIELD_NAME},{self.date})}}'
