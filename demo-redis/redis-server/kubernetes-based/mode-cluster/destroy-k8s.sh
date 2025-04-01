#!/bin/bash

kubectl delete -f .

kubectl delete pvc `kubectl get pvc|grep redis|awk '{print $1}'`
