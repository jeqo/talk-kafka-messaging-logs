#!/usr/bin/env bash
kubectl create configmap kafka-config --from-file config-maps/server.properties --from-file config-maps/log4j.properties  --from-file config-maps/tools-log4j.properties
