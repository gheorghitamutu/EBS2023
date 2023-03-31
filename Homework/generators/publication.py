from datetime import date
import random

from config.config import PUBLICATIONS_COUNT
from models.publication import Publication
from constants.constants import *


def generate_publication(counter):
    return Publication(
        counter,
        random.sample(CITIES, 1)[0],
        random.randint(MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE),
        random.uniform(MINIMUM_RAIN, MAXIMUM_RAIN),
        random.randint(MINIMUM_WIND_SPEED_IN_KMS, MAXIMUM_WIND_SPEED_IN_KMS),
        random.sample(WIND_DIRECTIONS, 1)[0],
        get_random_data()
    )


def get_random_data():
    end_date = datetime.now()
    start_date = end_date - timedelta(days=180)

    delta_date = end_date - start_date
    days_count = random.randrange(delta_date.days)

    return start_date + timedelta(days=days_count)


def generate_publications():
    return [generate_publication(i) for i in range(0, PUBLICATIONS_COUNT)]


def generate():
    for publication in [str(generate_publication(i)) for i in range(0, PUBLICATIONS_COUNT)]:
        print(publication)

    with open(PUBLICATIONS_FILEPATH, "w") as f:
        f.writelines([f'{str(generate_publication(i))}\n' for i in range(0, PUBLICATIONS_COUNT)])


if __name__ == '__main__':
    generate()
