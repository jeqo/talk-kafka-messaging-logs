#!/usr/bin/env bash
    bin/kafka-topics.sh --zookeeper localhost:2181 \
                        --create \
                        --partitions 5 \
                        --replication-factor 1 \
                        --topic parallel-consumers
