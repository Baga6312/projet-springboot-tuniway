# MySQL Dockerfile with seed data
FROM mysql:8.0

# Set environment variables
ENV MYSQL_ROOT_PASSWORD=
ENV MYSQL_DATABASE=tuniway
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=

COPY seed_data.sql /docker-entrypoint-initdb.d/

# Expose MySQL port
EXPOSE 3306

