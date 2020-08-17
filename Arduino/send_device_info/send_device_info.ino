#define output_pin 7
#define device1_pin 8
#define device2_pin 13

int state = 0;

void send_device_info(){
    char dev[] = "1:t,2:f"; // we can use this format : "pin_no : device_code"
    
//    BTserial.print(dev);
	Serial.print(dev); // one of these lines should work, unable to test
}

void perform_command(String inp[]){
	char d = inp[0].c_str();
	char c = inp[1].c_str();
	
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
    pinMode(device1_pin, OUTPUT);
    pinMode(device2_pin, OUTPUT);
    Serial.begin(9600);
//    send_device_info();
}

void loop() {
	
	if (Serial.available() > 0){
		String inp;
		while(Serial.available() > 0) inp += Serial.read();

    if (inp == "d") send_device_info();
   
//		switch(inp){
//			case "d":
//				send_device_info();
//				break;
//			
//			case "11": // turn on device 1
//				perform_command("11");
//				break;
//			case "10": // turn off device 1
//				perform_command("10");
//				break;
//				
//			case "21": // turn on device 2
//				perform_command("21");
//				break;
//			case "20": // turn off device 2
//				perform_command("20");
//				break;
//			
//			default:
//				printf("Invalid State Entered!\n");
//		}
	}
}
