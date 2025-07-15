#!/bin/bash

set -e

mvn package -Dmaven.test.skip

# 编译 ui
(cd ui && npm run build)

docker compose build
