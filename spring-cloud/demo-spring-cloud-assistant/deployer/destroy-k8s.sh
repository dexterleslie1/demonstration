#!/bin/bash

kubectl delete -f k8s-application.yaml

kubectl delete pvc `kubectl get pvc|grep redis-cluster|awk '{print $1}'`
kubectl delete pvc data-elasticsearch-elasticsearch-0
kubectl delete pvc db-data-db-0
