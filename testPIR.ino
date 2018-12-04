

#include <Wire.h>  // This library is already built in to the Arduino IDE
#include <LiquidCrystal_I2C.h> //This library you can add via Include Library > Manage Library > 
LiquidCrystal_I2C lcd(0x27, 16, 2);

int value = 0;
int pinPir = 0;    // D3

//int pinPir = 4;    // D2

void setup() {

  Serial.begin(115200);
  pinMode(pinPir, INPUT_PULLUP);

  //attachInterrupt(pinPir, flow, LOW); // Setup Interrupt 

  lcd.init();   // initializing the LCD
//  L DK
  
}

void loop() {
  
//  value = digitalRead(pinPir);

// digitalRead(pinPir);
   //Serial.println(value); 

  if (!digitalRead(pinPir)) { 
    Serial.println("Motion detected"); 
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("Motion");
    lcd.setCursor(1, 1);      
    lcd.print("Detected");
    
  }
  else{
    Serial.println("No Motion"); 
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("No");
    lcd.setCursor(1, 1);      
    lcd.print("Motion");
  }

  delay(1000);
}

void display(){
      lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("No");
    lcd.setCursor(1, 1);      
    lcd.print("Motion");
}

