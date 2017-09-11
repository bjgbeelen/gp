#!/usr/bin/env bash
if [ -w conf/server.key ];then
  rm -f conf/server.key
fi
if [ -w conf/server.crt ];then
  rm -f conf/server.crt
fi
openssl req -x509 -newkey rsa:4096 -nodes -keyout conf/server.key -out conf/server.crt -days 365
openssl pkcs12 -export -out ../certs/test-database.p12 -inkey conf/server.key -in conf/server.crt -certfile conf/server.crt
