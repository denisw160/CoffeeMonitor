#!/bin/sh
#
# This is the entrypoint for the docker image.
# 
python dummysensor.py --server "$MQTT_SERVER" --port "$MQTT_PORT" --ca "$SSL_CA" --user "$MQTT_USER" --password "$MQTT_PASSWORD" 
