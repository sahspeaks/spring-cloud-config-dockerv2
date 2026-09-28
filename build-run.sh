#!/usr/bin/env bash

set -euo pipefail

# Usage: ./build-run.sh [default|qa|prod] [--no-build]

ENVIRONMENT="${1:-default}"
BUILD=true

if [[ "${2:-}" == "--no-build" ]]; then
    BUILD=false
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$ROOT_DIR/docker-compose/$ENVIRONMENT"
COMPOSE_FILE="$COMPOSE_DIR/docker-compose.yml"

# Validate environment
case "$ENVIRONMENT" in
    default|qa|prod)
        ;;
    *)
        echo "Invalid environment: $ENVIRONMENT"
        echo "Usage: $0 [default|qa|prod] [--no-build]"
        exit 1
        ;;
esac

if [[ ! -f "$COMPOSE_FILE" ]]; then
    echo "Docker Compose file not found: $COMPOSE_FILE"
    exit 1
fi

# Build images using Jib
if [[ "$BUILD" == true ]]; then
    echo "Building images for environment: $ENVIRONMENT"

    for SERVICE in configserver accounts loans cards; do
        echo "Building $SERVICE..."

        (
            cd "$ROOT_DIR/$SERVICE"
            mvn compile jib:build
        )
    done
fi

# Start services
echo "Starting services for environment: $ENVIRONMENT"

docker compose \
    -f "$COMPOSE_FILE" \
    up -d --remove-orphans

echo ""
echo "Services started."

docker compose \
    -f "$COMPOSE_FILE" \
    ps

echo ""
echo "To view logs:"
echo "docker compose -f \"$COMPOSE_FILE\" logs -f"