#include<SoftwareSerial.h>

#define TxD 10
#define RxD 9
#define micdataPin A0 

SoftwareSerial bluetoothSerial(TxD, RxD); // Use pins 9,10 for bluetooth

#define ledPin 13;                        // Use on device LED at pin 13
#define buttonPin 2;                      // Emergency button at pin 2
volatile byte emergencyState = LOW;       // Normally emergency state is turned off
int micdata=0; 

void setup() {
  pinMode(ledPin, OUTPUT);                // Use LED pin for output 
  pinMode(buttonPin, INPUT_PULLUP);       // Use button pin for input
  // Attache a rising signal interrupt on button pin
  // When the button is pressed one time, set the change the emergency state
  attachInterrupt(digitalPinToInterrupt(buttonPin), changeToEmergency, RISING);
  bluetoothSerial.begin(9600);            // Initiate bluetooth comm
}

void loop() {
  digitalWrite(ledPin, emergencyState);   // Turns led on when emergency
  micdata = analogRead(micdataPin); 
  
  if(emergencyState){                      // When in Emergency state
    bluetoothSerial.print(1);              // Send data 1 signal to app
  }                                       //  over bluetooth
  
  if(bluetoothSerial.available()){
    emergencyState=LOW;                   // If any signal is received from the app
  }                                       // Change the emergency state to low
  
  
}
// Function to change the state of emergency from LOW to HIGH (0 to 1)
void changeToEmergency() {
  emergencyState = !emergencyState;
}
