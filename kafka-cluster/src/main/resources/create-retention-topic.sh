#!/usr/bin/env bash
# retention.ms 604800000 (7days) by default
# segment.bytes=1073741824 (1GB) approx
bin/kafka-topics.sh --zookeeper localhost:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 1 \
                    --topic retention \
                    --config cleanup.policy=delete \
                    --config segment.bytes=10000 \
                    --config retention.ms=30000
# Retention is applied in "old" segments. Segment size segment.bytes=10000 10KB
# retention.ms=30000 30 secs.
