#!/usr/bin/env bash
set -ex

CWD=`pwd`

#export K6_WEB_DASHBOARD=true
#export K6_WEB_DASHBOARD_PORT=5665
#export K6_WEB_DASHBOARD_PERIOD=2s
#export K6_WEB_DASHBOARD_OPEN=true
#export K6_WEB_DASHBOARD_EXPORT='report.html'

## BUILD
#TODO: change to native
#mvn clean package
#docker buildx build . -f src/main/docker/Dockerfile.jvm -t adrianomoreira86/rinha-de-backend-2025-java-quarkus:latest

### Start PROCESSOR
#cd "$CWD/../rinha-de-backend-2025/payment-processor"
#docker compose up -d
#sleep 2

#cd "$CWD/entrega"
#docker compose up -d
#sleep 2


### RUN TEST DEFAULT
cd "$CWD/../rinha-de-backend-2025/rinha-test"
k6 run rinha.js
#
###
#cd "$CWD/entrega"
#docker-compose down
#
### Stop PROCESSOR
#cd "$CWD/../rinha-de-backend-2025/payment-processor"
#docker-compose down
