#!/bin/bash

echo "✅ Generando keystore..."
keytool -genkey \
  -alias "jwt-sign-key" \
  -keyalg RSA \
  -keysize 4096 \
  -keystore "src/main/resources/jwt-keystore.jks" \
  -validity 3650 \
  -storepass "aladinos" \
  -keypass "aladinos" \
  -dname "CN=JWT, OU=Dev, O=MyCompany, L=LaPaz, ST=LaPaz, C=BO"

echo "🚀 Iniciando la aplicación..."
exec java -jar app.jar
