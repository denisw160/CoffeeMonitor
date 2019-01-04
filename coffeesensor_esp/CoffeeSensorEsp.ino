// External libraries
#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <WiFiManager.h>
#include <ESP8266WebServer.h>
#include <HX711_ADC.h>
#include <MqttClient.h>

// Interval for update
#define INTERVAL 2000

// Show debug messages in serial
#define DEBUG true

// Port on NodeMCU (D1) for the allocation sensor
#define ALLOCATION_SENSOR D1

// Ports on NodeMCU for the weight sensor - SCK (D2) + DT (D3)
#define WEIGHT_SCK D2
#define WEIGHT_DT D3

// Control Parameter - TODO replace with WiFiManager
#define MQTTSERVER "192.168.0.104"
#define MQTTPORT 1883
#define MQTTTOPIC "me/wirries/coffeesensor"


// HX711 - constructor (dout pin, sck pin):
HX711_ADC LoadCell(WEIGHT_DT, WEIGHT_SCK);

// MqttClient
MqttClient *mqtt = NULL;
WiFiClient network;
String mqttId;

// Heartbeat status
boolean led = false;

// Time mills for update
long t;




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

  // WiFiManager - local intialization.
  WiFiManager wifiManager;
  // reset saved settings - for deleting the settings
  //wifiManager.resetSettings();

  // fetches ssid and pass from eeprom and tries to connect
  // if it does not connect it starts an access point
  // and goes into a blocking loop awaiting configuration
  wifiManager.autoConnect("AutoConnectAP");

  Serial.print("Connecting to WiFi ");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected!");
  Serial.println("Hostname: " + WiFi.hostname());
  Serial.println("IP: " + WiFi.localIP().toString());

  // Setup hostname as MQTT_ID
  mqttId = WiFi.hostname();

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
    network.connect(MQTTSERVER, MQTTPORT); // Re-establish TCP connection with MQTT broker
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
      mqtt->publish(MQTTTOPIC, message);

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

