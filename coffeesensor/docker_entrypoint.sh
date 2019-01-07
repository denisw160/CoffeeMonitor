#!/bin/sh
#
# This is the entrypoint for the docker image.
# 
python dummysensor.py --server "$mqttServer" --port "$mqttPort" --ca "$sslCa" --user "$mqttUser" --password "$mqttPassword" 
