call ride-service-build-script.bat -DskipTests
cd ../
call passenger-service-build-script.bat -DskipTests
cd ../
call driver-service-build-script.bat -DskipTests
cd ../
call docker-compose up -d