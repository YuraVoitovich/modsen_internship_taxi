FROM openjdk:20-jdk

ARG JAR_FILE=target/*.jar

COPY ./target/driver-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]