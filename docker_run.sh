#!/bin/bash
#
# Running the system (without the coffeesensor) as Docker containers.
# Before you can this script, please build the Docker images with
# ./docker_build.sh
#
# User must have access to Docker.
#
# Usage: ./docker_run.sh 
# 

TAG=latest

# Stopping old containers
docker stop coffee-app
docker stop coffee-service
docker stop coffee-testsensor
docker stop coffee-mosquitto
docker stop coffee-mongodb

docker rm coffee-app
docker rm coffee-service
docker rm coffee-testsensor
docker rm coffee-mosquitto
docker rm coffee-mongodb

## Additional environment parameter for the containers
# coffee-service
# -e DB_HOST=db
# -e DB_PORT=27017
# -e DATABASE=coffeeservice
# -e MQTT_URL=tcp://mqtt:1883
# -e MQTT_USER=
# -e MQTTPASSWORD=
# -e MQTT_TRUSTSTORE=
# -e MQTT_TRUSTSTORE_PASSWORD=
# -e WEB_USER=
# -e WEB_PASSWORD=
# -e LOG_SESSIONS=false

# coffee-sensor
# -e MQTT_SERVER=mqtt
# -e MQTT_PORT=1883
# -e MQTT_USER=
# -e MQTT_PASSWORD=
# -e SSL_CA=

# coffee-app
# TODO implement ENV & script
# -e BASE_HREF=/

# Run the containers
docker run -d --name coffee-mongodb -p 27017:27017 -v coffee-mongodb-data:/data/db mongo:latest
docker run -d --name coffee-mosquitto -p 1883:1883 -v coffee-mosquitto-conf:/mosquitto/config -v coffee-mosquitto-data:/mosquitto/data -v coffee-mosquitto-log:/mosquitto/log eclipse-mosquitto:latest
docker run -d --name coffee-testsensor --link coffee-mosquitto:mqtt coffee-sensor:$TAG
docker run -d --name coffee-service -p 8080:8080 --link coffee-mongodb:db --link coffee-mosquitto:mqtt coffee-service:$TAG
docker run -d --name coffee-app --link coffee-service:apiserver -p 80:80 coffee-app:$TAG
