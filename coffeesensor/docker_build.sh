#!/bin/bash
#
# Building the sensor (onyl for test!) and prepare the Docker images.
#
# User must have access to Docker.
#
# Usage: ./build.sh
# 

NAME=coffee-sensor
TAG=latest

# Remove unused images
#echo Remove unused images
#docker image prune -a -f

# Building the image
echo Building the image
docker build -t $NAME:$TAG .

# Running the container
#echo Running the container
#docker run --rm -it --name coffee-testsensor --link coffee-mosquitto:mqtt $NAME:$TAG

## need mosquitto mqtt broker
#docker run -d --name coffee-mosquitto --restart always -p 1883:1883 -v coffee-mosquitto-conf:/mosquitto/config -v coffee-mosquitto-data:/mosquitto/data -v coffee-mosquitto-log:/mosquitto/log eclipse-mosquitto:latest
