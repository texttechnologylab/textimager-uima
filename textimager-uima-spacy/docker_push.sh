#!/bin/bash
IMAGE_VER=2
bash ./docker_build.sh
sudo docker tag textimager-spacy texttechnologylab/textimager-spacy:${IMAGE_VER}
sudo docker push texttechnologylab/textimager-spacy:${IMAGE_VER}
