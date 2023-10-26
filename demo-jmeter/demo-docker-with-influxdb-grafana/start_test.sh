#!/bin/bash

docker cp jmeter.jmx demo-jmeter:/jmeter.jmx

docker exec -it demo-jmeter jmeter -n -t /jmeter.jmx
