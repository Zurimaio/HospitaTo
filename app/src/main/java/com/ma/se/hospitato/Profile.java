package com.ma.se.hospitato;

public class Profile {
    private String name;
    private String surname;
    private String email;
    private String nascita;
    private String blood;
    private String height;
    private String weight;

    public Profile() {
    }

    public Profile(String name, String surname, String email, String nascita, String blood, String height, String weight) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.nascita = nascita;
        this.blood = blood;
        this.height = height;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNascita() {
        return nascita;
    }

    public void setNascita(String nascita) {
        this.nascita = nascita;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
