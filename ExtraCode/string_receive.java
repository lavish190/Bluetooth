import java.util.*;
import java.lang.*;
import java.io.*;

class ListItem{
    public int pin_no;
    public String name;
    public int status; // on, off, etc

    ListItem(int p_no, String n){
    	this.pin_no = p_no;
    	if (n.equals("t")) this.name = "Tubelight";
    	else if (n.equals("f")) this.name = "Fan";
    	this.status = 0;

    	System.out.println(this.pin_no);
    	System.out.println(this.name);
    	System.out.println(this.status);
    }
}

class string_receive{
	public static void main (String[] args){
	    Scanner sc = new Scanner(System.in);
		String received = sc.nextLine(); // 1:t,2:f

		ListItem[] device_list = new ListItem[17]; // no of devices supported in arduino
		int d = 0;

		int i=0;
		while(i < received.length()){
	    	int pin_no = 0;

	    	while(i < received.length() && Character.isDigit(received.charAt(i))){
	    		pin_no = pin_no*10 + Character.getNumericValue(received.charAt(i));
	    		i++;
	    	}

	    	i++; // skip :

	    	String device_code = "";
	    	while(i < received.length() && Character.isLetter(received.charAt(i))){
	    		device_code += received.charAt(i);
	    		i++;
	    	}

	    	// create ListItem -->change to android code
	    	ListItem device = new ListItem(pin_no, device_code);
	    	device_list[d] = device;
	    	d++;

	    	i++; // skip ,
		}
		
	}
}
