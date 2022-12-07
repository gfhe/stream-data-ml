package win.hgfdodo.stream.ml;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class App {
  public static void main(String[] args) {
    final String host = "localhost";
    final int port = 8080;
    final String inputTopic = "ImageInputTopic";
    final String outputTopic = "ImageOutputTopic";

    final Properties prop = new Properties();
    prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-tfserving");
    prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:9092");
    prop.put(StreamsConfig.EXACTLY_ONCE, true);
    prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    final StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> stream = builder.stream(inputTopic);

    KStream<String, Object> transformedMessage = stream.mapValues(v -> {
      System.out.println("image path: " + v);
      TensorflowObjectRecogniser tensorflowObjectRecogniser = null;
      FileInputStream imgInputSteam = null;
      try {
        imgInputSteam = new FileInputStream(v);
        tensorflowObjectRecogniser = new TensorflowObjectRecogniser(host, port);
        List<Map.Entry<String, Double>> result = tensorflowObjectRecogniser.recongnise(imgInputSteam);
        String prediction = result.toString();
        return prediction;

      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      } finally {
        if (tensorflowObjectRecogniser != null) {
          try {
            tensorflowObjectRecogniser.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        if (imgInputSteam != null) {
          try {
            imgInputSteam.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    });
    transformedMessage.to(outputTopic);

    Topology topology = builder.build(prop);
    KafkaStreams kafkaStreams = new KafkaStreams(topology, prop);

    kafkaStreams.cleanUp();
    kafkaStreams.start();

    // 增加shutdone hook， 优雅的停止kstream。
    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }
}
