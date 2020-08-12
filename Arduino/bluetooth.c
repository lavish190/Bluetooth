#include<SoftwareSerial.h>

/* Create object named bt of the class SoftwareSerial */ 
SoftwareSerial bt(2,3); /* (Rx,Tx) */	

void setup(){
  bluetooth.begin(9600);	/* Define baud rate for software serial communication */
  Serial.begin(9600);	/* Define baud rate for serial communication */
}

void loop() {
  
    if (bluetooth.available()){	/* If data is available on serial port */
    
     Serial.write(bluetooth.read());	/* Print character received on to the serial monitor */
    }
}