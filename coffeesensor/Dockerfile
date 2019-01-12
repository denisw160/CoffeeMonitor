FROM python:2.7-slim

# Install dependencies
RUN pip install paho-mqtt

# Copy the python-scrips
WORKDIR /
COPY src/*.py ./
COPY test/*.py ./

# Parameter for the execution
ENV MQTT_SERVER=mqtt
ENV MQTT_PORT=1883
ENV MQTT_USER=
ENV MQTT_PASSWORD=
ENV SSL_CA=

# Copy run script
COPY docker_entrypoint.sh /entrypoint.sh

# Run service
ENTRYPOINT [ "/entrypoint.sh" ]
