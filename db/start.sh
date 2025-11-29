#!/bin/bash
set -e

docker-entrypoint.sh mysqld &
MYSQL_PID=$!

until mysqladmin ping -h 127.0.0.1 -u root  --silent; do
    sleep 2
done

/seed_injector.sh &

wait $MYSQL_PID