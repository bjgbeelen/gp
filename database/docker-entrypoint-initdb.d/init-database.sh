#!/bin/sh
psql -U postgres <<-SQL
  CREATE DATABASE "gp-shifts";
  CREATE ROLE app;
  GRANT ALL ON DATABASE "gp-shifts" TO app;
  REVOKE CONNECT ON DATABASE "gp-shifts" FROM PUBLIC;
  GRANT CONNECT ON DATABASE "gp-shifts" TO app;
  ALTER DATABASE "gp-shifts" OWNER TO app;
  ALTER ROLE app WITH LOGIN;
SQL
