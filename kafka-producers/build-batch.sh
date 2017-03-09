#!/usr/bin/env bash
eval $(minikube docker-env)
docker build -t jeqo/kafka-producer-batch:1.0.0 -f Dockerfile-batch .