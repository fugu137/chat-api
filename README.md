# Chat Service

If you just want to run the chat service without running tests and you have Docker installed, you can use
the `dockerRun` Gradle task:

```zsh
./gradlew dockerRun
```

This spins up a database and the chat service in separate Docker containers and sets up the networking between them. The
chat service will be available at `localhost:8080`.

<details>
<summary><u>Run the chat service without cloning the git repository</u></summary>

If you don't want to clone this git repository, you can run the chat service using `docker compose` and Docker images
stored in DockerHub. (Coming soon.)

[comment]: <> (TODO: add docker-compose file and/or link once it's ready)

</details>

## Local Development

### 1. Setting up the databases

The chat service requires a PostgreSQL database to run, as do the integration tests.
To configure the two databases, create a .env file at the project root, with the following fields:

   ```properties
   # Sets the active Spring profile. Should be set to 'local' for local development.
   SPRING_PROFILES_ACTIVE=local

   # Credentials for the main database used for local development. Values are for illustration purposes.
   POSTGRES_DB=chat_db
   POSTGRES_USER=chat_admin
   POSTGRES_PASSWORD=password

   # Credentials for the test database used for integration testing. Sample values below. Values are for illustration purposes.
   TEST_POSTGRES_DB=test_db
   TEST_POSTGRES_USER=test_admin
   TEST_POSTGRES_PASSWORD=password
   ```

*Note: without this .env file the application won't run without manual configuration.*

With the configuration in place, create two PostgreSQL databases with matching credentials. You have a two options:

<details>
   <summary><u>Run and set up the database yourself:</u></summary>

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
5. Run the Gradle task `initialiseDBs` to create the databases from the values you set in the .env file:

   ```zsh
   ./gradlew initialiseDBs
   ```

   If you prefer, you can also manually create the databases (you can use the commands in `database/initdb.sh`). Just
   make sure the credentials you use match those set in the .env file, since those will be used to configure the
   application to connect to the databases.

</details>

<details>
   <summary><u>Run the databases in Docker:</u></summary>

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
containers using:

```zsh
./gradle dockerRun
```

*Note: you will have to have Docker installed to run this command*

### 3. Running the tests

The application comes with a full suite of unit tests and integration tests. The unit tests can be run with the command:

```zsh
./gradlew test
```

The integration tests can be run with the command:

```zsh
./gradlew integrationTest
```

If you want to run all of the tests, run:

```zsh
./gradlew check
```
