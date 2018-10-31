#!/bin/bash
#
# This scripts installs necessary components
#

sudo apt update
sudo apt upgrade
sudo apt autoremove

sudo apt-get install -y mosquitto mosquitto-clients
sudo apt-get install -y mongodb-server
