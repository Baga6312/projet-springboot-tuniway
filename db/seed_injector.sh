#!/bin/bash

set -e

DB_HOST="127.0.0.1"
DB_NAME="tuniway"
DB_USER="root"
DB_PASS=""

echo " Waiting for backend to create database and tables..."

while true; do
  TABLE_COUNT=$(mysql -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -sse "SHOW TABLES;" 2>/dev/null | wc -l || echo "0")
  
  echo "Current tables: $TABLE_COUNT/5"
  
  if [ "$TABLE_COUNT" -eq 5 ]; then
    echo " All 5 tables created!"
    break
  fi
  
  sleep 2
done

echo "Injecting seed data..."
mysql -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < /seed_data.sql

echo "Seed data injection complete!"