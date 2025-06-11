#!/bin/bash

echo "Generando keystore..."
keytool -genkeypair \
  -alias jwt-sign-key \
  -keyalg RSA \
  -keysize 2048 \
  -storetype JKS \
  -keystore "$KEYSTORE_PATH" \
  -validity 3650 \
  -storepass "$STORE_PASS" \
  -keypass "$KEY_PASS" \
  -dname "$DNAME"

# Ejecutar la app
exec java -jar app.jar