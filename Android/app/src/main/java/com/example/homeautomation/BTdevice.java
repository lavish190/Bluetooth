package com.example.homeautomation;

public class BTdevice {
    public String name;
    public String address;

    BTdevice(String name, String address) {
        this.name=name;
        this.address=address;
    }
    public void printDevices() {
        System.out.println(this.name);
        System.out.println(this.address);
    }
}