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

# Copiar el .jar
COPY --from=build /app/target/*.jar app.jar

# Copiar script y declarar variables
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x entrypoint.sh

ENV KEYSTORE_PATH=src/main/resources/jwt-keystore.jks
ENV DNAME="CN=JWT, OU=Dev, O=MyCompany, L=LaPaz, ST=LaPaz, C=BO"

# Exponer el puerto
EXPOSE 8080

ENTRYPOINT ["./entrypoint.sh"]