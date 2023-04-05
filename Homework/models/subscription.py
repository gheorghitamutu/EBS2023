from filters.filters import *


class Subscription:
    def __init__(self):

        self.entities = {
            FieldsType.STATION_ID: '',
            FieldsType.CITY: '',
            FieldsType.TEMPERATURE: '',
            FieldsType.RAIN: '',
            FieldsType.WIND: '',
            FieldsType.DIRECTION: '',
            FieldsType.DATE: '',
        }

        self.filters = {
            FieldsType.STATION_ID: StationId(),
            FieldsType.CITY: City(),
            FieldsType.TEMPERATURE: Temperature(),
            FieldsType.RAIN: Rain(),
            FieldsType.WIND: Wind(),
            FieldsType.DIRECTION: Direction(),
            FieldsType.DATE: Date(),
        }

    def __str__(self) -> str:
        return f'{{{self.entities[FieldsType.STATION_ID]}{self.entities[FieldsType.CITY]}' \
               f'{self.entities[FieldsType.TEMPERATURE]}{self.entities[FieldsType.RAIN]}' \
               f'{self.entities[FieldsType.WIND]}'f'{self.entities[FieldsType.DIRECTION]}' \
               f'{self.entities[FieldsType.DATE]}}}'

    def set_filter(self, entity_type: FieldsType):
        self.entities[entity_type] = self.filters[entity_type]

    def is_valid(self) -> bool:
        return bool(self.entities[FieldsType.STATION_ID]) or bool(self.entities[FieldsType.CITY]) or \
            bool(self.entities[FieldsType.TEMPERATURE]) or bool(self.entities[FieldsType.RAIN]) or \
            bool(self.entities[FieldsType.WIND]) or bool(self.entities[FieldsType.DIRECTION]) or \
            bool(self.entities[FieldsType.DATE])

