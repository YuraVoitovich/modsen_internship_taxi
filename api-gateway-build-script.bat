cd api-gateway

call mvn clean package
call docker build -t api-gateway .