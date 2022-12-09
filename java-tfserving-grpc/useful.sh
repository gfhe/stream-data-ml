 echo -e "example.jpg" | kafkacat -b localhost:9092 -P -t ImageInputTopic

 kafka-console-consumer --bootstrap-server localhost:9092 --topic ImageOutputTopic --from-beginning