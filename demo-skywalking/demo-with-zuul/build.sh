#!/bin/bash

set -e

mvn package -Dmaven.test.skip

docker compose build
