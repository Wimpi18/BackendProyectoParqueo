#!/bin/bash

# Cargar variables desde el archivo .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "❌ No se encontró el archivo .env"
  exit 1
fi

# Validación de contraseñas
if [[ ${#KEY_PASS} -lt 6 || ${#STORE_PASS} -lt 6 ]]; then
  echo "❌ ERROR: Las contraseñas deben tener al menos 6 caracteres."
  exit 1
fi

echo "✅ Generando keystore..."
keytool -genkey \
  -alias "jwt-sign-key" \
  -keyalg RSA \
  -keysize 4096 \
  -storetype JKS \
  -keystore "$KEYSTORE_PATH" \
  -validity 3650 \
  -storepass "$STORE_PASS" \
  -keypass "$KEY_PASS" \
  -dname "$DNAME"

echo "🚀 Iniciando la aplicación..."
exec java -jar app.jar
