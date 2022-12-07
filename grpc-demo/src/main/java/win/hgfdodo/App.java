package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import win.hgfdodo.GrpcDemoServiceHandler;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) throws IOException, InterruptedException {
    Server server = ServerBuilder.forPort(8080)
        .addService(new GrpcDemoServiceHandler())
        .maxInboundMessageSize(8*1024*1024) // 1MB
        .build();
    server.start();
    server.awaitTermination();
  }
}
