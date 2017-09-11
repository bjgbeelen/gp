FROM postgres:9.6-alpine

ADD docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
ADD conf /etc/postgresql
RUN chown -R postgres:postgres /etc/postgresql
RUN chmod og-rwx /etc/postgresql/server.key
ENV PGSSL 'off'
