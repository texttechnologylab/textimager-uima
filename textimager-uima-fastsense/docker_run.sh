#!/bin/bash
sudo docker run -p 5000:80 -it --rm --name fastsense-en -v $(pwd)/src/main/python/best_model:/model textimager-fastsense-en
