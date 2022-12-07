package win.hgfdodo;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionRequest;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionResponse;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionServiceGrpc;

public class Client {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 8080;

    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext() ///无需加密或认证
        .build();

    InceptionServiceGrpc.InceptionServiceBlockingStub inceptionServiceStub = InceptionServiceGrpc.newBlockingStub(channel);
    byte[] data = "hello!!!".getBytes();
    ByteString bytes = ByteString.copyFrom(data);
    InceptionRequest request = InceptionRequest.newBuilder()
        .setJpegEncoded(bytes)
        .build();
    InceptionResponse response = inceptionServiceStub.classify(request);
    System.out.println(response.getClassesList());
    System.out.println(response.getScoreList());

  }
}
