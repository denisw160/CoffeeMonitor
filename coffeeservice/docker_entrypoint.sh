#!/bin/bash
#
# This is the entrypoint for the docker image.
#

PARAMS="--spring.profiles.active=production"

if [ ! -z "$DB_HOST" ]; then
    PARAMS="$PARAMS --app.db.host=$DB_HOST"
fi
if [ ! -z "$DB_PORT" ]; then
    PARAMS="$PARAMS --app.db.port=$DB_PORT"
fi
if [ ! -z "$DATABASE" ]; then
    PARAMS="$PARAMS --app.db.database=$DATABASE"
fi
if [ ! -z "$MQTT_URL" ]; then
    PARAMS="$PARAMS --app.mqtt.url=$MQTT_URL"
fi
if [ ! -z "$MQTT_USER" ]; then
    PARAMS="$PARAMS --app.mqtt.user=$MQTT_USER"
fi
if [ ! -z "$MQTT_PASSWORD" ]; then
    PARAMS="$PARAMS --app.mqtt.password=$MQTT_PASSWORD"
fi
if [ ! -z "$MQTT_TRUSTSTORE" ]; then
    PARAMS="$PARAMS --app.mqtt.trustStore=$MQTT_TRUSTSTORE"
fi
if [ ! -z "$MQTT_TRUSTSTORE_PASSWORD" ]; then
    PARAMS="$PARAMS --app.mqtt.trustStorePassword=$MQTT_TRUSTSTORE_PASSWORD"
fi
if [ ! -z "$WEB_USER" ]; then
    PARAMS="$PARAMS --app.web.user=$WEB_USER"
fi
if [ ! -z "$WEB_PASSWORD" ]; then
    PARAMS="$PARAMS --app.web.password=$WEB_PASSWORD"
fi
if [ ! -z "$LOG_SESSIONS" ]; then
    PARAMS="$PARAMS --app.web.logSessions=$LOG_SESSIONS"
fi

echo "Starting parameters: $PARAMS"
java -jar coffeeservice.jar $PARAMS