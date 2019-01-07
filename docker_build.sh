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

cd $WORKDIR/coffeeservice
./docker_build.sh

cd $WORKDIR/coffeeapp
./docker_build.sh $BASEHREF
