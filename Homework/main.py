import threading
import time

from generators.publication import generate_publication
from generators.subscription import generate_simple_subscription, generate_complex_subscription
from config.config import SUBSCRIPTIONS_COUNT, PUBLICATIONS_COUNT

WORKERS_NO = 6

simple_subscriptions = []
complex_subscriptions = []
publications = []
lock = threading.Lock()


class PublicationWorker(threading.Thread):
    def __init__(self, worker_id):
        threading.Thread.__init__(self)
        self.worker_id = worker_id

    def run(self):
        while len(publications) < PUBLICATIONS_COUNT:
            with lock:
                publication = generate_publication(len(publications))
                publications.append(publication)


class SimpleSubscriptionWorker(threading.Thread):
    def __init__(self, worker_id):
        threading.Thread.__init__(self)
        self.worker_id = worker_id

    def run(self):
        while len(simple_subscriptions) < SUBSCRIPTIONS_COUNT:
            with lock:
                simple_subscription = generate_simple_subscription()
                simple_subscriptions.append(simple_subscription)


class ComplexSubscriptionWorker(threading.Thread):
    def __init__(self, worker_id):
        threading.Thread.__init__(self)
        self.worker_id = worker_id

    def run(self):
        while len(complex_subscriptions) < SUBSCRIPTIONS_COUNT:
            with lock:
                complex_subscription = generate_complex_subscription()
                complex_subscriptions.append(complex_subscription)


if __name__ == '__main__':

    publication_workers = []
    for i in range(0, WORKERS_NO):
        publication_workers.append(PublicationWorker(i))

    for worker in publication_workers:
        worker.start()

    simple_subscription_workers = []
    for i in range(0, WORKERS_NO):
        simple_subscription_workers.append(SimpleSubscriptionWorker(i))

    for worker in simple_subscription_workers:
        worker.start()

    complex_subscription_workers = []
    for i in range(0, WORKERS_NO):
        complex_subscription_workers.append(ComplexSubscriptionWorker(i))

    for worker in complex_subscription_workers:
        worker.start()
