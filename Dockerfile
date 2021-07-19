# syntax=docker/dockerfile:1
FROM adoptopenjdk/openjdk11:ubi
WORKDIR /scrittorello
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
CMD ["./mvnw", "spring-boot:run"]

