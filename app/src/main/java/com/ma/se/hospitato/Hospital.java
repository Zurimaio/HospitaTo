package com.ma.se.hospitato;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Hospital {


    private String Name;
    private String Address;
    private String PhoneNumber;
    private HashMap<String, Boolean> Departments;
    private HashMap<String, String> Coordinate;


    public Hospital(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public HashMap<String, Boolean> getDepartments() {
        return Departments;
    }

    public void setDepartments(HashMap<String, Boolean> departments) {
        Departments = departments;
    }

    public HashMap<String, String> getCoordinate() {
        return Coordinate;
    }

    public void setCoordinate(HashMap<String, String> coordinate) {
        Coordinate = coordinate;
    }
}

