import argparse
import datetime
import json
import random
import socket
import ssl
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
MQTT_TOPIC = "me/wirries/coffeesensor"
MQTT_KEEPALIVE_INTERVAL = 45
INTERVAL = 15


# Functions

# Define on_publish event function
def on_publish(client, userdata, mid):
    print "Message published..."


# Define isNotBlank function for testing, if a String is blank
def is_not_blank(s):
    if s and s.strip():
        return True
    return False


# Starting server
ap = argparse.ArgumentParser()
ap.add_argument("-s", "--server", required=False, help="server / default: " + MQTT_HOST, default=MQTT_HOST)
ap.add_argument("-p", "--port", required=False, help="port / default: " + str(MQTT_PORT), default=str(MQTT_PORT))
ap.add_argument("-c", "--ca", required=False, help="path to ca file (full chain) to verify the SSL connection")
ap.add_argument("-t", "--topic", required=False, help="topic for publish / default: " + MQTT_TOPIC, default=MQTT_TOPIC)
ap.add_argument("-u", "--user", required=False, help="username for login")
ap.add_argument("-w", "--password", required=False, help="password for user")
args = vars(ap.parse_args())

host = args["server"]
port = args["port"]
ca = args["ca"]
topic = args["topic"]
user = args["user"]
password = args["password"]

print "CoffeeSensor (dummy) started - for stopping please press CRTL-c"
print " - MQTT-Server:", host
print " - MQTT-Port:", port
print " - MQTT-Keepalive:", MQTT_KEEPALIVE_INTERVAL
print " - MQTT-Topic:", topic

loginEnabled = is_not_blank(user) and is_not_blank(password)
if loginEnabled:
    print " - use login for connection with user:", user

sslEnabled = is_not_blank(ca)
if sslEnabled:
    print " - use ca file for SSL connection:", ca

# Initiate MQTT Client
mqttc = mqtt.Client(client_id="CoffeeSensor_" + socket.gethostname())
# Register publish callback function
mqttc.on_publish = on_publish

# Setup login for connection
if loginEnabled:
    mqttc.username_pw_set(user, password)

# Setup SSL connection
if sslEnabled:
    mqttc.tls_set(ca, tls_version=ssl.PROTOCOL_TLSv1_2)
    mqttc.tls_insecure_set(False)

# Connect with MQTT Broker
mqttc.connect(host, port, MQTT_KEEPALIVE_INTERVAL)

# Running server
try:
    while True:
        timestamp = str(datetime.datetime.now().isoformat())
        weight = random.uniform(0.3, 2.8)
        allocated = random.choice([True, False])

        msg = json.dumps({"timestamp": timestamp, "weight": weight, "allocated": allocated})
        # Publish message to MQTT Broker
        mqttc.publish(topic, msg)

        # wait for next update
        time.sleep(INTERVAL)

except KeyboardInterrupt:
    print "Strg-C called"

finally:
    print "CoffeeSensor stopped"
    # Disconnect from MQTT_Broker
    mqttc.disconnect()
