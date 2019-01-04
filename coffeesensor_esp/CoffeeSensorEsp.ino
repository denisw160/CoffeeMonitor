// External libraries
#include <FS.h> // this needs to be first, or it all crashes and burns...
#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <WiFiManager.h>
#include <ESP8266WebServer.h>
#include <HX711_ADC.h>
#include <MqttClient.h>
#include <ArduinoJson.h>

// Interval for update
#define INTERVAL 2000

// Show debug messages in serial
#define DEBUG true

// Port on NodeMCU (D1) for the allocation sensor
#define ALLOCATION_SENSOR D1

// Ports on NodeMCU for the weight sensor - SCK (D2) + DT (D3)
#define WEIGHT_SCK D2
#define WEIGHT_DT D3


// Control Parameter
// define your default values here, if there are different values in config.json, they are overwritten.
char mqttServer[50];
char mqttPort[6] = "1883";
char mqttTopic[50] = "me/wirries/coffeesensor";


// HX711 - constructor (dout pin, sck pin):
HX711_ADC LoadCell(WEIGHT_DT, WEIGHT_SCK);

// MqttClient
MqttClient *mqtt = NULL;
WiFiClient network;
String mqttId;
int mPort;

// Heartbeat status
boolean led = false;

// Time mills for update
long t;

// Flag for saving data
bool shouldSaveConfig = false;


//
// MqttSystem implementation
class MqttSystem: public MqttClient::System {
  public:

    unsigned long millis() const {
      return ::millis();
    }

    void yield(void) {
      ::yield();
    }
};


//
// Initialize and setup
void setup() {
  // Initialize Debug-Console
  Serial.begin(115200);
  Serial.println("Booting ...");


  // Initialize the pins as in-/output
  Serial.println("Initialize pins ...");
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(ALLOCATION_SENSOR, INPUT);


  // Handle configuration
  // Clean filesystem - remove old config
  //SPIFFS.format();

  // Read configuration from FS json
  Serial.println("Mounting file system ...");
  if (SPIFFS.begin()) {
    if (SPIFFS.exists("/config.json")) {
      // File exists, reading and loading
      Serial.println("Loading config from file ...");
      File configFile = SPIFFS.open("/config.json", "r");
      if (configFile) {
        size_t size = configFile.size();
        std::unique_ptr<char[]> buf(new char[size]);
        configFile.readBytes(buf.get(), size);
        DynamicJsonBuffer jsonBuffer;
        JsonObject& json = jsonBuffer.parseObject(buf.get());

        if (DEBUG) json.printTo(Serial);
        if (json.success()) {
          if (DEBUG) Serial.println("\nConfig parsed");

          strcpy(mqttServer, json["mqtt_server"]);
          strcpy(mqttPort, json["mqtt_port"]);
          strcpy(mqttTopic, json["mqtt_topic"]);

          Serial.println("Config loaded");
        } else {
          Serial.println("Failed to load config");
        }
        configFile.close();
      }
    }
  } else {
    Serial.println("Failed to mount file system");
  }

  // The extra parameters to be configured (can be either global or just in the setup)
  // After connecting, parameter.getValue() will get you the configured value
  // id/name placeholder/prompt default length
  WiFiManagerParameter customMqttServer("server", "mqtt server", mqttServer, 50);
  WiFiManagerParameter customMqttPort("port", "mqtt port", mqttPort, 6);
  WiFiManagerParameter customMqttTopic("topic", "mqtt topic", mqttTopic, 50);


  // WiFiManager - local intialization.
  WiFiManager wifiManager;
  // reset saved settings - for deleting the settings
  //wifiManager.resetSettings();

  // set config save notify callback
  wifiManager.setSaveConfigCallback(saveConfigCallback);

  // add all parameters
  wifiManager.addParameter(&customMqttServer);
  wifiManager.addParameter(&customMqttPort);
  wifiManager.addParameter(&customMqttTopic);

  // fetches ssid and pass from eeprom and tries to connect
  // if it does not connect it starts an access point
  // and goes into a blocking loop awaiting configuration
  wifiManager.autoConnect("AutoConnectAP");

  Serial.print("Connecting to WiFi .");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected!");
  Serial.println("Hostname: " + WiFi.hostname());
  Serial.println("IP: " + WiFi.localIP().toString());

  // Setup hostname as MQTT_ID
  mqttId = WiFi.hostname();

  // Setup / update parameter
  strcpy(mqttServer, customMqttServer.getValue());
  strcpy(mqttPort, customMqttPort.getValue());
  strcpy(mqttTopic, customMqttTopic.getValue());

  // Save the custom parameters to file system
  if (shouldSaveConfig) {
    Serial.println("Saving config ...");
    DynamicJsonBuffer jsonBuffer;
    JsonObject& json = jsonBuffer.createObject();
    json["mqtt_server"] = mqttServer;
    json["mqtt_port"] = mqttPort;
    json["mqtt_topic"] = mqttTopic;

    File configFile = SPIFFS.open("/config.json", "w");
    if (!configFile) {
      Serial.println("Failed to open config file for writing");
    }

    json.printTo(Serial);
    json.printTo(configFile);
    configFile.close();
    Serial.println("Config saved");
  }


  // Initialize MqttClient
  Serial.println("Initialize MQTT client ...");
  MqttClient::System *mqttSystem = new MqttSystem();
  MqttClient::Logger *mqttLogger = new MqttClient::LoggerImpl<HardwareSerial>(Serial);
  MqttClient::Network *mqttNetwork = new MqttClient::NetworkClientImpl<WiFiClient>(network, *mqttSystem);
  MqttClient::Buffer *mqttSendBuffer = new MqttClient::ArrayBuffer<128>();
  MqttClient::Buffer *mqttRecvBuffer = new MqttClient::ArrayBuffer<128>();
  MqttClient::MessageHandlers *mqttMessageHandlers = new MqttClient::MessageHandlersImpl<2>();
  MqttClient::Options mqttOptions;
  mqttOptions.commandTimeoutMs = 10000;
  mqtt = new MqttClient(mqttOptions, *mqttLogger, *mqttSystem, *mqttNetwork, *mqttSendBuffer, *mqttRecvBuffer, *mqttMessageHandlers);
  Serial.println("MQTT client is ready");


  // Initialize weight sensor
  Serial.println("Initialize weight sensor ...");

  float calValue = 1;
  // calibration value
  // How to find this value
  // First you need a comparison object whose weight you know. E.g. a water bottle can be used well.
  // The weight should have an average value of the maximum of the load cell.
  // First you have to comment out the line with the reference value. Then start the program and follow
  // the outputs. Place the object. The displayed values can be positive or negative.
  // In this example, values around -402000 were displayed at 1kg (=1000g).
  // So this reference value is: -402000 / 1000 = -402.
  calValue = 385.92;

  LoadCell.begin();
  long stabilisingtime = 2000; // tare preciscion can be improved by adding a few seconds of stabilising time
  LoadCell.start(stabilisingtime);

  if (LoadCell.getTareTimeoutFlag()) {
    Serial.println("Tare timeout, check MCU>HX711 wiring and pin designations");
  } else {
    LoadCell.setCalFactor(calValue); // set calibration value (float)
    Serial.println("Weight sensor is ready");
  }


  // Setup port
  mPort = String(mqttPort).toInt();

  Serial.println("Setup completed.");
}

