#define device1_pin 13
#define device2_pin 8

void send_device_info(){
    String dev = "1:t,2:f"; // we can use this format : "pin_no : device_code";
  Serial.println(dev); 
}

/////////////////////////////////////////////////////////////////////
//      case "11": // turn on device 1
//      case "10": // turn off device 1
//      case "21": // turn on device 2
//      case "20": // turn off device 2 
/////////////////////////////////////////////////////////////////////
void perform_command(String inp){
  char d = inp.charAt(0);
  char c = inp.charAt(1);
  
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
  Serial.begin(9600);
  pinMode(13,OUTPUT);
}

void loop() {
   if(Serial.available())
   {
     String in=Serial.readString();
     if(in=="read_device")
      send_device_info();   
     else
      perform_command(in);
   }
}
