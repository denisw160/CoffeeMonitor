#!/bin/bash
#
# Building the service and prepare the Docker images.
#
# User must have access to Docker.
#
# Usage: ./build.sh
# 

NAME=coffee-service
TAG=latest

# Remove unused images
#echo Remove unused images
#docker image prune -a -f

# Building the image
echo Building the image
docker build -t $NAME:$TAG .

# Running the container
#echo Running the container on port 8889
#docker run --rm -it --name coffee-service -p 8889:8080 $NAME:$TAG
