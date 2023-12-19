cd passenger-service

call mvn clean package
call docker build -t passenger-service .