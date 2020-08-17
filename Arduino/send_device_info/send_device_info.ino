#define output_pin 7
#define device1_pin 8
#define device2_pin 9

int state = 0;

void send_device_info(){
    String dev = "1:t,2:f"; // we can use this format : "pin_no : device_code"
    
//    BTserial.print(dev);
	Serial.println(dev); // one of these lines should work, unable to test
}


/////////////////////////////////////////////////////////////////////
//      case "11": // turn on device 1
//      case "10": // turn off device 1
//      case "21": // turn on device 2
//      case "20": // turn off device 2 
/////////////////////////////////////////////////////////////////////
void perform_command(String inp){
	char d = inp[0];
	char c = inp[1];
	
	switch(d){
		case '1': //code for device 1
			if (c == '1') digitalWrite(device1_pin, HIGH);
			else digitalWrite(device1_pin, LOW);
			break;
		case '2': //code for device 2
			if (c == '1') digitalWrite(device2_pin, HIGH);
			else digitalWrite(device2_pin, LOW);
			break;
	}
}


void setup() {
    pinMode(output_pin, OUTPUT);
    // digitalWrite(output_pin, LOW);
    Serial.begin(9600);
//    send_device_info();
}

void loop() {
	
	if (Serial.available() > 0){
		String inp;
		while(Serial.available() > 0) inp += Serial.read();

    if (inp == "d") send_device_info();
    else perform_command(inp);
	}
}
