#!/bin/bash
# Wrapper script to build Spring Boot native image with Docker API version compatibility
export DOCKER_API_VERSION=1.42
./mvnw -Pnative spring-boot:build-image "$@"
