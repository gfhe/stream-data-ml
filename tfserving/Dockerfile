FROM tensorflow/tensorflow AS builder
COPY mnist /mnist
RUN python /mnist/mnist_saved_model.py /models/mnist

FROM tensorflow/serving
COPY --from=builder /models /models