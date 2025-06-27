#!/bin/bash

set -e

mvn clean package -Dmaven.test.skip

docker compose build
