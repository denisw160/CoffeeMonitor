### STAGE 1: Build the service
FROM maven:3-jdk-11 as builder

# Prepare the build (if no changes in pom.xml, the cache will used)
WORKDIR /java-app
COPY pom.xml pom.xml
RUN mvn dependency:resolve dependency:resolve-plugins

# Building the service
COPY . .
RUN mvn -DskipTests clean package

## STAGE 2: Setup the image
FROM openjdk:11-jdk-slim

# Copy the build
WORKDIR /
COPY --from=builder /java-app/target/coffeeservice.jar /

# Expose port
EXPOSE 8080

# Parameter for the execution
ENV dbHost=db
ENV dbPort=27017
ENV database=coffeeservice
ENV mqttUrl=tcp://mqtt:1883
ENV mqttUser=
ENV mqttPassword=
ENV mqttTruststore=
ENV mqttTruststorePassword=
ENV webUser=
ENV webPassword=
ENV logSessions=false

# Copy run script
COPY docker_entrypoint.sh /entrypoint.sh

# Run service
ENTRYPOINT [ "/entrypoint.sh" ]