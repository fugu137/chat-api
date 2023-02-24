#!/bin/bash

SUPERUSER="postgres"
DIR=''

if [[ $HOME == '/var/lib/postgresql' ]]; then
  echo "Running database init script inside of PostgreSQL Docker container. Sourcing env file..."
  DIR=/docker-entrypoint-initdb.d/
fi

set -o allexport
source $DIR.env
set +o allexport

psql -v ON_ERROR_STOP=1 --username "$SUPERUSER" <<-EOSQL
  CREATE USER $POSTGRES_USER WITH PASSWORD '$POSTGRES_PASSWORD';
  CREATE DATABASE $POSTGRES_DB OWNER $POSTGRES_USER;

  CREATE USER $TEST_POSTGRES_USER WITH PASSWORD '$TEST_POSTGRES_PASSWORD';
  CREATE DATABASE $TEST_POSTGRES_DB OWNER $TEST_POSTGRES_USER;
EOSQL