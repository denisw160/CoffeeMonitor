### STAGE 1: Build the service
FROM maven:3-jdk-11 as builder

# Building the service
WORKDIR /java-app
COPY . .
RUN mvn -DskipTests clean package

## STAGE 2: Setup the image
FROM openjdk:11-jdk-slim

# Copy the build
WORKDIR /
COPY --from=builder /java-app/target/camera-service.jar /

# Expose port
EXPOSE 8080

# Run service
ENTRYPOINT [ "/usr/bin/java", "-jar", "camera-service.jar", "--spring.profiles.active=production" ]
# TODO Adding configuration parameter from CMD line
#--app.db.host=db
#--app.db.database=coffeeservice
#--app.db.port=27017
#--app.mqtt.url=ssl://xxx:8883
#--app.mqtt.user=xxxx
#--app.mqtt.password=xxxx
#--app.mqtt.trustStore=./../documentation/trustStore.jks
#--app.mqtt.trustStorePassword=password
#--app.web.user=xxx
#--app.web.password=xxx
#--app.web.logSessions=true
