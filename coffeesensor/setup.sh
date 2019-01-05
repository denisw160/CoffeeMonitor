#!/bin/bash
#
# This scripts installs necessary components
#

echo Setup components for CoffeeSensor ...

sudo apt update
sudo apt upgrade
sudo apt autoremove

sudo apt-get install -y python
sudo apt-get install -y python-pip

sudo apt-get install -y python-dev
sudo apt-get install -y python-rpi.gpio

sudo apt-get install -y python-numpy

sudo pip install paho-mqtt
sudo pip install numpy
