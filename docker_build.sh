#!/bin/bash
#
# Building the Docker images for the Angular App and Spring Boot Application.
#
# User must have access to Docker.
#
# Usage: ./docker_build.sh
# 

# Parameter for the build, modify if needed
BASEHREF=/

# Variables
WORKDIR=$(pwd)

# Remove unused images
#echo Remove unused images
#docker image prune -a -f

cd $WORKDIR/coffeesensor
./docker_build.sh

cd $WORKDIR/coffeeservice
./docker_build.sh

cd $WORKDIR/coffeeapp
./docker_build.sh $BASEHREF
