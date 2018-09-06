#!/bin/bash
IMAGE_VER=3
bash ./docker_build.sh
sudo docker tag textimager-spacy texttechnologylab/textimager-spacy:${IMAGE_VER}
sudo docker push texttechnologylab/textimager-spacy:${IMAGE_VER}
