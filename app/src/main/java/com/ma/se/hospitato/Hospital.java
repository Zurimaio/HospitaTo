package com.ma.se.hospitato;

public class Hospital {

    private String name, address;

    public Hospital(String name, String address) {
        this.name = name;
        this.address = address;

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }


}