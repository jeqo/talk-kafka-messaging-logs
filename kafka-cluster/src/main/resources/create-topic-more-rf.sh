#!/usr/bin/env bash
bin/kafka-topics.sh --zookeeper zookeeper:2181 \
                    --create \
                    --partitions 1 \
                    --replication-factor 10 \
                    --topic more-rf


bin/kafka-topics.sh --describe \
                    --topic more-rf\
                    --zookeeper zookeeper:2181