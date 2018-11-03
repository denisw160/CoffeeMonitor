#!/bin/bash
#
# This scripts installs necessary components
#

echo Setup components for CoffeeApp ...

sudo apt update
sudo apt upgrade
sudo apt autoremove

# Setup Node.js Repository for version 10
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -

sudo apt-get install -y gcc g++ make
sudo apt-get install -y nodejs

# Update global packages
sudo npm outdated -g --depth=0
sudo npm update -g

# Install angular/cli
sudo npm install -g @angular/cli

# Install NGINX
sudo apt-get install -y nginx
sudo systemctl enable nginx
sudo systemctl start nginx
