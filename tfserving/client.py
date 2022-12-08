from proto import inception_inference_pb2, inception_inference_pb2_grpc
import grpc

def run():
    channel = grpc.insecure_channel('localhost:9000')
    stub = inception_inference_pb2_grpc.InceptionServiceStub(channel)
    request = inception_inference_pb2.InceptionRequest(jpeg_encoded = str.encode("hello"))
    response = stub.Classify(request)
    print(response)

if __name__== "__main__":
    run()