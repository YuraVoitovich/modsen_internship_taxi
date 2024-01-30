cd api-gateway

call mvn clean package %1
call docker build -t api-gateway .