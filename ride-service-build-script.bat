cd ride-service

call mvn clean package %1
call docker build -t ride-service .