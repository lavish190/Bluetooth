#define output_pin 7
#define device1_pin 8
#define device2_pin 9

char inp[]={0,0};
bool power=false;

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
void perform_command(char inp[]){
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

void device() {
  while(!power)
  {
    if(Serial.available())
    {
      char d=Serial.read();
      if(d=='d')
      {
        send_device_info();
        power=true;
      }
    }
  }
}
void setup() {
    pinMode(output_pin, OUTPUT);
    // digitalWrite(output_pin, LOW);
    Serial.begin(9600);
}

void loop() {
  if(!power) device();
  if(Serial.available() > 0 && power == true) {
        for(int i=0;Serial.available() > 0;i++) {
        inp[i] = Serial.read();
      }
      Serial.println(inp[0],inp[1]);
    
      perform_command(inp);
    }
}
