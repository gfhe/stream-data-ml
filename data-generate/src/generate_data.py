from confluent_kafka import Producer

import mnist_input_data
import os


def getenv_or_default(key, default_val):
    val = os.getenv(key)
    return val if val is not None else default_val


work_dir = getenv_or_default('MNIST_DIR', "/tmp")
bootstrap_servers = getenv_or_default('KAFKA_BROKER', "localhost:9092")

producer = Producer({"bootstrap.servers": bootstrap_servers})
test_data_set = mnist_input_data.read_data_sets(work_dir).test


def on_delivery(err, decode_message, original_message):
    if err is not None:
        print(f"❌: {err}")


def produce(topic, num_test=1, batch_size=1):
    """
    生成数据到kafka， key为label的_ 拼接。
    :param topic:
    :param num_test:
    :param batch_size:
    :return:
    """
    for _ in range(num_test):
        images, labels = test_data_set.next_batch(batch_size, fake_data=False)
        key = labels.tobytes()
        val = images.tobytes()
        producer.produce(topic, value=val, key=key, on_delivery=on_delivery)

    producer.flush()


if __name__ == "__main__":
    topic = os.getenv("TOPIC")
    if topic is None:
        print("destination topic is none!")
    else:
        produce(topic)