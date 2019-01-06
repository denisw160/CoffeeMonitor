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
#docker run --rm -it --name coffee-service -p 8889:8080 --link coffee-mongodb:db $NAME:$TAG

## need database
#docker run -d --name coffee-mongodb --restart always -v coffee-mongodb-data:/data/db mongo:latest

## need mosquitto mqtt broker
#docker run -d --name mosquitto --restart always -p 1883:1883 -p 9001:9001 -v mosquitto-conf:/mosquitto/config -v mosquitto-data:/mosquitto/data -v mosquitto-log:/mosquitto/log eclipse-mosquitto:latest
