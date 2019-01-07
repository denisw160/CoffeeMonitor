#!/bin/bash
#
# This is the entrypoint for the docker image.
#

PARAMS="--spring.profiles.active=production"

if [ ! -z "$dbHost" ]; then
    PARAMS="$PARAMS --app.db.host=$dbHost"
fi
if [ ! -z "$dbPort" ]; then
    PARAMS="$PARAMS --app.db.port=$dbPort"
fi
if [ ! -z "$database" ]; then
    PARAMS="$PARAMS --app.db.database=$database"
fi
if [ ! -z "$mqttUrl" ]; then
    PARAMS="$PARAMS --app.mqtt.url=$mqttUrl"
fi
if [ ! -z "$mqttUser" ]; then
    PARAMS="$PARAMS --app.mqtt.user=$mqttUser"
fi
if [ ! -z "$mqttUser" ]; then
    PARAMS="$PARAMS --app.mqtt.password=$mqttPassword"
fi
if [ ! -z "$mqttTruststore" ]; then
    PARAMS="$PARAMS --app.mqtt.trustStore=$mqttTruststore"
fi
if [ ! -z "$mqttTruststorePassword" ]; then
    PARAMS="$PARAMS --app.mqtt.trustStorePassword=$mqttTruststorePassword"
fi
if [ ! -z "$webUser" ]; then
    PARAMS="$PARAMS --app.web.user=$webUser"
fi
if [ ! -z "$webPassword" ]; then
    PARAMS="$PARAMS --app.web.password=$webPassword"
fi
if [ ! -z "$logSessions" ]; then
    PARAMS="$PARAMS --app.web.logSessions=$logSessions"
fi

/usr/bin/java -jar coffeeservice.jar $PARAMS