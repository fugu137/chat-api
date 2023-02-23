# Chat Service

## 1. Running the application

You will need to create a .env file with the following fields at the project root to configure the database:
   
   ```properties
   SPRING_PROFILES_ACTIVE = <Spring profile to run ('local' for local development)>
   # If running your own database these fields should match your database credentials
   POSTGRES_DB = <PostgreSQL database name>
   POSTGRES_USER = <PostgreSQL username>
   POSTGRES_PASSWORD = <PostgreSQL password>
   ```

To run the application execute the following command from the project root:
 
   ```zsh
   ./gradlew run
   ```

Alternatively, you can use your IDE's in-built tooling. For example, in Intellij you can right-click on Application.java
and then click `Run Application.main()`.

For the application to work with these commands you will need set up a local database (see below).

<details>
   <summary><u>Setting up the local databases</u></summary>

You will need to create two local PostgreSQL databases: one for local development, and one for running the integration
tests.

Instructions:

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

If you don't want to set up the databases yourself you can run the entire application, with databases, in Docker containers using:
  
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
