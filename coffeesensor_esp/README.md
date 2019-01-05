## CoffeeSensor for ESP8266

### Description
This module can be installed on a ESP8266 / NodeMCU. It starts the monitoring of the sensors (GPIO) and transmits the values to the MQTT broker. 
This module is written in the Arduino IDE.

The data is send every 15 seconds. If you want another update interval, please change the variable INTERVAL.

The connection of the hardware (sensors) can be found [here][1].
![Plug-in board for ESP8266][image-1]

You need the following libraries in your Arduino IDE.

- HX711\_ADC by Olav Kallhovd ([https://github.com/olkal/HX711\_ADC][2])
- ArduinoMqtt by Oleg Kovalenko ([https://github.com/monstrenyatko/ArduinoMqtt][3])
- WiFiManager by tzapu ([https://github.com/tzapu/WiFiManager][4])

### Install
For install the software, open the file CoffeeSensorEsp.ino in your Arduino IDE and flash the ESP8266.

### Setup
On the first start of the NodeMCU it try to connect to a WiFi. If no WiFi parameter set, the NodeMCU stops and create and open WiFi network (AutoConnectAP). 
Please connect to this network and update your WiFi credentials and MQTT server. Then the NodeMCU restarts and automatically connect to your WiFi network.

You can canfigure the following MQTT parameter:

- MQTT Server: IP or hostname of your MQTT server
- MQTT Port: Your port of your server (default: 1883 or SSL: 8883)
- MQTT Topic: The topic for the sensor data, you must have write access for this topic
- MQTT User: Your username for the the server
- MQTT Password: The password for the user
- MQTT SSL: 1 for use SSL for the connection, 0 disable SSL

The MQTT ID of the client is the hostname of the ESP8266.
 

### Debug
In the serial monitor you can see the messages of the module.

[1]:	../documentation/CoffeeSensorEsp.fzz
[2]:	https://github.com/olkal/HX711_ADC "GitHub for HX711"
[3]:    https://github.com/monstrenyatko/ArduinoMqtt "GitHub for ArdunioMqtt"
[4]:    https://github.com/tzapu/WiFiManager "GitHub for WiFiManager"

[image-1]:	../documentation/CoffeeSensorEsp_Plug-in_board.png "Plug-in board for ESP8266"