#!/usr/bin/env bash

set -euo pipefail

# Usage: ./build-run.sh [dev|default|qa|prod] [--no-build]
#   dev: builds images into local Docker only (tag :dev), nothing is pushed
#   others: builds and pushes :latest images to Docker Hub

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
    dev|default|qa|prod)
        ;;
    *)
        echo "Invalid environment: $ENVIRONMENT"
        echo "Usage: $0 [dev|default|qa|prod] [--no-build]"
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
            if [[ "$ENVIRONMENT" == dev ]]; then
                mvn compile jib:dockerBuild -Djib.to.image="sahspeaks/$SERVICE:dev"
            else
                mvn compile jib:build
            fi
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