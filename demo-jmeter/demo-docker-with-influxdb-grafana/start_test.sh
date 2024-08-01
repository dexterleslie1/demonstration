#!/bin/bash

docker compose cp jmeter.jmx demo-jmeter:/jmeter.jmx

docker compose exec -it demo-jmeter jmeter -n -t /jmeter.jmx
