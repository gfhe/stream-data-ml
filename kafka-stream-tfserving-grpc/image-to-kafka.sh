#!/bin/env bash

for img in `ls images`;
do
 echo -e $img | kafkacat -b broker:9092 -P -t ImageInputTopic
done