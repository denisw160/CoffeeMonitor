import datetime
import json
import signal
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
#  - python-dev + python-rpi.gpio (apt)
#    or for development fakeRPiGPIO (pip)
#

# Defaults

MQTT_HOST = "localhost"
MQTT_PORT = 1883
MQTT_KEEPALIVE_INTERVAL = 45
MQTT_TOPIC = "me/wirries/coffeesensor"
INTERVAL = 25

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


# Starting server
print "CoffeeSensor started - for stopping please press CRTL-c"
print " - MQTT-Broker:", MQTT_HOST
print " - MQTT-Port:", MQTT_PORT
print " - MQTT-Keepalive:", MQTT_KEEPALIVE_INTERVAL
print " - MQTT-Topic:", MQTT_TOPIC

# Initiate MQTT Client
mqttc = MQTT.Client()
# Register publish callback function
mqttc.on_publish = on_publish
# Connect with MQTT Broker
mqttc.connect(MQTT_HOST, MQTT_PORT, MQTT_KEEPALIVE_INTERVAL)

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
        mqttc.publish(MQTT_TOPIC, msg)

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
