#include <ESP8266WiFi.h>

int count=0;
const char* ssid = "Irindu";
const char* password = "87654321";

// We will take analog input from A0 pin 
const int AnalogIn = A0; 

// Client's IP Address
IPAddress host(192,168,43,48);

void setup() {
  Serial.begin(115200);
  delay(10);

  // We start by connecting to a WiFi network

  //Serial.println();
  Serial.println("");
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
}
void loop() {

  count++;

// Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort = 80;
  if (!client.connect(host, httpPort)) {
    Serial.println("connection failed");
    return;
  }
  
//sensor variable is set with a function to check. Let me know if you need it
//"soilmoisture=" is what is going to be sent using GET to the apache server, see code in moisture_values.php
// Read analog value, in this case a soil moisture

  int data = analogRead(AnalogIn);
  String dataString = "moisture="+(String)data;

  if (count==1){
    dataString=dataString+"&&first=true";
  }else{
    dataString=dataString+"&&first=false";
  }

  Serial.println("");
  Serial.println("connection successed");
  
  // We now create a URI for the request
  String url = "GET /hospital/index.html  HTTP/1.1";
  client.println(url);
  client.println("Host:"+host);
  client.println("Connection: close");
  client.println();
  delay(500);
  
  // Read all the lines of the reply from server and print them to Serial
  while(client.available()){
    String line = client.readStringUntil('\r');
    Serial.print(line);
    if(line == (String)'\r') break;
  }
  
  //ESP.deepSleep(60U*60*1000000); //U for unsigned
  delay(2000); //for above sleep
  
}
