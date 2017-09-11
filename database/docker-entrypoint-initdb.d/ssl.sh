#!/usr/bin/env bash

if [ -r /etc/postgresql/server.key ] && [ -r /etc/postgresql/server.crt ]
then
  # turn on ssl
  sed -ri "s/^#?(ssl\s*=\s*)\S+/\1'$PGSSL'/" "$PGDATA/postgresql.conf"
  sed -ri "s|^#?(ssl_key_file\s*=\s*)\S+|\1'/etc/postgresql/server.key'|" "$PGDATA/postgresql.conf"
  sed -ri "s|^#?(ssl_cert_file\s*=\s*)\S+|\1'/etc/postgresql/server.crt'|" "$PGDATA/postgresql.conf"
fi