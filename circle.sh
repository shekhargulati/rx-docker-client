#!/bin/bash -ex

case "$1" in
  pre_machine)
    # have docker bind to both localhost and unix socket
    docker_opts='DOCKER_OPTS="$DOCKER_OPTS -D -H tcp://127.0.0.1:2375 -H unix:///var/run/docker.sock --registry-mirror=http://localhost:5000"'
    sudo sh -c "echo '$docker_opts' >> /etc/default/docker"

    cat /etc/default/docker

    ;;

  test)
    set +x

    case $CIRCLE_NODE_INDEX in
      0)
        # test with http
        export DOCKER_HOST=tcp://127.0.0.1:2375

        ;;

      1)
        # test with unix sockets
        export DOCKER_HOST=unix:///var/run/docker.sock

        ;;

    esac

    ./gradlew clean build -x signArchives

    ;;

  post_test)
    docker logs registry &> $CIRCLE_ARTIFACTS/registry.log

    cp target/surefire-reports/*.xml $CI_REPORTS

    ;;

esac
