#!/bin/bash
#
# This scripts installs necessary components
#

sudo apt update
sudo apt upgrade
sudo apt autoremove

sudo apt-get install -y oracle-java8-jdk
sudo apt-get install -y mosquitto mosquitto-clients

# On Rasbian 9 MongoDB is only available in version 2.4.x.
# This version is to old for Spring Boot with MongoDB.
#sudo apt-get install -y mongodb-server

# Using a precompiled version from https://facat.github.io/cross-compile-mongodb-for-arm.html
APP_DIR=$(pwd -P)

sudo useradd --system --user-group --home-dir /var/lib/mongodb --no-create-home --shell /bin/false mongodb

sudo mkdir -p /var/lib/mongodb
sudo chown -R mongodb:mongodb /var/lib/mongodb

sudo mkdir -p /var/log/mongodb
sudo chown -R mongodb:mongodb /var/log/mongodb

sudo apt-get install -y p7zip-full
if [ ! -d /opt/mongodb ]; then
  sudo 7z x -o/opt ../3rd-party/mongodb-2.6.4-arm.7z
  sudo chown -R  mongodb:mongodb /opt/mongodb
fi

sudo cp ../3rd-party/mongodb.conf /etc/mongodb.conf
sudo cp ../3rd-party/mongodb-server /etc/logrotate.d/mongodb-server
sudo cp ../3rd-party/mongodb.service  /etc/systemd/system/mongodb.service
sudo systemctl enable mongodb.service
sudo systemctl start mongodb.service
