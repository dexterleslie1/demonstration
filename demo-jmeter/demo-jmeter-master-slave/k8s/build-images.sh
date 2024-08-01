#!/bin/bash

set -x
set -e

cp -r ../../demo-jmeter-customize-plugin/target ./target

docker build --tag demo-k8s-jmeter-base:latest -f Dockerfile-base .

docker build --tag demo-k8s-jmeter-master:latest -f Dockerfile-master .

docker build --tag demo-k8s-jmeter-slave:latest -f Dockerfile-slave .
