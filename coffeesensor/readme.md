## CoffeeSensor

This module starts the monitoring of the sensors (GPIO) and transmits the values to the MQTT broker. 
This module is written in Python 2.x. For the connection to the mqtt broker the python lib from paho.mqtt is used.

The connection of the hardware (sensors) can be found in the main folder under doc.

To install the required components, simply run the setup script. The installation requires SUDO rights.

    bash>setup.sh

To install the service, simply run the install script. The installation requires SUDO rights.

    bash>install.sh

To uninstall the service, simply run the uninstall script. The installation requires SUDO rights.

    bash>uninstall.sh

The start script can be used to start the server on the local console.

    bash>start.sh

The test script can be used for testing without hardware. It generates random values and transfers them.

    bash>test.sh
