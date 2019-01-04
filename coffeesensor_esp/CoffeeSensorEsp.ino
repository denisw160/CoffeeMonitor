// External libraries
#include <HX711_ADC.h>

// Interval for update
#define INTERVAL 2000

// Show debug messages in serial
#define DEBUG true

// Port on NodeMCU (D1) for the allocation sensor
#define ALLOCATION_SENSOR D1

// Ports on NodeMCU for the weight sensor - SCK (D2) + DT (D3)
#define WEIGHT_SCK D2
#define WEIGHT_DT D3

// HX711 constructor (dout pin, sck pin):
HX711_ADC LoadCell(WEIGHT_DT, WEIGHT_SCK);

// Heartbeat status
boolean led = false;

// Time mills for update
long t;

void setup() {
  // Initialize Debug-Console
  Serial.begin(115200);
  Serial.println("Booting ...");

  // Initialize the pins as in-/output
  Serial.println("Initialize pins ...");
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(ALLOCATION_SENSOR, INPUT);

  // TODO setup WiFi manager and variables


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

  // TODO send data to server

}

//
// Toogle the LED and write an message ro serial.
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

