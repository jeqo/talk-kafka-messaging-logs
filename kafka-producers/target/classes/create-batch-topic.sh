#!/usr/bin/env bash
bin/kafka-topics.sh --zookeeper zookeeper:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 1 \
                    --topic batch-size

bin/kafka-topics.sh --zookeeper zookeeper:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 1 \
                    --topic batch-linger