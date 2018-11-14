# CoffeeMonitor
**Retrofit the coffee**

_State: Beta_

This project is a showcase for an IoT application that digitizes our "old" / conventional coffee machine. With the help of basic sensors this project shall show how existing "hardware" can be transformed into the modern digital world.

A presentation about the CoffeeMonitor can be found [here][1] (under construction).

## Why this scenario?

The classic coffee pot is widely used and everyone does not know the problem: if you want to have a coffee, the pot is empty. How good it would be if you knew in advance how much coffee was still available before you went on your way.

## The idea

In order to determine how full the coffee pot is, there are different variants to determine this. A simple variant is to weigh the coffee pot. There are already a number of sensors that can be connected directly.
If you know the empty and full weight, you can easily estimate the degree of filling of the coffee pot.
In addition, I wanted to know how often the coffee pot was refilled. Since the coffee pot has to be removed from the weighing plate, I used a distance sensor that monitors the occupancy of the plate. With the sensor one can count the change of the allocation and/or refilling so. 
Now the data from the sensors must be processed and made available to the user. For this I use a Raspberry Pi 3, which connects the necessary software and hardware components. The sensors are connected to the GPIO ports. The status of the coffee pot is then published via a web application. 

## The solution

All sensors are connected to the GPIO ports. This looks like this, for example. 

![Raspberry complete][image-1]

A weight cell with a HX711 is used as weighing sensor. In order for the IR distance sensor (FC-51) to work properly, the plate must be provided with two holes for weighing.

![Sensors of the CoffeeMonitor][image-2]

The structure and connection to the Raspberry Pi is as follows

![Plug-in board][image-3]

The details of the electrical connection can be found in the [Fritzing file][2].

## The architecture

The software consists of three modules:
- [CoffeeSensor][3]: a Python script that reads the data from the sensors and transmits it to an MQTT broker.
- [CoffeeService][4]: a Spring Boot application (Java) that receives messages from the MQTT Broker and stores them in a MongoDB. The application also provides a REST API to retrieve the data from the MongoDB.
- [CoffeeApp][5]: An angular application that retrieves and displays the data via the REST service. 

### The prerequisites

The following software components are required on the Raspberry:
- Mosquito MQTT-Broker
- MongoDB from 2.6
- Python 2.x and PIP
- Java 1.8
- Node.js

The installation can be carried out using the setup scripts.

## Features of the application

Display how full the coffee pot is

**Full**
![][image-4]

**Some coffee taken**
![][image-5]

**Almost empty**
![][image-6]

**Empty**
![][image-7]

If the application detects that no more data is being transferred, this is displayed.
![][image-8]

The application can be used both on the desktop and on mobile devices.
![][image-9 | width=100]

The configuration of the weights (filling and empty weight) can be set directly in the web application. 
![][image-10]

The progression of the filling level as well as the refilling can be tracked in the history.
![][image-11]

[1]:	documentation/CoffeeMonitor.pdf "Presentation of the CoffeeMonitor"
[2]:	documentation/CoffeeSensor.fzz
[3]:	coffeesensor
[4]:	coffeeservice
[5]:	coffeeapp

[image-1]:	documentation/CoffeeSensor_Showcase1.jpg "Showcase 1"
[image-2]:	documentation/CoffeeSensor_Showcase2.jpg "Sensors"
[image-3]:	documentation/CoffeeSensor_Plug-in_board.png "Plug-in board"
[image-4]:	documentation/CoffeeMonitor_1_Full.png
[image-5]:	documentation/CoffeeMonitor_2_Reduced.png
[image-6]:	documentation/CoffeeMonitor_3_Almost.png
[image-7]:	documentation/CoffeeMonitor_4_Empty.png
[image-8]:	documentation/CoffeeMonitor_0_NoData.png
[image-9]:	documentation/CoffeeMonitor_iPhone.png
[image-10]:	documentation/CoffeeMonitor_Config.png
[image-11]:	documentation/CoffeeMonitor_History1.png
