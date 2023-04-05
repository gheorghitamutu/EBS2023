import threading
import time
from pathos.multiprocessing import ProcessingPool as Pool

from generators.publication import generate_publication, generate_publications
from generators.subscription import generate_simple_subscription, generate_complex_subscription, \
    generate_complex_subscriptions, generate_simple_subscriptions
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
        self.worker_publications_count = PUBLICATIONS_COUNT // WORKERS_NO
        self.publications = list()

    def run(self):

        item_count = 0
        while item_count < self.worker_publications_count:
            self.publications.append(generate_publication(len(publications)))
            item_count = item_count + 1

        with lock:
            publications.extend(self.publications)


class SimpleSubscriptionWorker(threading.Thread):
    def __init__(self, worker_id):
        threading.Thread.__init__(self)
        self.worker_id = worker_id
        self.worker_simple_subscriptions_count = SUBSCRIPTIONS_COUNT // WORKERS_NO
        self.simple_subscriptions = list()

    def run(self):
        item_count = 0
        while item_count < self.worker_simple_subscriptions_count:
            self.simple_subscriptions.append(generate_simple_subscription())
            item_count = item_count + 1

        with lock:
            simple_subscriptions.extend(self.simple_subscriptions)


class ComplexSubscriptionWorker(threading.Thread):
    def __init__(self, worker_id):
        threading.Thread.__init__(self)
        self.worker_id = worker_id
        self.worker_complex_subscriptions_count = SUBSCRIPTIONS_COUNT // WORKERS_NO
        self.complex_subscriptions = list()

    def run(self):
        item_count = 0
        while item_count < self.worker_complex_subscriptions_count:
            self.complex_subscriptions.append(generate_complex_subscription())
            item_count = item_count + 1

        with lock:
            complex_subscriptions.extend(self.complex_subscriptions)


def do_iterative_tests():

    st = time.time()
    _ = generate_publications()
    et = time.time()
    elapsed_time = et - st
    print(f'Execution time generating {PUBLICATIONS_COUNT} publications: {elapsed_time} seconds.')

    st = time.time()
    _ = generate_simple_subscriptions()
    et = time.time()
    elapsed_time = et - st
    print(f'Execution time generating {SUBSCRIPTIONS_COUNT} simple subscriptions: {elapsed_time} seconds.')

    st = time.time()
    _ = generate_complex_subscriptions()
    et = time.time()
    elapsed_time = et - st
    print(f'Execution time generating {SUBSCRIPTIONS_COUNT} complex subscriptions: {elapsed_time} seconds.')


def do_multi_threaded_tests():

    st = time.time()
    publication_workers = []
    for i in range(0, WORKERS_NO):
        publication_workers.append(PublicationWorker(i))

    for worker in publication_workers:
        worker.start()

    for worker in publication_workers:
        worker.join()
    et = time.time()
    elapsed_time = et - st
    print(f'Execution time generating {PUBLICATIONS_COUNT} publications: {elapsed_time} seconds.')

    st = time.time()
    simple_subscription_workers = []
    for i in range(0, WORKERS_NO):
        simple_subscription_workers.append(SimpleSubscriptionWorker(i))

    for worker in simple_subscription_workers:
        worker.start()

    for worker in simple_subscription_workers:
        worker.join()

    et = time.time()
    elapsed_time = et - st
    print(f'Execution time generating {SUBSCRIPTIONS_COUNT} simple subscriptions: {elapsed_time} seconds.')

    st = time.time()
    complex_subscription_workers = []
    for i in range(0, WORKERS_NO):
        complex_subscription_workers.append(ComplexSubscriptionWorker(i))

    for worker in complex_subscription_workers:
        worker.start()

    for worker in complex_subscription_workers:
        worker.join()
    et = time.time()
    elapsed_time = et - st
    print(f'Execution time generating {SUBSCRIPTIONS_COUNT} complex subscriptions: {elapsed_time} seconds.')


class ProcessManager(object):
    def __init__(self, jobs_count):
        self.map = Pool().map
        self.jobs_count = jobs_count
        self.result = None

    def start(self):

        st = time.time()
        self.result = self.map(self.generate_publications, range(self.jobs_count))
        et = time.time()
        elapsed_time = et - st
        print(f'Execution time generating {PUBLICATIONS_COUNT} publications: {elapsed_time} seconds.')

        st = time.time()
        self.result = self.map(self.generate_simple_subscriptions, range(self.jobs_count))
        et = time.time()
        elapsed_time = et - st
        print(f'Execution time generating {SUBSCRIPTIONS_COUNT} simple subscriptions: {elapsed_time} seconds.')

        st = time.time()
        self.result = self.map(self.generate_complex_subscriptions, range(self.jobs_count))
        et = time.time()
        elapsed_time = et - st
        print(f'Execution time generating {SUBSCRIPTIONS_COUNT} complex subscriptions: {elapsed_time} seconds.')

        return self.result

    @staticmethod
    def generate_complex_subscriptions(self):
        count = SUBSCRIPTIONS_COUNT // WORKERS_NO
        data = list()

        item_count = 0
        while item_count < count:
            s = generate_complex_subscription()
            data.append(str(s))
            # data.append(s)
            item_count = item_count + 1

        return data

    @staticmethod
    def generate_simple_subscriptions(self):
        count = SUBSCRIPTIONS_COUNT // WORKERS_NO
        data = list()

        item_count = 0
        while item_count < count:
            s = generate_simple_subscription()
            data.append(str(s))
            # data.append(s)
            item_count = item_count + 1

        return data

    @staticmethod
    def generate_publications(self):
        count = PUBLICATIONS_COUNT // WORKERS_NO
        data = list()

        item_count = 0
        while item_count < count:
            s = generate_publication(item_count)
            data.append(str(s))
            # data.append(s)
            item_count = item_count + 1

        return data


if __name__ == '__main__':

    # do_iterative_tests()
    # do_multi_threaded_tests()
    manager = ProcessManager(WORKERS_NO)
    manager.start()
