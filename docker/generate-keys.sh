#!/usr/bin/env bash
# generate-keys.sh — Generates RSA key pair and writes to .env
# Run from the project root: ./docker/generate-keys.sh
# Requires: openssl, base64 (both standard on macOS and Linux)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
ENV_FILE="$PROJECT_ROOT/.env"

echo "Generating RSA 2048-bit key pair..."

# Generate PEM key pair in a temp directory
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

openssl genrsa -out "$TMP_DIR/keypair.pem" 2048 2>/dev/null

# Export private key as PKCS#8 DER (required by JwtConfig: PKCS8EncodedKeySpec)
openssl pkcs8 -topk8 -inform PEM -outform DER \
  -in "$TMP_DIR/keypair.pem" \
  -out "$TMP_DIR/private.der" \
  -nocrypt

# Export public key as X.509/SubjectPublicKeyInfo DER (required by JwtConfig: X509EncodedKeySpec)
openssl rsa -in "$TMP_DIR/keypair.pem" -pubout -outform DER \
  -out "$TMP_DIR/public.der" 2>/dev/null

# Base64 encode (handle macOS vs Linux base64 flag differences)
if [[ "$(uname)" == "Darwin" ]]; then
  PRIVATE_B64=$(base64 -i "$TMP_DIR/private.der")
  PUBLIC_B64=$(base64 -i "$TMP_DIR/public.der")
else
  # GNU base64: -w 0 disables line-wrapping
  PRIVATE_B64=$(base64 -w 0 < "$TMP_DIR/private.der")
  PUBLIC_B64=$(base64 -w 0 < "$TMP_DIR/public.der")
fi

# Write or update .env
# Preserve any existing lines not related to JWT keys
if [[ -f "$ENV_FILE" ]]; then
  # Remove existing JWT key lines before re-adding
  grep -v "^JWT_PRIVATE_KEY=" "$ENV_FILE" | grep -v "^JWT_PUBLIC_KEY=" > "$TMP_DIR/env_existing" || true
  cp "$TMP_DIR/env_existing" "$ENV_FILE"
fi

printf "\nJWT_PRIVATE_KEY=%s\n" "$PRIVATE_B64" >> "$ENV_FILE"
printf "JWT_PUBLIC_KEY=%s\n" "$PUBLIC_B64" >> "$ENV_FILE"

echo "Keys written to $ENV_FILE"
echo ""
echo "WARNING: .env is gitignored. Do not commit it."
echo "Each developer must run this script once."
