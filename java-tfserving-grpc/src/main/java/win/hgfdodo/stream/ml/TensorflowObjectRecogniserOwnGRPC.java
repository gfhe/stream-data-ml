package win.hgfdodo.stream.ml;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionRequest;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionResponse;
import win.hgfdodo.kafka.stream.tfserving.proto.InceptionServiceGrpc;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

//与TensorFlow Serving 服务器的 gRCP 通信， 实现图像识别。
public class TensorflowObjectRecogniserOwnGRPC implements Closeable {
  private Logger log = LoggerFactory.getLogger(TensorflowObjectRecogniserOwnGRPC.class);
  private ManagedChannel channel;
  private InceptionServiceGrpc.InceptionServiceBlockingStub serviceStub;

  public TensorflowObjectRecogniserOwnGRPC(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    this.serviceStub = InceptionServiceGrpc.newBlockingStub(this.channel);
  }

  public List<Map.Entry<String, Double>> recongnise(InputStream inputStream) {
    List<Map.Entry<String, Double>> objects = new ArrayList<>();

    try {
      ByteString byteString = ByteString.readFrom(inputStream);
      InceptionRequest request = InceptionRequest.newBuilder()
          .setJpegEncoded(byteString)
          .build();
      long start = System.currentTimeMillis();
      InceptionResponse response = this.serviceStub.classify(request);
      Iterator<String> classes = response.getClassesList().iterator();
      Iterator<Float> scores = response.getScoreList().iterator();
      while (classes.hasNext() && scores.hasNext()) {
        String className = classes.next();
        Float score = scores.next();
        Map.Entry<String, Double> object = new AbstractMap.SimpleEntry<>(className, score.doubleValue());
        objects.add(object);
      }
      log.info("end request, waste: {}ms", (System.currentTimeMillis() - start));
      return objects;

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void close() throws IOException {
    if (channel != null) {
      channel.shutdownNow();
    }
  }
}
