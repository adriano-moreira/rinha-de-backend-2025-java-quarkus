#!/bin/bash -ex

## JVM version
mvn clean package -Dmaven.test.skip=true
docker buildx build . -f src/main/docker/Dockerfile.jvm -t adrianomoreira86/rinha-de-backend-2025-java-quarkus:latest

### Native version
#mvn clean package -Pnative
#docker buildx build . -f src/main/docker/Dockerfile.native-micro -t adrianomoreira86/rinha-de-backend-2025-java-quarkus:latest
