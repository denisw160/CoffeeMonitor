#!/usr/bin/env bash
#
# This scripts run the build and start a local server on port 4200.
#

echo Run CoffeeApp ...
ng serve --proxy-config proxy.conf.json --host 0.0.0.0 --disable-host-check
