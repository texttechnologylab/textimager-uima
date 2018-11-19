#!/bin/bash
IMAGE_VER=2
bash ./docker_build.sh
sudo docker tag textimager-neuralnetwork-ner texttechnologylab/textimager-neuralnetwork-ner:${IMAGE_VER}
sudo docker push texttechnologylab/textimager-neuralnetwork-ner:${IMAGE_VER}
