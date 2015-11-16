#!/bin/bash -ex

docker_opts='DOCKER_OPTS="$DOCKER_OPTS -D -H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock --registry-mirror=http://localhost:5000"'
sudo sh -c "echo '$docker_opts' >> /etc/default/docker"
cat /etc/default/docker
export DOCKER_HOST=tcp://127.0.0.1:2375