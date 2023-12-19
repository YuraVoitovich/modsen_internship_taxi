cd discovery-service

call mvn clean package
call docker build -t discovery-service .