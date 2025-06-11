# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x entrypoint.sh

# Crear el directorio necesario para el keystore
RUN mkdir -p src/main/resources

# Pasar variables desde .env (se hará en tiempo de ejecución)
EXPOSE 8080
ENTRYPOINT ["./entrypoint.sh"]