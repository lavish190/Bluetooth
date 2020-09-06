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
}