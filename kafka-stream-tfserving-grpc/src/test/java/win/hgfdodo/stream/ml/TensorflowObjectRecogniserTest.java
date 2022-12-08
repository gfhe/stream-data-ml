package win.hgfdodo.stream.ml;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

class TensorflowObjectRecogniserTest {
  private final static String host = "localhost";
  private final static int port = 9000;

  private final static String IMAGE_PATH_NAME_PATTERN = "images/n04596742_%d.JPEG";

  @Test
  void recongnise() {
    TensorflowObjectRecogniser recogniser = new TensorflowObjectRecogniser(host, port);
    for (int i = 0; i < 3; i++) {
      InputStream inputStream = getClass().getResourceAsStream(String.format(IMAGE_PATH_NAME_PATTERN, i));
      List<Map.Entry<String, Double>> result = recogniser.recongnise(inputStream);
      System.out.println(result);
    }

    try {
      recogniser.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}