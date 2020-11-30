cat <<EOF | kind create cluster --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  extraPortMappings:
  - containerPort: 30000
    hostPort: 30000
  - containerPort: 30001
    hostPort: 30001
  - containerPort: 30002
    hostPort: 30002
  - containerPort: 30003
    hostPort: 30003
  - containerPort: 30004
    hostPort: 30004
  - containerPort: 32100
    hostPort: 32100
  - containerPort: 32000
    hostPort: 32000
  - containerPort: 32001
    hostPort: 32001
  - containerPort: 32002
    hostPort: 32002
- role: worker
- role: worker
EOF

NAMESPACE="kafka"
STRIMZI_VER="0.20.0"

kubectl create namespace $NAMESPACE && kubectl config set-context --current --namespace=$NAMESPACE

curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/$STRIMZI_VER/strimzi-cluster-operator-$STRIMZI_VER.yaml \
    | sed "s/namespace: .*/namespace: $NAMESPACE/g" | kubectl apply -f -

kubectl wait --for=condition=ready pod -l name=strimzi-cluster-operator --timeout=-1s

# deploy Kafka cluster
#kubectl apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/release-${STRIMZI_VER/%.0/.x}/examples/kafka/kafka-persistent-single.yaml
#https://github.com/strimzi/strimzi-kafka-operator/tree/master/examples/kafka
kubectl apply -f k8s/kafka-persistent.yaml

# and wait a while
kubectl wait --for=condition=ready pod my-cluster-zookeeper-0 --timeout=120s
kubectl wait --for=condition=ready pod my-cluster-kafka-0 --timeout=120s

kubectl -n $NAMESPACE apply -f k8s/topics.yaml
kubectl -n $NAMESPACE apply -f k8s/internal-topics.yaml

kubectl exec -i my-cluster-kafka-0 -c kafka -- \
    bin/kafka-configs.sh --alter \
    --bootstrap-server localhost:9092 \
    --entity-type topics \
    --entity-name track \
    --add-config delete.retention.ms=60000

####################
kubectl create configmap kafka-config --from-literal=kafka.bootstrap.servers=my-cluster-kafka-bootstrap:9092 --from-literal=quarkus.kafka-streams.bootstrap-servers=my-cluster-kafka-bootstrap:9092
####################
mvn clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
mvn clean package -Dquarkus.profile=docker-registry -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
mvn clean package -Dquarkus.container-image.push=true
mvn clean package -Dquarkus.container-image.build=true

kind load docker-image leonardobonacci/timesup-react-unmoved-gate:4.2
kubectl apply -f react-unmoved-gate/target/kubernetes/kubernetes.yml
kubectl apply -f target/kubernetes/kubernetes.yml
#2020-11-24 23:44:03,650 INFO  [io.quarkus] (main) react-unmoved-gate 1.0-SNAPSHOT native (powered by Quarkus 1.9.2.Final) started in 0.027s. Listening on: http://0.0.0.0:8080
docker exec -it kind-control-plane crictl images

kubectl get service my-cluster-kafka-external-bootstrap -o=jsonpath='{.spec.ports[0].nodePort}{"\n"}' # 32100
kubectl get node kind-control-plane -o=jsonpath='{range .status.addresses[*]}{.type}{"\t"}{.address}{"\n"}' # 172.18.0.2
kubectl exec my-cluster-kafka-0 -c kafka -it -- cat /tmp/strimzi.properties | grep advertised

################## INSTALL PROMETHEUS AND MORE FOR KUBE CLUSTER MONITORING
https://www.replex.io/blog/kubernetes-in-production-the-ultimate-guide-to-monitoring-resource-metrics

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install --name prometheus prometheus-community

kubectl create namespace monitoring
helm install --generate-name prometheus-community/prometheus-operator --namespace monitoring

kubectl port-forward -n monitoring prometheus-prometheus-operator-160669-prometheus-0 9090:9090

sum(rate(container_cpu_usage_seconds_total{container_name!="POD",pod!=""}[5m]))
rate(container_cpu_usage_seconds_total{container_name!="POD",pod!=""}[5m])
