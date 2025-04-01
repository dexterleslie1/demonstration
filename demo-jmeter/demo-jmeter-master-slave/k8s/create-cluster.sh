#!/bin/bash

kubectl apply -f deploy-master.yaml

kubectl apply -f deploy-slave.yaml

kubectl apply -f deploy-influxdb.yaml

kubectl apply -f deploy-grafana.yaml
