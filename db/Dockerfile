 FROM mysql:8.0


ENV MYSQL_ROOT_PASSWORD=rootpassword

RUN echo '[mysqld]\n\
default-authentication-plugin=mysql_native_password\n\
' > /etc/mysql/conf.d/auth.cnf

COPY seed_data.sql /seed_data.sql
COPY seed_injector.sh /seed_injector.sh
COPY start.sh /start.sh

RUN chmod +x /start.sh /seed_injector.sh

EXPOSE 3306

CMD ["/start.sh"]
