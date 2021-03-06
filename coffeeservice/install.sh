#!/bin/bash
#
# This scripts install the systemd service.
#

APP_DIR=$(pwd -P)
PROCESS_USR=$(whoami)

echo Installing CoffeeService ...
sudo cp src/service/coffeeservice.service  /etc/systemd/system/coffeeservice.service
sudo sed -i -e "s~##APP_DIR##~$APP_DIR~g" /etc/systemd/system/coffeeservice.service
sudo sed -i -e "s~##PROCESS_USR##~$PROCESS_USR~g" /etc/systemd/system/coffeeservice.service
sudo systemctl enable coffeeservice.service
sudo systemctl start coffeeservice.service
