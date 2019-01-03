## CoffeeSensor for ESP8266

### Description
This module can be installed on a ESP8266 / NodeMCU. It starts the monitoring of the sensors (GPIO) and transmits the values to the MQTT broker. 
This module is written in the Arduino IDE.

The data is send every two seconds. If you want another update interval, please change the variable INTERVAL.

The connection of the hardware (sensors) can be found here (TODO LINK).

You need the following libraries in your Arduino IDE.

- HX711_ADC by Olav Kallhovd (https://github.com/olkal/HX711_ADC)
- (TODO add libraries)

### Install
For install the software, open the file CoffeeSensorEsp.ino in your Arduino IDE and flash the ESP8266.

### Setup
On the first start of the NodeMCU it try to connect to a WiFi. If no WiFi parameter set, the NodeMCU stops and create and open WiFi network (AutoConnectAP). 
Please connect to this network and update your WiFi credentials and MQTT server. Then the NodeMCU restarts and automaticaly connect to your WiFi network.

### Debug
In the serial monitor you can see the messages of the module.
