#!/bin/bash
#
# This scripts install the Nginx site for CoffeeApp.
#

APP_DIR=$(pwd -P)

echo Installing CoffeeApp ...
sudo cp nginx.site.conf /etc/nginx/sites-available/coffeeapp
sudo sed -i -e "s~##APP_DIR##~$APP_DIR~g" /etc/nginx/sites-available/coffeeapp
sudo rm /etc/nginx/sites-enabled/default
sudo ln -s /etc/nginx/sites-available/coffeeapp /etc/nginx/sites-enabled/coffeeapp
sudo systemctl restart nginx
