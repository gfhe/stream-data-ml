package win.hgfdodo;


import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionRequest;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionResponse;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionServiceGrpc;

public class GrpcDemoServiceHandler extends InceptionServiceGrpc.InceptionServiceImplBase {

  @Override
  public void classify(InceptionRequest request, StreamObserver<InceptionResponse> responseObserver) {
    System.out.println("request classify");
    ByteString bytes = request.getJpegEncoded();
    System.out.println("request size: " + bytes.size());
    try {
      InceptionResponse response = InceptionResponse.newBuilder()
          .addClasses("a")
          .addScore(0.3F)
          .addClasses("b")
          .addScore(0.7F)
          .build();
      responseObserver.onNext(response);
    } catch (Exception e) {
      responseObserver.onError(e);
    }
    responseObserver.onCompleted();
  }
}
