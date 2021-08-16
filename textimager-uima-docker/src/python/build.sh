docker build --build-arg DOCKER_GROUP_ID=`getent group docker | cut -d: -f3` . -t docker_orchestrator
