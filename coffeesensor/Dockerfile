FROM python:2.7-slim

# Install dependencies
RUN pip install paho-mqtt

# Copy the python-scrips
WORKDIR /
COPY src/*.py ./
COPY test/*.py ./

# Parameter for the execution
ENV mqttServer=mqtt
ENV mqttPort=1883
ENV mqttUser=
ENV mqttPassword=
ENV sslCa=

# Copy run script
COPY docker_entrypoint.sh /entrypoint.sh

# Run service
ENTRYPOINT [ "/entrypoint.sh" ]
