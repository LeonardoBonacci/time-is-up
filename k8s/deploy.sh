#!/bin/bash

cat <<EOF | kind create cluster --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  extraPortMappings:
  - containerPort: 30000
    hostPort: 30000
    protocol: TCP
  - containerPort: 30001
    hostPort: 30001
    protocol: TCP
  - containerPort: 30002
    hostPort: 30002
    protocol: TCP
  - containerPort: 30003
    hostPort: 30003
    protocol: TCP
EOF

NAMESPACE="demo"
STRIMZI_VER="0.20.0"

# create a new namespace
kubectl create namespace $NAMESPACE && kubectl config set-context --current --namespace=$NAMESPACE

# deploy Strimzi operator
curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/$STRIMZI_VER/strimzi-cluster-operator-$STRIMZI_VER.yaml \
    | sed "s/namespace: .*/namespace: $NAMESPACE/g" | kubectl apply -f -

kubectl wait --for=condition=ready pod -l name=strimzi-cluster-operator --timeout=-1s

# deploy Kafka cluster
kubectl apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/release-${STRIMZI_VER/%.0/.x}/examples/kafka/kafka-persistent-single.yaml

# and wait a while
kubectl wait --for=condition=ready pod my-cluster-zookeeper-0 --timeout=90s
kubectl wait --for=condition=ready pod my-cluster-kafka-0 --timeout=90s

--------------------
kubectl -n $NAMESPACE create configmap kafka-config --from-literal kafka.bootstrap.servers=my-cluster-kafka-bootstrap:9092
kubectl apply -f topics.yaml

--------------------
mvn clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.push=true
mvn clean package -Dquarkus.container-image.push=true
kubectl apply -f target/kubernetes/kubernetes.yml
#2020-11-24 23:44:03,650 INFO  [io.quarkus] (main) react-unmoved-gate 1.0-SNAPSHOT native (powered by Quarkus 1.9.2.Final) started in 0.027s. Listening on: http://0.0.0.0:8080