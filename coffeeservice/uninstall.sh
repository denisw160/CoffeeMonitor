#!/bin/bash
#
# This scripts remove the systemd service.
#

echo Uninstalling CoffeeService ...
sudo systemctl stop coffeeservice.service
sudo systemctl disable coffeeservice.service
sudo rm /etc/systemd/system/coffeeservice.service
