package com.example.ahmed.sqlitedatabase;

public class List_medicine {
    private int id;
    private String disease_name;
    private String medicine_name;
    private String details;
    private byte[] image;

    public List_medicine(int id, String disease_name, String medicice_name, String details, byte[] image) {
        this.id = id;
        this.disease_name = disease_name;
        this.medicine_name = medicice_name;
        this.details = details;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisease_name() {
        return disease_name;
    }

    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
