FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=password

RUN echo '[mysqld]\ndefault-authentication-plugin=mysql_native_password\n' > /etc/mysql/conf.d/auth.cnf

COPY seed_data.sql /seed_data.sql
COPY seed_injector.sh /seed_injector.sh
COPY start.sh /start.sh

# Fix line endings using sed (available by default) and make executable
RUN sed -i 's/\r$//' /start.sh /seed_injector.sh && \
    chmod +x /start.sh /seed_injector.sh

EXPOSE 3306

CMD ["/start.sh"]