#!/bin/bash
#
# This scripts remove the systemd service.
#

sudo systemctl stop coffeesensor.service
sudo systemctl disable coffeesensor.service
sudo rm /etc/systemd/system/coffeesensor.service
