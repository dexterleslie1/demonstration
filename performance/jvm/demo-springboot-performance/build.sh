#!/bin/bash

set -x
set -e

mvn package

docker build --tag demo-springboot-performance-dev --file Dockerfile --build-arg jarFile=demo-springboot-performance.jar .
