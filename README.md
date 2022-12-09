
## init dev env

使用`spece-init.sh`初始化开发环境。

## gRPC
maven 插件：
默认编译的 proto 文件位于 `src/main/proto`。

```xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.6.2</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.10.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>
            <configuration>
                <!-- 非本地protoc-->
                <protocArtifact>com.google.protobuf:protoc:3.17.3:exe:${os.detected.classifier}</protocArtifact>
                <!-- 定义plugin generate java rpc， 使用mvn protobuf:compile-custom编译-->
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.42.0:exe:${os.detected.classifier}</pluginArtifact>

                <!--<protoSourceRoot></protoSourceRoot>-->
                <!-- <outputDirectory>${project.basedir}/src/main/java</outputDirectory>-->
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                        <goal>test-compile</goal>
                        <goal>test-compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 生成类文件

1. mvn protobuf:compile 生成**消息**类文件
2. mvn protobuf:compile-custom 生成GRPC文件

> 也可以直接使用maven的声明周期命令 `mvn generate-sources`声明所有 gRPC 文件。

### 构造Server
步骤：
1. 构造Message Service Handler
2. 声明Server服务，包括地址、端口等信息。

### 构造Client

步骤：
1. 构造Channel
2. 使用Channel 构造Stub
3. 用Stub 请求Service

## tfserving




## Kafka Stream tfserving

kafka stream 接入数据流，并在流中，充当 gRPC 客户端请求 服务，将服务结果写入到数据。

###  依据tfserving源代码的proto 文件编译serving 服务接口
因为python的tensorflow_serving_api中包含了所有的tfserving的proto编译后的类文件，不需要处理，但是其他语言不一定有对应的类文件，需要自己编译。

#### 原始的 tfserving 编译方法
tfserving 源代码地址：[https://github.com/tensorflow/serving](https://github.com/tensorflow/serving)

例如 example中，mnist需要使用 `predict.proto`,位于`https://github.com/tensorflow/serving/apis/predict.proto`.
注意里面可能包含了多个include。

> 缺点：
> 1. 麻烦：tfserving 的ptoro include了 tensorflow的proto，所以需要tensorflow的源代码。
> 2. 不可复用。

#### 专门编译 tensorflow serving api，作为依赖导入程序

链接：https://github.com/gfhe/tensorflow-serving-api


#### tfserving grpc client

参考：https://developer.aliyun.com/article/692123

1. 明确model spec 信息；
2. 明确模型的 signature信息。如果 savedModel时 包含多个signature，注意区分每个signature的对应输入输出和signature name。

> 本例中以prediction_signature为例子。
> 

##### 常量说明

常量：
* `tf.compat.v1.saved_model.signature_constants.CLASSIFY_INPUTS=input`
* `tf.compat.v1.saved_model.signature_constants.CLASSIFY_OUTPUT_CLASSES=classes`
* `tf.compat.v1.saved_model.signature_constants.CLASSIFY_OUTPUT_SCORES=scores`
* `tf.compat.v1.saved_model.signature_constants.CLASSIFY_METHOD_NAME=tensorflow/serving/classify`
* `tf.compat.v1.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY=serving_default`
* `tf.compat.v1.saved_model.signature_constants.PREDICT_METHOD_NAME=tensorflow/serving/predict`

##### ModelSpec 信息

ModelSpec 信息包含3个部分： 模型的名字、版本和 SignatureName。

其中：
1. 模型的名字和版本可以通过部署的路径来确认。例如tfserving的配置的环境变量 `` , 那么模型的名字和版本分别是：
2. 模型的SignatureName。通过阅读tfserving时，saveModule的代码确认，为`signature_def_map`的 key。
   如本例，位于：`tfserving/mnist/mnist_saved_model.py`中的代码：
   ```python
   signature_def_map={
          'predict_images':
              prediction_signature,
          tf.compat.v1.saved_model.signature_constants
          .DEFAULT_SERVING_SIGNATURE_DEF_KEY:
              classification_signature,
      }
   ```
   其中
   * `predict_images`为 prediction_signature；
   * `tf.compat.v1.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY=serving_default` 为classification_signature；

##### 输入输出参数信息

如本例，prediction_signature 位于：`tfserving/mnist/mnist_saved_model.py`中的代码：
```pythhon
  prediction_signature = (
      tf.compat.v1.saved_model.signature_def_utils.build_signature_def(
          inputs={'images': tensor_info_x},
          outputs={'scores': tensor_info_y},
          method_name=tf.compat.v1.saved_model.signature_constants
          .PREDICT_METHOD_NAME))
```
   
可确定：
* 输入的 参数 为： `images`，输入的tensor由`tensor_info_x`定义，包含了tensor的形状等信息。
  * `tensor_info_x = tf.compat.v1.saved_model.utils.build_tensor_info(x)`
* 输出的 参数 为： `scores`，输出的tensor由`tensor_info_y`定义，包含了tensor的形状等信息。
  * `tensor_info_y = tf.compat.v1.saved_model.utils.build_tensor_info(y)`

##### 输入输出参数tensor信息

参数定义信息：

```python
serialized_tf_example = tf.compat.v1.placeholder(tf.string, name='tf_example')
feature_configs = {
    'x': tf.io.FixedLenFeature(shape=[784], dtype=tf.float32),
}
tf_example = tf.io.parse_example(serialized_tf_example, feature_configs)
x = tf.identity(tf_example['x'], name='x')  # use tf.identity() to assign name
y_ = tf.compat.v1.placeholder('float', shape=[None, 10])
```

可知：
* x可批量传入，批次中的每个元素长度为784的float32类型数组。具体的，将`VarLenFeature` 映射为`SparseTensor`, 数组索引结构为`[batch, index]`。batch表示批大小索引，index表示特征数据值的索引
  * `tf.io.parse_example`表示：以批次中的一个元素的形状，定义批次输入数据的形状。详细说明：
      ```
      parse_example_v2(serialized, features, example_names=None, name=None)
      Parses `Example` protos into a `dict` of tensors.
      
      Parses a number of serialized [`Example`](https://www.tensorflow.org/code/tensorflow/core/example/example.proto)
      protos given in `serialized`. We refer to `serialized` as a batch with
      `batch_size` many entries of individual `Example` protos.
      
      `example_names` may contain descriptive names for the corresponding serialized
      protos. These may be useful for debugging purposes, but they have no effect on
      the output. If not `None`, `example_names` must be the same length as
      `serialized`.
      
      This op parses serialized examples into a dictionary mapping keys to `Tensor`
      `SparseTensor`, and `RaggedTensor` objects. `features` is a dict from keys to
      `VarLenFeature`, `SparseFeature`, `RaggedFeature`, and `FixedLenFeature`
      objects. Each `VarLenFeature` and `SparseFeature` is mapped to a
      `SparseTensor`; each `FixedLenFeature` is mapped to a `Tensor`; and each
      `RaggedFeature` is mapped to a `RaggedTensor`.
      
      Each `VarLenFeature` maps to a `SparseTensor` of the specified type
      representing a ragged matrix. Its indices are `[batch, index]` where `batch`
      identifies the example in `serialized`, and `index` is the value's index in
      the list of values associated with that feature and example.
      ```
  * 
