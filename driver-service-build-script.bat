cd driver-service

call mvn clean package %1
call docker build -t driver-service .