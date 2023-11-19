FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY /src /src
COPY checkstyle-suppressions.xml /
COPY pom.xml /
RUN mvn -f /pom.xml clean package

FROM  eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]

