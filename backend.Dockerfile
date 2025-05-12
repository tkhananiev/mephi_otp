# Родительский образ контейнера с maven внутри
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests=true --file pom.xml

FROM eclipse-temurin AS run
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]