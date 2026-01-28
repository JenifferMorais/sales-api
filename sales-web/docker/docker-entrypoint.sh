#!/bin/sh
set -eu

echo "[entrypoint] Starting Sales Web..."

PORT="${PORT:-10000}"

# Render sets $PORT; make nginx listen on it
if [ -f /etc/nginx/nginx.conf ]; then
  sed -i "s/listen 80;/listen ${PORT};/g" /etc/nginx/nginx.conf || true
fi

# Replace API URL in built JS (no rebuild needed)
if [ -n "${API_URL:-}" ]; then
  echo "[entrypoint] Configuring API_URL=$API_URL"
  # Support both the placeholder token and the previous localhost default.
  find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|__API_URL__|$API_URL|g" {} +
  find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|http://localhost:8080/api|$API_URL|g" {} +
else
  echo "[entrypoint] API_URL not set; using default http://localhost:8080/api"
fi

# Health check endpoint
printf "OK\n" > /usr/share/nginx/html/health

echo "[entrypoint] Ready (port=${PORT})"
exec "$@"
