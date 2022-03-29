/**
   PostHTTPClient.ino

    Created on: 21.11.2016

*/

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

/* this can be run with an emulated server on host:
        cd esp8266-core-root-dir
        cd tests/host
        make ../../libraries/ESP8266WebServer/examples/PostServer/PostServer
        bin/PostServer/PostServer
   then put your PC's IP address in SERVER_IP below, port 9080 (instead of default 80):
*/
//#define SERVER_IP "10.0.1.7:9080" // PC address with emulation on host
#define SERVER_IP "192.168.43.96:5000"

#ifndef STASSID
#define STASSID "lasec_security"
#define STAPSK  "lasec@123"
#endif

bool device_status = false;
#define LASER_PIN 0
#define INPUT_LASER 2

void setup() {

  Serial.begin(115200);
  pinMode(LASER_PIN, OUTPUT);
  pinMode(INPUT_LASER, INPUT);

  Serial.println();
  Serial.println();
  Serial.println();

  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected! IP address: ");
  Serial.println(WiFi.localIP());

}

void loop() {
  // wait for WiFi connection
  if ((WiFi.status() == WL_CONNECTED)) {

    if (device_status) {
      digitalWrite(LASER_PIN, HIGH);

      //checkif device is off
      if (getserver("http://" SERVER_IP "/device_status") == "0")
        device_status = false;
      
      //register an intruder if input is HIGH
      if (digitalRead(INPUT_LASER) == HIGH)
        getserver("http://" SERVER_IP "/register_intruder");


    } else {
      digitalWrite(LASER_PIN, LOW);

       if (getserver("http://" SERVER_IP "/device_status") == "1")
        device_status = true;

    }

    delay(5000);
  }
}

String getserver(String url) {
  WiFiClient client;
  HTTPClient http;

  Serial.print("[HTTP] begin...\n");
  // configure traged server and url
  http.begin(client, url); //HTTP
  http.addHeader("Content-Type", "application/json");

  Serial.print("[HTTP] GET...\n");
  // start connection and send HTTP header and body
  int httpCode = http.GET();

  // httpCode will be negative on error
  if (httpCode > 0) {
    // HTTP header has been send and Server response header has been handled
    Serial.printf("[HTTP] POST... code: %d\n", httpCode);

    // file found at server
    if (httpCode == HTTP_CODE_OK) {
      const String& payload = http.getString();
      Serial.println("received payload:\n<<");
      Serial.println(payload);
      Serial.println(">>");
      
      http.end();
      return payload;
    }
  } else {
    Serial.printf("[HTTP] POST... failed, error: %s\n", http.errorToString(httpCode).c_str());
    http.end();
    
    return "";
  }

}
