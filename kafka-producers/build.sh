#!/usr/bin/env bash
eval $(minikube docker-env)
docker build -t jeqo/kafka-producer:1.0.0 .