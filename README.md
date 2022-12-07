

## gRPC

maven 插件：
默认编译的 proto 文件位于 `src/main/proto`。

```xml
<plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
<!--                    protoc path-->
                    <protocExecutable>/usr/local/bin/protoc</protocExecutable>
                    <protoSourceRoot>${project.basedir}/src/main/resources/proto</protoSourceRoot>
                    <outputDirectory>${project.basedir}/src/main/java</outputDirectory>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

### 生成类文件

1. mvn protobuf:compile 生成**消息**类文件
2. mvn protobuf:compile-custom 生成GRPC文件