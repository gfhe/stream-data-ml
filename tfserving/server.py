from proto import inception_inference_pb2,inception_inference_pb2_grpc
import time
from concurrent import futures
import grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24


class TensorflowRecongnizer(inception_inference_pb2_grpc.InceptionServiceServicer):
    def Classify(self, request, context):
        print(f"get request: {len(request.jpeg_encoded)}")
        return inception_inference_pb2.InceptionResponse(classes=['a','b'], score=[0.3, 0.7])


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=2))
    inception_inference_pb2_grpc.add_InceptionServiceServicer_to_server(TensorflowRecongnizer(), server)
    server.add_insecure_port('0.0.0.0:9000')
    server.start()  # 不会阻塞
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()
