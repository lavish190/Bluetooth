#define output_pin 7


void send_device_info(){
    
}

void setup() {
    pinMode(output_pin, OUTPUT);
    // digitalWrite(output_pin, LOW);
    Serial.begin(9600);
    send_device_info();
}

void loop() {
    // if(Serial.available() > 0){ // Checks whether data is comming from the serial port
    //     state = Serial.read(); // Reads the data from the serial port

    //     if (state == '0'){
    //         digitalWrite(ledPin, LOW); // Turn LED OFF
    //         Serial.println("LED: OFF"); // Send back, to the phone, the String "LED: ON"
    //         state = 0;
    //     }
    //     else if (state == '1') {
    //         digitalWrite(ledPin, HIGH);
    //         Serial.println("LED: ON");;
    //         state = 0;
    //     }
    // }
}