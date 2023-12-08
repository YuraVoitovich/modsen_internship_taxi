cd driver-service

call mvn clean package
call docker build -t driver-service .