#!/bin/bash

set -x
set -e

dockerRegistry=192.168.235.138:80/library

cp -r ../../demo-jmeter-customize-plugin/target ./target

docker build --tag $dockerRegistry/demo-jmeter-base:latest -f Dockerfile-base .

docker build --tag $dockerRegistry/demo-jmeter-master:latest -f Dockerfile-master .

docker build --tag $dockerRegistry/demo-jmeter-slave:latest -f Dockerfile-slave .
