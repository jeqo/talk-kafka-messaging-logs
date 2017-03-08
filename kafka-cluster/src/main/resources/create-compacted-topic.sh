#!/usr/bin/env bash
# min.compaction.lag.ms=0 The minimum time a message will remain uncompacted in the log. Only applicable for logs that are being compacted.
# min.cleanable.dirty.ratio=0.5 This configuration controls how frequently the log compactor will attempt to clean the log
bin/kafka-topics.sh --zookeeper zookeeper:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 1 \
                    --topic compacted \
                    --config segment.bytes=10000 \
                    --config cleanup.policy=compact \
                    --config min.compaction.lag.ms=0 \
                    --config min.cleanable.dirty.ratio=0.01
# This topic will create segments of 10KB each one and after that threshold is passed "old" segments will be processed.
# min.compaction.lag.ms=0 means all messages in a segment to be compacted will be compacted, without lags
# min.cleanable.dirty.ratio=0.01 means if 1% is duplicated it will run compaction process.
# A higher ratio will mean fewer, more efficient cleanings but will mean more wasted space in the log.