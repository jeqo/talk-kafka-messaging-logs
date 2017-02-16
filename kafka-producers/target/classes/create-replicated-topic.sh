#!/usr/bin/env bash
bin/kafka-topics.sh --zookeeper localhost:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 15 \
                    --topic ack-topic
