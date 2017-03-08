#!/usr/bin/env bash
kubectl create configmap zookeeper-config --from-file config-maps/zookeeper.properties --from-file config-maps/log4j.properties --from-file config-maps/tools-log4j.properties
