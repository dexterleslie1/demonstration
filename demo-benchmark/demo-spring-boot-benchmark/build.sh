#!/bin/bash

set -e

./mvnw package -Dmaven.test.skip=true

docker compose build
