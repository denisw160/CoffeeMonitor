#!/bin/bash
#
# This scripts remove the systemd service.
#

echo Uninstalling CoffeeSensor ...
sudo systemctl stop coffeesensor.service
sudo systemctl disable coffeesensor.service
sudo rm /etc/systemd/system/coffeesensor.service
