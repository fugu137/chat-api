# Readme

## Local Development

### Setting up the local databases

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