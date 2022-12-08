from proto import inception_inference_pb2, inception_inference_pb2_grpc
import grpc


def img_bytes(image_name):
    """
    get image byte array

    :param image_name: image file path name
    :return:
    """
    with open(image_name, 'rb') as image:
        return image.read()


def run():
    channel = grpc.insecure_channel("localhost:9000")
    stub = inception_inference_pb2_grpc.InceptionServiceStub(channel)
    bytes = img_bytes("../kafka-stream-tfserving-grpc/images/n04596742_10.JPEG")
    request = inception_inference_pb2.InceptionRequest(jpeg_encoded=bytes)
    response = stub.Classify(request)
    print(response)


if __name__ == "__main__":
    run()
