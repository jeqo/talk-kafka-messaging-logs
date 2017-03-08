#!/usr/bin/env bash
# retention.ms 604800000 (7days) by default
bin/kafka-topics.sh --zookeeper zookeeper:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 1 \
                    --topic retention \
                    --config segment.bytes=10000 \
                    --config retention.ms=60000
# Retention is applied in "old" segments. Segment size segment.bytes=10000 10KB
# retention.ms=60000 1 minute.
