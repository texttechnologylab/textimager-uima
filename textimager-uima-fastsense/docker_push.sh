#!/bin/bash
IMAGE_VER=1
bash ./docker_build.sh
sudo docker tag textimager-fastsense-en texttechnologylab/textimager-fastsense-en:${IMAGE_VER}
sudo docker push texttechnologylab/textimager-fastsense-en:${IMAGE_VER}
