FROM openjdk:20-jdk

ARG JAR_FILE=target/*.jar

COPY ./target/passenger-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]