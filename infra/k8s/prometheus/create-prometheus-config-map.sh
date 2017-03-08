#!/usr/bin/env bash
kubectl create configmap prometheus-config --from-file config-maps/prometheus.yml
