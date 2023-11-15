FROM openjdk:20-jdk

<<<<<<< HEAD
ARG JAR_FILE=target/*.jar

COPY ./target/driver-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
=======
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-cp", "/app:/app/lib/*", "io.voitovich.yura.driverservice.DriverServiceApplication"]
>>>>>>> dev
