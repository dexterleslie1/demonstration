#!/bin/bash

mvn package

docker build --tag demo-spring-boot-test-db --file Dockerfile-db .

docker build --tag demo-spring-boot-test-service --file Dockerfile-service .
