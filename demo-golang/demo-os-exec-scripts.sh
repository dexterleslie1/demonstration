#!/bin/bash

declare -a arr=("frontend" "backend" "middleware" "apigateway")
for service in "${arr[@]}"
do
  sleep 2
  echo $service-prod-secret.yaml
done

# date1