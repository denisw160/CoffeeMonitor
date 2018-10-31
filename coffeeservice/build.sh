#!/usr/bin/env bash
#
# Building the service with maven
#

echo Building CoffeeService ...
./mvnw.sh -DskipTests clean package
