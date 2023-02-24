# Builds the Docker image for the PostgreSQL databases. Used in the 'initialiseDBs' Gradle task.

FROM postgres:15.2-alpine3.17

COPY ./database/initdb.sh ./docker-entrypoint-initdb.d/

COPY .env ./docker-entrypoint-initdb.d/