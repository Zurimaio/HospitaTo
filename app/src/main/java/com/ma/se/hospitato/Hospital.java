package com.ma.se.hospitato;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.HashMap;

public class Hospital implements Parcelable {


    private String Name;
    private String Address;
    private String PhoneNumber;
    private Integer estimatedTime;
    private HashMap<String, Boolean> Departments;
    private HashMap<String, String> Coordinate;
    private HashMap <String, Integer> patients;

    public Hospital(){}


    protected Hospital(Parcel in) {
        Name = in.readString();
        Address = in.readString();
        PhoneNumber = in.readString();
        estimatedTime = in.readInt();
    }

    public static final Creator<Hospital> CREATOR = new Creator<Hospital>() {
        @Override
        public Hospital createFromParcel(Parcel in) {
            return new Hospital(in);
        }

        @Override
        public Hospital[] newArray(int size) {
            return new Hospital[size];
        }
    };

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        this.Name = name;
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

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Address);
        dest.writeString(PhoneNumber);
        dest.writeInt(estimatedTime);
    }

    public HashMap<String, Integer> getPatients() {
        return patients;
    }

    public void setPatients(HashMap<String, Integer> patients) {
        this.patients = patients;
    }

    public static final Comparator<Hospital> BY_TIME=new Comparator<Hospital>() {
        @Override
        public int compare(Hospital hospital, Hospital t1) {
            return hospital.estimatedTime.compareTo(t1.estimatedTime);
        }
    };
}
