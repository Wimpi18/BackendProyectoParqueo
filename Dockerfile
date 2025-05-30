# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Aprovechar cache de dependencias
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copiar el resto del código
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]