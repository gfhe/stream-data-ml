FROM zookeeper as builder
COPY src /opt/kafka-stream-tfserving/src
WORKDIR /opt/kafka-stream-tfserving
RUN mvn generate-srouces && mvn package

FROM builder
COPY /opt/kafka-stream-tfserving/target/*.jar /app/
