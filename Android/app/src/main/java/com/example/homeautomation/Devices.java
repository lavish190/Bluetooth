package com.example.homeautomation;

class Devices{
    public int dev_no;
    public String name;
    public int status; // on, off, etc

    Devices(int p_no, String n, int status){
        this.dev_no = p_no;

        switch (n) {
            case "t":
                this.name = "Tubelight";
                break;
            case "f":
                this.name = "Fan";
                break;
            case "s":
                this.name = "Socket";
                break;
            case "l":
                this.name = "Lamp";
                break;
            case "c":
                this.name = "CFL";
                break;
            case "b":
                this.name = "Bulb";
                break;
        }

        this.status = status;
    }

    public void printDevices () {
        System.out.println(this.dev_no);
        System.out.println(this.name);
        System.out.println(this.status);
    }
}