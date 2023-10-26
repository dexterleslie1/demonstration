#!/bin/bash

kubectl delete -f deploy-master.yaml

kubectl delete -f deploy-slave.yaml

kubectl delete -f deploy-influxdb.yaml

kubectl delete -f deploy-grafana.yaml
