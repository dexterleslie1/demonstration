#!/bin/bash

set -x
set -e

dockerRegistry=192.168.235.138:80/library

docker tag demo-k8s-jmeter-base:latest $dockerRegistry/demo-jmeter-base:latest
docker push $dockerRegistry/demo-jmeter-base:latest

docker tag demo-k8s-jmeter-master:latest $dockerRegistry/demo-jmeter-master:latest
docker push $dockerRegistry/demo-jmeter-master:latest

docker tag demo-k8s-jmeter-slave:latest $dockerRegistry/demo-jmeter-slave:latest
docker push $dockerRegistry/demo-jmeter-slave:latest
