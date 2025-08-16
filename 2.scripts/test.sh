#!/usr/bin/env bash
set -x

CWD=`pwd`

cd $CWD/1.participacao
docker compose down -f || true

cd $CWD/../rinha-de-backend-2025/payment-processor
docker compose down -f || true

docker rm --force `docker ps -q`

cd $CWD/../rinha-de-backend-2025/payment-processor
docker compose up -d
sleep 2

cd $CWD/1.participacao
#docker compose up -d
rm -rvf docker-compose.logs
nohup docker compose up > docker-compose.logs &
sleep 2

success=1
max_attempts=15
attempt=1
while [ $success -ne 0 ] && [ $max_attempts -ge $attempt ]; do
    curl -f -s http://localhost:9999/payments-summary
    success=$?
    echo "tried $attempt out of $max_attempts..."
    sleep 5
    ((attempt++))
done

cd $CWD/../rinha-de-backend-2025/rinha-test
#k6 run rinha.js
k6 run -e MAX_REQUESTS=550 rinha.js
#k6 run -e MAX_REQUESTS=1000 rinha.js


#cd $CWD/1.participacao
#docker compose down

#cd $CWD/../rinha-de-backend-2025/payment-processor
#docker compose down

sleep 3
cat ../rinha-de-backend-2025/rinha-test/partial-results.json | jq

