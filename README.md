# Readme

## Local Development

### 1. Running the application

To run the application execute the following command from the project root:

   ```zsh
   ./gradlew run
   ```

Alternatively, you can use your IDE's inbuilt tooling. For example, in Intellij you can right-click on Application.java
and then click `Run Application.main()`.

For the application to work with these commands you will also need set up local databases (see below).

<details>
   <summary>Setting up the local databases</summary>

You will need to create two local PostgreSQL databases: one for local development, and one for running the integration
tests.

<u>Instructions:</u>

1. If you don't already have PostgreSQL installed, download and install it
   from [here](https://www.postgresql.org/download/).
2. Run postgres using your system account. For example, in Linux run the following command:
   ```zsh
   sudo -u postgres psql
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

</details>

If you don't want to do this you can run the entire application, with databases, in Docker containers using:
   ```zsh
   ./gradle dockerRun
   ```

### 2. Running the tests

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
