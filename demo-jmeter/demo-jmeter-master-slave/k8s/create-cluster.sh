#!/bin/bash

kubectl apply -f deploy-master-configmap.yaml

kubectl apply -f deploy-master.yaml

kubectl apply -f deploy-slave.yaml

kubectl apply -f deploy-slave-service.yaml
