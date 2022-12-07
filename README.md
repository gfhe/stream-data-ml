

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


## Kafka Stream tfserving

kafka stream 接入数据流，并在流中，充当 gRPC 客户端请求 服务，将服务结果写入到数据。



