#!/usr/bin/env bash
#
# Building the service with npm and ng
#

echo Building CoffeeApp ...
npm install
ng build --configuration=production

# beep when done
echo -en "\007"
