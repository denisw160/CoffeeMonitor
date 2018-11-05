import datetime
import json
import random
import time

import paho.mqtt.client as mqtt

#
# This scripts starts a dummy service, that sends random sensor values to the backend.
# For transmission a mqtt broker is used. The format of the data is json:
# {"timestamp":"2012-04-23T18:25:43.511Z", "weight":[double], "allocated":[boolean]}
#
# The mqtt topic "me/wirries/coffeesensor" is used.
#
# require modules
#  - paho-mqtt (pip)
#

# Defaults

MQTT_HOST = "localhost"
MQTT_PORT = 1883
MQTT_KEEPALIVE_INTERVAL = 45
MQTT_TOPIC = "me/wirries/coffeesensor"
INTERVAL = 15


# Functions

# Define on_publish event function
def on_publish(client, userdata, mid):
    print "Message Published..."


# Starting server
print "CoffeeSensor (dummy) started - for stopping please press CRTL-c"
print " - MQTT-Broker:", MQTT_HOST
print " - MQTT-Port:", MQTT_PORT
print " - MQTT-Keepalive:", MQTT_KEEPALIVE_INTERVAL
print " - MQTT-Topic:", MQTT_TOPIC

# Initiate MQTT Client
mqttc = mqtt.Client()
# Register publish callback function
mqttc.on_publish = on_publish
# Connect with MQTT Broker
mqttc.connect(MQTT_HOST, MQTT_PORT, MQTT_KEEPALIVE_INTERVAL)

# Running server
try:
    while True:
        timestamp = str(datetime.datetime.now().isoformat())
        weight = random.uniform(0.3, 2.8)
        allocated = random.choice([True, False])

        msg = json.dumps({"timestamp": timestamp, "weight": weight, "allocated": allocated})
        # Publish message to MQTT Broker
        mqttc.publish(MQTT_TOPIC, msg)

        # wait for next update
        time.sleep(INTERVAL)

except KeyboardInterrupt:
    print "Strg-C called"

finally:
    print "CoffeeSensor stopped"
    # Disconnect from MQTT_Broker
    mqttc.disconnect()
