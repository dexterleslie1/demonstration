#!/bin/bash

set -x
set -e

dockerRegistry=192.168.235.138:80/library

docker push $dockerRegistry/demo-jmeter-base:latest

docker push $dockerRegistry/demo-jmeter-master:latest

docker push $dockerRegistry/demo-jmeter-slave:latest
