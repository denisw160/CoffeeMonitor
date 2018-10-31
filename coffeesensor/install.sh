#!/bin/bash
#
# This scripts install the systemd service.
#

APP_DIR=$(pwd -P)

sudo cp src/coffeesensor.service  /etc/systemd/system/coffeesensor.service
sudo sed -i -e "s~##APP_DIR##~$APP_DIR~g" /etc/systemd/system/coffeesensor.service
sudo systemctl enable coffeesensor.service
sudo systemctl start coffeesensor.service
