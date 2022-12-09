FROM maven:3.8-jdk-8-slim AS builder
COPY src /opt/src
COPY pom.xml /opt/pom.xml
RUN cd /opt ; mvn generate-sources ; mvn install

FROM openjdk:8-jre
RUN apt update && apt install -y kafkacat
COPY useful.sh /app/
COPY images /images
COPY --from=builder /opt/target/kafka-stream-tfserving-grpc-*.jar /app/app.jar
