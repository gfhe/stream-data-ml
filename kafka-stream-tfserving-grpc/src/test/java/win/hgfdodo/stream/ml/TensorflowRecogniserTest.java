package win.hgfdodo.stream.ml;

import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class TensorflowRecogniserTest {

  @Test
  void classify() throws IOException {
    TensorflowRecogniser recogniser = new TensorflowRecogniser("localhost:9000", "mnist", "predict_images");
    recogniser.classify();
  }
}