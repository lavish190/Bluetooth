package com.example.homeautomation;

class Devices{
    public int dev_no;
    public String name;
    public int status; // on, off, etc

    Devices(int p_no, String n){
        this.dev_no = p_no;
        if (n.equals("t")) this.name = "Tubelight";
        else if (n.equals("f")) this.name = "Fan";
        this.status = 0;
    }

    public void printDevices () {
        System.out.println(this.dev_no);
        System.out.println(this.name);
        System.out.println(this.status);
    }
}