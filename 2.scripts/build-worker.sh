#!/bin/bash -ex

cd worker

IMAGE_NAME='adrianomoreira86/rinha-de-backend-2025-java-quarkus-worker:latest'

mvn clean package -Dmaven.test.skip=true -Pnative
docker buildx build . -f src/main/docker/Dockerfile.native-micro -t $IMAGE_NAME
