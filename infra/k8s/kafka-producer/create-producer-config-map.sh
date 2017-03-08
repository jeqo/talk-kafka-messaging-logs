#!/usr/bin/env bash
kubectl create configmap kafka-producer-config --from-file config-maps/producer.properties
