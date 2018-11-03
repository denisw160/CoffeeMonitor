#!/bin/bash
#
# This scripts remove the Nginx site for CoffeeApp
# and enables the default site.
#

echo Uninstalling CoffeeApp ...
sudo rm /etc/nginx/sites-enabled/coffeeapp
sudo ln -s /etc/nginx/sites-available/default /etc/nginx/sites-enabled/default
sudo systemctl restart nginx
