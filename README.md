# Readme

## Local Development

### 1. Setting up the local databases

You will need to create two local PostgreSQL databases: one for local development, and one for running the integration
tests.

#### Instructions:

1. If you don't already have PostgreSQL installed, download and install it
   from [here](https://www.postgresql.org/download/).
2. Run postgres using your system account. For example, in Linux run the following command:
   ```zsh
   $ sudo -u postgres psql
   ```
3. Create a local user and database:
   ```
   postgres=# CREATE USER chat_admin WITH PASSWORD 'password';
   ```
   ```
   postgres=# CREATE DATABASE chat_db OWNER chat_admin;
   ```
4. Create a test user and database:
   ```
   postgres=# CREATE USER test_admin WITH PASSWORD 'password';
   ```
   ```
   postgres=# CREATE DATABASE test_db OWNER test_admin;
   ```

### 2. Running the application

You can run the application in one of three ways:

<details>
   <summary>IDE</summary>

If your IDE supports it right click on Application.java and run `Application.main()`. This should work
in IntelliJ and other popular IDEs.

</details>

<details>
   <summary>Command-Line</summary>

First build the application using Gradle Wrapper:

   ```zsh
   $ ./gradlew clean build
   ```

This will create a .jar file at `build/libs/chat.api-0.0.1-SNAPSHOT.jar`. This file can then be executed:

   ```zsh
   $ java -jar build/libs/chat.api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
   ```

*Important: don't forget to add `--spring.profiles.active=local`. Without
this argument the application won't run.*

</details>

<details>
   <summary>Docker</summary>

// TODO: setup docker and update

</details>

*Note: the first two methods require that you setup a local user and PostgreSQL database (
see [1. Setting up the local databases](#1-setting-up-the-local-databases)). Alternatively you can run a database with
the same details in a [Docker container](https://hub.docker.com/_/postgres).*

### 3. Running the tests

The application comes with a full suite of unit tests and integration tests. The unit tests can be run with the command:

   ```zsh
   $ ./gradlew test
   ```

The integration tests can be run with the command:

   ```zsh
   $ ./gradlew integrationTest
   ```

If you want to run all of the tests, run:

   ```zsh
   $ ./gradlew check
   ```
