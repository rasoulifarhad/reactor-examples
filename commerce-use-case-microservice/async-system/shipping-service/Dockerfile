FROM openjdk:8-jdk-alpine
COPY target/shipping-service-async-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/app.jar"]
