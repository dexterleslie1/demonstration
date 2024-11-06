#!/bin/bash

kubectl delete -f k8s-application.yaml

kubectl delete pvc `kubectl get pvc|grep redis|awk '{print $1}'`
