#!/bin/bash
IMAGE_VER=1.0.1
bash ./docker_build.sh
sudo docker tag textimager-cltk texttechnologylab/textimager-cltk:${IMAGE_VER}
sudo docker push texttechnologylab/textimager-cltk:${IMAGE_VER}