//
// Run system loop
void loop() {
  // update() should be called at least as often as HX711 sample rate; >10Hz@10SPS, >80Hz@80SPS
  // use of delay in sketch will reduce effective sample rate (be carefull with use of delay() in the loop)
  LoadCell.update();

  // Update and send data in interval
  if (millis() > t + INTERVAL) {
    boolean allocation = readAllocation();
    float weight = readWeight();
    sendMqttMessage(allocation, weight);

    heartbeat();
    t = millis();
  }
}

//
// Read the allocation from the sensor and
// return the value as boolean.
// - TRUE=allocated
// - FALSE=free
boolean readAllocation() {
  boolean allocation = digitalRead(ALLOCATION_SENSOR) == 0;

  // Debug message
  if (DEBUG) {
    Serial.print("Allocation is: ");
    if (allocation) {
      Serial.println("allocated");
    } else {
      Serial.println("free");
    }
  }

  return allocation;
}

//
// Read the weight from the sensor and
// return the value.
float readWeight() {
  float weight = LoadCell.getData();

  // Debug message
  if (DEBUG) {
    Serial.print("Weight is: ");
    Serial.println(weight);
  }

  return weight;
}

//
// Convert the sensor values to a JSON message
// and send the message to the MQTT broker.
void sendMqttMessage(boolean allocation, float weight) {
  // Check connection status
  if (!mqtt->isConnected()) {
    network.stop(); // Close connection if exists

    Serial.println("Reconnecting to MQTT server ...");
    network.connect(mqttServer, mPort); // Re-establish TCP connection with MQTT broker
    if (!network.connected()) {
      Serial.println("Can't establish the TCP connection");
    }

    // Start new MQTT connection
    MqttClient::ConnectResult connectResult;
    {
      // Connect
      MQTTPacket_connectData options = MQTTPacket_connectData_initializer;
      options.MQTTVersion = 4;
      options.clientID.cstring = (char*)mqttId.c_str();
      options.cleansession = true;
      options.keepAliveInterval = 15; // 15 seconds
      MqttClient::Error::type rc = mqtt->connect(options, connectResult);
      if (rc != MqttClient::Error::SUCCESS) {
        Serial.print("Connection error: ");
        Serial.println(rc);
        return;
      }
    }
  } else {
    {
      // Sending MQTT message
      // Original structure: {"timestamp": "2019-01-04T10:54:49.517000", "allocated": true, "weight": 1.0406128215270019}
      // ESP8266 short structure : {"allocated": true, "weight": 1.0406128215270019}
      // timestamp will added by the service
      const String a = allocation ? "true" : "false";
      const String w = String(weight);
      const String m = "{\"allocated\": " + a + ", \"weight\": " + w + "}";
      const char* buf = (char*)m.c_str();
      MqttClient::Message message;
      message.qos = MqttClient::QOS0;
      message.retained = false;
      message.dup = false;
      message.payload = (void*) buf;
      message.payloadLen = strlen(buf);
      mqtt->publish(mqttTopic, message);

      if (DEBUG) {
        Serial.print("MQTT message send: ");
        Serial.println(buf);
      }
    }
  }
}

//
// Toogle the LED and write an message to serial.
void heartbeat() {
  if (led) {
    digitalWrite(LED_BUILTIN, LOW);
    if (!DEBUG) Serial.print("*");
    led = false;
  } else {
    digitalWrite(LED_BUILTIN, HIGH);
    if (!DEBUG) Serial.print("-");
    led = true;
  }
}

// callback notifying us of the need to save config
void saveConfigCallback () {
  if (DEBUG) Serial.println("Should save config");
  shouldSaveConfig = true;
}

