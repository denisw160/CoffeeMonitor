## CoffeeService

This module provides the backend for the system. The Spring Boot application handles the incoming mqtt messages from the
sensor and stores the data in a MongoDB. With rest services the data can be query from the clients. 
This module is written in Java 1.8. For Spring Boot the version 2.1.0.RELEASE is used.

To install the required components, simply run the setup script. The installation requires SUDO rights.

    bash>setup.sh

To install the service, simply run the install script. The installation requires SUDO rights.

    bash>install.sh

To uninstall the service, simply run the uninstall script. The installation requires SUDO rights.

    bash>uninstall.sh

The start script can be used to start the server on the local console.

    bash>start.sh

The run script can be used to build and start the server on the local console.

    bash>run.sh

For clean and building the service the following scripts can be used.

    bash>clean.sh
    bash>build.sh
