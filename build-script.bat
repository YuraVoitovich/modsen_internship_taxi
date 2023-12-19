call ride-service-build-script.bat
cd ../
call passenger-service-build-script.bat
cd ../
call driver-service-build-script.bat
cd ../
call docker-compose up -d