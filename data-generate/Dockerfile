FROM python:3.9
ENV MNIST_DIR=/tmp
ENV KAFKA_BROKER=localhost:9092
ENV TOPIC=mnist
COPY requirements.txt /app/requirements.txt
COPY src /app/src
RUN pip install -r /app/requirements.txt --no-cache-dir
