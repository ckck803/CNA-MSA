#!/usr/bin/env bash
#

cd app
kubectl apply -f ./kubernetes --namespace=team02
cd ..
cd pay
kubectl apply -f ./kubernetes --namespace=team02
cd ..
cd movie
kubectl apply -f ./kubernetes --namespace=team02
cd ..
cd theater
kubectl apply -f ./kubernetes --namespace=team02
cd ..
cd notice
kubectl apply -f ./kubernetes --namespace=team02
cd ..
cd gateway
kubectl apply -f ./kubernetes --namespace=team02
kubectl create deploy gateway --image=kidshim/gateway:v1 -n team02
kubectl expose deploy gateway --type="LoadBalancer" --port=8080 -n team02
cd ..