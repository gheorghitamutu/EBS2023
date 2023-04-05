from filters.filters import *


class ComplexSubscription:
    def __init__(self):
        self.entities = {
            ComplexPublication.CITY: '',
            ComplexPublication.AVERAGE_TEMPERATURE: '',
            ComplexPublication.AVERAGE_RAIN: '',
            ComplexPublication.AVERAGE_WIND: ''
        }

        self.filters = {
            ComplexPublication.CITY: City(),
            ComplexPublication.AVERAGE_TEMPERATURE: AverageTemperature(),
            ComplexPublication.AVERAGE_RAIN: AverageRain(),
            ComplexPublication.AVERAGE_WIND: AverageWind()
        }

    def __str__(self) -> str:
        return f'{{{self.entities[ComplexPublication.CITY]}{self.entities[ComplexPublication.AVERAGE_TEMPERATURE]}' \
               f'{self.entities[ComplexPublication.AVERAGE_RAIN]}{self.entities[ComplexPublication.AVERAGE_WIND]}}}'

    def set_field(self, field_id):
        self.entities[field_id] = self.filters[field_id]

    def is_valid(self) -> bool:
        return bool(self.entities[ComplexPublication.CITY]) or \
            bool(self.entities[ComplexPublication.AVERAGE_TEMPERATURE]) or \
            bool(self.entities[ComplexPublication.AVERAGE_RAIN]) or \
            bool(self.entities[ComplexPublication.AVERAGE_WIND])

