// Port on NodeMCU (D1) for the allocation sensor
#define ALLOCATION_SENSOR D1

// Intervall for update
#define INTERVALL 2000

// Heartbeat status
boolean led = false;

void setup() {
  // Initialize Debug-Console
  Serial.begin(115200);
  Serial.println("Booting ...");

  // Initialize the pins as in-/output
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(ALLOCATION_SENSOR, INPUT);

  // TODO setup WiFi manager and variables

  Serial.println("Setup completed");
}

void loop() {
  updateData();
  heartbeat();
}

void updateData() {
  // TODO reading sensors
  int val = digitalRead(ALLOCATION_SENSOR);
  Serial.print("Allocation Sensor is: ");
  if (val == 0) {
    Serial.println("allocated");
  } else {
    Serial.println("free");
  }
  // TODO send data to server
}


void heartbeat() {
  if (led) {
    digitalWrite(LED_BUILTIN, LOW);
    //Serial.print("*");
    delay(INTERVALL);
    led = false;
  } else {
    digitalWrite(LED_BUILTIN, HIGH);
    delay(INTERVALL);
    //Serial.print("-");
    led = true;
  }
}
