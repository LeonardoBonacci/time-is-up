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
EOF

NAMESPACE="kafka"
STRIMZI_VER="0.20.0"

# create a new namespace
kubectl create namespace $NAMESPACE && kubectl config set-context --current --namespace=$NAMESPACE

# deploy Strimzi operator
curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/$STRIMZI_VER/strimzi-cluster-operator-$STRIMZI_VER.yaml \
    | sed "s/namespace: .*/namespace: $NAMESPACE/g" | kubectl apply -f -

kubectl wait --for=condition=ready pod -l name=strimzi-cluster-operator --timeout=-1s

# deploy Kafka cluster
#kubectl apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/release-${STRIMZI_VER/%.0/.x}/examples/kafka/kafka-persistent-single.yaml
#https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/2ab51a232b38a88ee434486788adbe33187f350b/examples/kafka/kafka-persistent-single.yaml
kubectl apply -f kafka-persistent-single.yaml

# and wait a while
kubectl wait --for=condition=ready pod my-cluster-zookeeper-0 --timeout=90s
kubectl wait --for=condition=ready pod my-cluster-kafka-0 --timeout=90s

kubectl -n kafka apply -f topics.yaml
kubectl -n kafka apply -f kafka-connect/connect-cluster.yaml

--------------------
kubectl create configmap kafka-config --from-literal=kafka.bootstrap.servers=my-cluster-kafka-bootstrap:9092 --from-literal=quarkus.kafka-streams.bootstrap-servers=my-cluster-kafka-bootstrap:9092
kubectl apply -f tile38.yaml
kubectl apply -f kafka-connect/tile-sink-connector.yaml

kubectl port-forward tile38 9851 #bash not installed on the pod :(
SETHOOK arrivals kafka://my-cluster-kafka-bootstrap:9092/arrival-raw NEARBY trace FENCE ROAM unmoved * 1000


--------------------
mvn clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.push=true
mvn clean package -Dquarkus.container-image.push=true
kubectl apply -f target/kubernetes/kubernetes.yml
#2020-11-24 23:44:03,650 INFO  [io.quarkus] (main) react-unmoved-gate 1.0-SNAPSHOT native (powered by Quarkus 1.9.2.Final) started in 0.027s. Listening on: http://0.0.0.0:8080

kubectl get service my-cluster-kafka-external-bootstrap -o=jsonpath='{.spec.ports[0].nodePort}{"\n"}' # 30092
kubectl get node kind-control-plane -o=jsonpath='{range .status.addresses[*]}{.type}{"\t"}{.address}{"\n"}' # 172.18.0.2
kubectl exec my-cluster-kafka-0 -c kafka -it -- cat /tmp/strimzi.properties | grep advertised
