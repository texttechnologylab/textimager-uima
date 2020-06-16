#!/bin/bash
conda create --name stanza python=3.7
conda activate stanza
conda install pip
pip install stanza jep
