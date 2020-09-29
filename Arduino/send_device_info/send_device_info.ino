int dev_no[] = {13,12,11,10,9,8};                       //keep adding device pin number corrosponding to the device name
String name[] = {"tubelight","fan","cfl","lamp","socket","bulb"};          //keep adding device name corrosponding to the device pin number
bool status[sizeof(dev_no)/sizeof(dev_no[0])];

void send_device_info() {                     //we can use this format : "dev_no : device_code"
  String device_info;
  for(int i=0;i<(sizeof(dev_no)/sizeof(dev_no[0]));i++) {
    device_info.concat(String(i));
    device_info.concat(':');
    device_info.concat(name[i].charAt(0));
    device_info.concat(':');
    device_info.concat(String(status[i]));
    device_info.concat(',');
  }             
  Serial.println(device_info); 
}

void perform_command(String inp) {            //this format will be recieved : "dev_no : status"
  String dev = inp.substring(0,inp.indexOf(':'));
  char c = inp.charAt(inp.length()-1);
  digitalWrite(dev_no[dev.toInt()], c-'0');
  status[dev.toInt()] = c-'0';
}

void setup() {
  Serial.begin(9600);
  for(int i=0;i<(sizeof(dev_no)/sizeof(dev_no[0]));i++) pinMode(dev_no[i],OUTPUT);
}

void loop() {
   if(Serial.available()) {
     String in=Serial.readString();
     if(in=="read_device") send_device_info();   
     else perform_command(in);
   }
}
