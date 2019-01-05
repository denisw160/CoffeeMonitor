import argparse
import datetime
import json
import signal
import socket
import ssl
import time

import RPi.GPIO as GPIO
import paho.mqtt.client as MQTT

from hx711 import HX711

#
# This scripts starts the service. The service reads the values (weight, allocated) from the hardware.
# The values are send backend via mqtt.
# For transmission a mqtt broker is used. The format of the data is json:
# {"timestamp":"2012-04-23T18:25:43.511Z", "weight":[double], "allocated":[boolean]}
#
# The mqtt topic "me/wirries/coffeesensor" is used.
#
# The circuit diagram can be found in the folder documentation.
#
#
# require modules
#  - paho-mqtt (pip)
#  - numpy (pip)
#  - python-dev + python-rpi.gpio (apt)
#    or for development fakeRPiGPIO (pip)
#

# Defaults

MQTT_HOST = "localhost"
MQTT_PORT = 1883
MQTT_TOPIC = "me/wirries/coffeesensor"
MQTT_KEEPALIVE_INTERVAL = 45
INTERVAL = 15

# Using GPIO numbers
ALLOCATED_SENSOR = 5  # GPIO 05 / PIN 29
WEIGHT_SENSOR_DT = 24  # GPIO 24 / PIN 18
WEIGHT_SENSOR_SCK = 23  # GPIO 23 / PIN 16


# Classes

class GracefulKiller:
    kill_now = False

    def __init__(self):
        signal.signal(signal.SIGINT, self.exit_gracefully)
        signal.signal(signal.SIGTERM, self.exit_gracefully)

    def exit_gracefully(self, signum, frame):
        self.kill_now = True


# Functions

# Define on_publish event function
def on_publish(client, userdata, mid):
    print "Message published"


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

print "CoffeeSensor started - for stopping please press CRTL-c"
print " - MQTT-Broker:", MQTT_HOST
print " - MQTT-Port:", MQTT_PORT
print " - MQTT-Keepalive:", MQTT_KEEPALIVE_INTERVAL
print " - MQTT-Topic:", MQTT_TOPIC

loginEnabled = is_not_blank(user) and is_not_blank(password)
if loginEnabled:
    print " - use login for connection with user:", user

sslEnabled = is_not_blank(ca)
if sslEnabled:
    print " - use ca file for SSL connection:", ca

# Initiate MQTT Client
mqttc = MQTT.Client(client_id="CoffeeSensor_" + socket.gethostname())
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

# Setup GPIO layout
GPIO.setmode(GPIO.BCM)
GPIO.setup(ALLOCATED_SENSOR, GPIO.IN)  # as input

# Setup weight sensor
hx = HX711(WEIGHT_SENSOR_DT, WEIGHT_SENSOR_SCK)
hx.set_reading_format("LSB", "MSB")

# Reference value for the weight sensor.
hx.set_reference_unit(-392)
# How to find this value
# First you need a comparison object whose weight you know. E.g. a water bottle can be used well.
# The weight should have an average value of the maximum of the load cell.
# First you have to comment out the line with the reference value. Then start the program and follow
# the outputs. Place the object. The displayed values can be positive or negative.
# In this example, values around -402000 were displayed at 1kg (=1000g).
# So this reference value is: -402000 / 1000 = -402.

hx.reset()
hx.tare()
print "Weight sensor ready"

# Running server
try:
    killer = GracefulKiller()

    while True:
        # Reading values from sensors
        allocated = GPIO.input(ALLOCATED_SENSOR)
        weight = hx.get_weight(5)
        hx.power_down()
        hx.power_up()

        # Output for debugging
        print "Allocated is ", allocated  # High is free
        print "Weight is ", weight

        if weight < 0:
            weight = 0  # ignore negative values

        timestamp = str(datetime.datetime.now().isoformat())
        msg = json.dumps({"timestamp": timestamp, "weight": weight, "allocated": allocated == 0})
        print "MQTT message: ", msg

        # Publish message to MQTT Broker
        mqttc.publish(topic, msg)

        if killer.kill_now:
            break

        # wait for next update
        time.sleep(INTERVAL)

except KeyboardInterrupt:
    print "Strg-C called"

finally:
    print "CoffeeSensor stopped"
    # Release GPIO ports
    GPIO.cleanup()
    # Disconnect from MQTT_Broker
    mqttc.disconnect()
