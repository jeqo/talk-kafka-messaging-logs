#!/usr/bin/env bash
bin/kafka-topics.sh --zookeeper zookeeper:2181 \
                    --create \
                    --partitions 3 \
                    --replication-factor 1 \
                    --topic more-partitions

bin/kafka-topics.sh --describe \
                    --topic more-partitions \
                    --zookeeper zookeeper:2181

bin/kafka-console-producer.sh --broker-list localhost:9092 \
                    --topic more-partitions