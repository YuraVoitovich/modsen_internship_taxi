cd passenger-service

call mvn clean package %1
call docker build -t passenger-service .