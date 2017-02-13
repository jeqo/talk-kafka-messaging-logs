#!/usr/bin/env bash
# retention.ms 604800000 (7days)
bin/kafka-topics.sh --zookeeper localhost:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 1 \
                    --topic retention \
                    --config segment.bytes=10000 \
                    --config retention.ms=100