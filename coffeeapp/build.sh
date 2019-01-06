#!/usr/bin/env bash
#
# Building the service with npm and ng
#
# Usage: ./build.sh [/yourPath/]
#  - setup the basehref to /yourPath/ in your Angular app
#

BASEHREF=/

if [ -n "$1" ]; then
    BASEHREF=$1
fi

echo Building CoffeeApp ...
echo "Set base-href to ${BASEHREF}"
npm install
ng build --configuration=production --base-href ${BASEHREF}

# beep when done
echo -en "\007"
