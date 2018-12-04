/*
 *  This sketch sends data via HTTP GET requests to data.sparkfun.com service.
 *
 *  You need to get streamId and privateKey at data.sparkfun.com and paste them
 *  below. Or just customize this script to talk to other HTTP servers.
 *
 */

#include <ESP8266WiFi.h>

//port mappings
#define LED 4
#define  D0 16
#define  D1 5
#define  D2 4
#define  D3 0
#define  D4 2
#define  D5 14
#define  D6 12
#define  D7 13
#define  D8 15
#define  D9 3
#define  D10 1


const char* ssid     = "Irindu";
const char* password = "87654321";

const char* hosti = " http://192.168.43.48";
const char* streamId   = "....................";
const char* privateKey = "pot1";

IPAddress host(192,168,43,48);

int soilMoisture;
int lightIntensity;
int temperature;
int RequestNumber;


void setup() {
  Serial.begin(115200);
  delay(10);

  // We start by connecting to a WiFi network
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(D0, OUTPUT);
  pinMode(D1, OUTPUT);
  pinMode(D2, OUTPUT);
  pinMode(D3, OUTPUT);
  
  Serial.println();
  Serial.println();
  ConnectToWifi(ssid, password);
//  attachInterrupt(digitalPinToInterrupt(pin), blink, CHANGE); //

/*

 LOW to trigger the interrupt whenever the pin is low, 
CHANGE to trigger the interrupt whenever the pin changes value 
RISING to trigger when the pin goes from low to high, 
FALLING for when the pin goes from high to low. 

The Due board allows also: 

HIGH to trigger the interrupt whenever the pin is high. 
 */
  

}
 
int value = 0;

void ConnectToWifi(const char* ssid,const char* password){
   Serial.print("Connecting to ");
   Serial.println(ssid);
   WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  byte mac[6];
   WiFi.macAddress(mac);
  
}

  
void ReadData(){

  digitalWrite(D0, HIGH);
  digitalWrite(D1, LOW);
  digitalWrite(D2, LOW);
  delay(100);
 soilMoisture = analogRead(A0);
 

Serial.print("soilMoisture Read:");
 Serial.println(soilMoisture);
 
  digitalWrite(D0, LOW);
  digitalWrite(D1, LOW);
  digitalWrite(D1, HIGH);
  delay(100);
 lightIntensity = analogRead(A0);
 
 
 Serial.print("lightIntensity Read:");
  Serial.println(lightIntensity);
 
  digitalWrite(D0, LOW);
  digitalWrite(D1, HIGH);
  digitalWrite(D2, LOW);
  delay(10000);
 temperature = analogRead(A0);
   
 Serial.print("Temperature Read:");
 Serial.println(temperature);
 digitalWrite(D1, LOW);

 Serial.println("");
 Serial.println("");
  }

int SendDataAndReadResposne(WiFiClient client){
  Serial.print("connecting to ");
  Serial.println(host);
  
  // Use WiFiClient class to create TCP connections
  //WiFiClient client;
  const int httpPort = 8080;
  if (!client.connect(host, httpPort)) {
    Serial.println("connection failed");
    return -1;
  }

  Serial.println("");
  Serial.println("connection successed");
  Serial.println("");
  //http://localhost:8080/TalkingPot
  // We now create a URI for the request
  String url = "GET /TalkingPot/Servlet?&";
  url += "privateKey=";
  url += privateKey;
  url += "&temperature=";
  url += temperature;
  url += "&soilMoisture=";
  url += soilMoisture;
  url += "&lightIntensity=";
  url += lightIntensity;

  Serial.print("Requesting URL: ");
  Serial.println(url);
  
  // This will send the request to the server
  //  client.print(String("GET ") + url + " HTTP/1.1\r\n" +
  //"Host: 127.0.0.1
/*  client.print(String("GET /project/test.php") + " HTTP/1.1\r\n" +
               "Host: 127.0.0.1" + "\r\n" + 
               "Connection: close\r\n\r\n"); */

  String urli = "GET /TalkingPot/Servlet?&&moisture="+ (String)soilMoisture ;
  client.println(url);
  client.println("Host:"+host);
  client.println("Accept */*");
  client.println("Connection: close");
  client.println();
  delay(500);

 // Read all the lines of the reply from server and print them to Serial
 String line;
   while(client.available()){
     line = client.readStringUntil('\n');
    Serial.print(line);
  }

  Serial.println();
  Serial.println("closing connection");
  client.stop();
  Serial.println("Testing...");
  Serial.println(line.toInt());
  delay(10);
  return (line.toInt());
  }

void HandleResposne(int response){
   Serial.println("Response");
    Serial.println(response);
 switch (response) {
    case 0:    // your hand is on the sensor
      Serial.println("OK");
      
      break;
    case 1:    // your hand is close to the sensor
      Serial.println("I need water...");
      requestWater();
      break;
    case 2:    // your hand is a few inches from the sensor
      Serial.println("I need Sun Light...");
      requestSunLight();
      break;
    case 3:    // your hand is nowhere near the sensor
      Serial.println("The Temerature is too much");
      notifyHighTemerature();
      break;
    case 4:    // your hand is nowhere near the sensor
      Serial.println("The Temerature is not enough");
      notifyLowTemerature();
    break;
     Default:    // your hand is nowhere near the sensor
      Serial.println("Error!");  
      break;
  } 
}

  




void loop() {
 
 //ESP.deepSleep(60U*60*1000000); //Sleep
 delay(1000);

  digitalWrite(LED_BUILTIN,LOW );
  ReadData();
delay(1000);
 digitalWrite(LED_BUILTIN, HIGH);
 delay(10000); 
  WiFiClient client;
  int resposnse;
  resposnse = SendDataAndReadResposne(client);
 HandleResposne(resposnse); //
}
/*
void blink() {
 //   state = !state;
} */
void requestWater(){
    digitalWrite(D3, HIGH);
    delay(1000);
    digitalWrite(D3, LOW);
  }
void requestSunLight(){
  digitalWrite(D3, HIGH);
  delay(1000);
  digitalWrite(D3, LOW);
  }
void notifyHighTemerature(){
    digitalWrite(D3, HIGH);
    delay(1000);
    digitalWrite(D3, LOW);
  }
void notifyLowTemerature(){
  digitalWrite(D3, HIGH);
  delay(1000);
  digitalWrite(D3, LOW);
  }
  
