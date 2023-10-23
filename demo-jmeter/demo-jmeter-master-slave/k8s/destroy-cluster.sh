#!/bin/bash

kubectl delete -f deploy-master-configmap.yaml

kubectl delete -f deploy-master.yaml

kubectl delete -f deploy-slave.yaml

kubectl delete -f deploy-slave-service.yaml
