# Chat Service 

![CI/CD Pipeline](https://github.com/fugu137/chat-api/actions/workflows/pipeline.yml/badge.svg)
![status: in development](https://badgen.net/badge/status/in%20development/yellow)

This service consists of a Spring application (API) and a database. If you don't want to do any local development
and just want to run the application see [Running the Application in Docker](#running-the-application-in-docker) below.
If
you want to do local development, run tests, or have more control over how you run the application,
see [Local Development](#local-development).

## Running the Application in Docker

If you just want to run the chat service without running tests (requires Docker), you can run the supplied
`docker-compose.yml` file by executing the following command from same directory*:

```zsh
docker compose up --build
```

You will need to provide a _database name_, _username_, and _password_ by setting environment variables. The easiest way
to do this is to create an .env file at your project root with the following fields (sample values included):

```properties
POSTGRES_DB=chat_db
POSTGRES_USER=chat_admin
POSTGRES_PASSWORD=password
```

This will spin up a database and the API in separate Docker containers and set up the networking between them.
The chat service will be available at `http://localhost:8080`.

By default, it will be configured to allow CORS from `http://localhost:3000`. If you want to allow requests from
different origins add a `CORS_ALLOWED_ORIGINS` environment variable with a comma separated list of URLs. (This can be
added to your .env file.)

*_Note: if all you want to do is run the application in Docker there is no need to clone this repository. You can just
download or copy the `docker-compose.yml` file and follow the instructions above. Docker compose will pull an image from
[DockerHub](https://hub.docker.com/repository/docker/fugu137/chat-service) and use that to run the chat service, rather than building the image from the codebase._

## API Documentation

When the service is running full Swagger API documentation is published at `/swagger-ui.html`. The OpenAPI specification
used to generate the documentation is available at `/v3/api-docs`.

## Local Development

### 1. Setting up the databases

The chat service requires a Postgres database to run, as do the integration tests.
To configure the two databases, create a .env file at the project root, with the following fields:

```properties
# Credentials for the main database used for local development. Values are for illustration purposes.
POSTGRES_DB=chat_db
POSTGRES_USER=chat_admin
POSTGRES_PASSWORD=password

# Credentials for the test database used for integration testing. Sample values included.
TEST_POSTGRES_DB=test_db
TEST_POSTGRES_USER=test_admin
TEST_POSTGRES_PASSWORD=password
```

With the configuration in place, create two Postgres databases with matching credentials. You have two options for doing
this:

<details>
   <summary><u>Run and set up the database yourself</u></summary>

1. Download and install [PostgreSQL](https://www.postgresql.org/download/) by following the instructions for your
   operating system. Version 15.2 is recommended, but other versions should work as well.
2. Next you will need to login with the superuser account `postgres`. To do this follow the instructions for your
   operating system. In Linux the command is:

   ```zsh
   sudo -u postgres psql   
   ```

3. Create a password for the superuser:

   ```zsh
   \password postgres
   ```

4. Configure Postgres to allow connection with username and password. To do this,
    - Find your `pg_hba.conf` configuration file. The location of this file varies depending on operating system so you
      will have to look in the documentation. (In some Linux distributions it is located
      at `/var/lib/pgsql/data/pg_hba.conf`.)
    - Edit the file that all local and host databases have their METHOD set to `md5` rather than `ident` or `peer`.
    - Save the file and restart PostgreSQL. (In Linux the command is `sudo systemctl restart postgresql`.)
5. Run the Gradle task `createDatabases` to create the databases from the values you set in the .env file:

   ```zsh
   ./gradlew createDatabases
   ```

   If you prefer, you can also manually create the databases (you can use the commands in `database/initdb.sh`). Just
   make sure the credentials you use match those set in the .env file, since those will be used to configure the
   application to connect to the databases.

</details>

<details>
   <summary><u>Run the databases in Docker</u></summary>

1. Install [Docker](https://docs.docker.com/get-docker/) if you don't already have it installed.
2. Run the Gradle task `startDatabases`:

   ```zsh
   ./gradlew startDatabases --password <POSTGRES_PASSWORD>
   ```

   The postgres password can be anything you like. It will set the password for the superuser `postgres` within the
   container.

</details>

### 2. Running the application

To run the application execute the following command from the project root:

```zsh
./gradlew run
```

Alternatively, you can use your IDE's in-built tooling. For example, in Intellij you can right-click on Application.java
and then click `Run Application.main()`.

If you don't want to set up the databases yourself you can run the entire application, with database, in Docker
containers (see [Running the Application in Docker](#running-the-application-in-docker)).

### 3. Running the Tests

The application comes with a full suite of unit tests and integration tests. The unit tests can be run with the command:

```zsh
./gradlew test
```

The integration tests can be run with the command:

```zsh
./gradlew integrationTest
```

*Note: The integration tests need a Postgres database to run.
See [1. Setting up the databases](#1-setting-up-the-databases) for instructions on creating and configuring the test
database.*

Finally, if you want to run all the tests, run:

```zsh
./gradlew check
```