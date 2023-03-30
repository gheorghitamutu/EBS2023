import threading
import time

import paho.mqtt.client as mqtt

from generators.publication import generate_publication

publications_generated = 0
publications_read = 0
lock = threading.Lock()

WORKERS_NO = 6
avg_latency = [0.0 for _ in range(0, WORKERS_NO)]
msg_read = [0 for _ in range(0, WORKERS_NO)]

start_time = round(time.time() * 1000)
end_time = round(time.time() * 1000)

stop_subscribers = False


class Publisher:
	def __init__(self, worker_id):
		self.worker_id = worker_id
		self.broker_address = "localhost"
		self.client = mqtt.Client()

		print(f"Creating Publisher Instance #{self.worker_id}")
		self.client.on_message = None
		self.client.connect(self.broker_address, port=8883)
		self.client.loop_start()

		self.topic = f"publication/client_{self.worker_id}"

		self.publications_no = 50000

	@staticmethod
	def generate_publication(index):
		return generate_publication(index)

	def publish(self, publication):
		current_time = round(time.time() * 1000)
		self.client.publish(
			topic=self.topic,
			payload=f"Time: {current_time} => Client #{self.worker_id} => {publication}")

	def stop(self):
		self.client.loop_stop()


class Subscriber:
	def __init__(self, worker_id):
		self.worker_id = worker_id
		self.broker_address = "localhost"
		self.client = mqtt.Client()

		def on_message(mosq, obj, message):
			current_time = round(time.time() * 1000)

			with lock:
				global publications_read
				# print(f'Read publication #{publications_read}')
				publications_read = publications_read + 1

			content = message.payload.decode("utf-8")
			# print(f'Time: {current_time} Topic: {message.topic} Payload: {content}')

			timestamp, client, data = [x.strip() for x in content.split('=>')]
			timestamp = timestamp.split(':')[-1].strip()
			client_no = int(client.split('#')[-1].strip())

			avg_latency[client_no] = current_time - int(timestamp)
			msg_read[client_no] = msg_read[client_no] + 1

		print(f"Creating Subscriber Instance #{self.worker_id}")
		self.client.on_message = on_message
		self.client.connect(self.broker_address, port=8883)
		self.topic = f"publication/client_{self.worker_id}"
		self.client.subscribe(self.topic, 0)


class PublisherWorker(threading.Thread):
	def __init__(self, publisher_id):
		threading.Thread.__init__(self)
		self.publisher = Publisher(publisher_id)

	def run(self):
		global publications_generated
		while publications_generated < self.publisher.publications_no:
			with lock:
				# print(f"-------------------- THREAD ID: {self.publisher.worker_id} ---------------------------------")
				publication = self.publisher.generate_publication(publications_generated)
				# print(f"Publication {publications_generated} generated....")
				# print(f"Publication: {publication}")
				self.publisher.publish(publication)
				# print(f"Publication {publications_generated} published....")
				# print("---------------------------------------------------------------------------------------------")

				publications_generated = publications_generated + 1

		self.publisher.stop()


class SubscriberWorker(threading.Thread):
	def __init__(self, subscriber_id):
		threading.Thread.__init__(self)
		self.subscriber = Subscriber(subscriber_id)

	def run(self):
		global publications_generated
		global publications_read

		while self.subscriber.client.loop() == 0:
			with lock:
				if stop_subscribers or publications_read >= publications_generated:
					self.subscriber.client.loop_stop()
					return


if __name__ == '__main__':

	workers = []
	for i in range(0, int(WORKERS_NO / 2)):
		worker = PublisherWorker(i)
		worker.start()
		workers.append(worker)

	for i in range(int(WORKERS_NO / 2), WORKERS_NO):
		worker = SubscriberWorker(i - int(WORKERS_NO / 2))
		worker.start()
		workers.append(worker)

	for worker in workers[0:int(WORKERS_NO/2)]:
		worker.join()

	time.sleep(10)
	stop_subscribers = True

	for worker in workers[int(WORKERS_NO/2):WORKERS_NO]:
		worker.join()

	for d in range(0, int(WORKERS_NO/2)):
		print(f"Medium latency worker #{d}: {avg_latency[d] / msg_read[d]}")
		print(avg_latency, msg_read)
