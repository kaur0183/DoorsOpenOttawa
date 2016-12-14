package com.kaur0183algonquincollege.doorsopenottawa.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * This Class is used for setting and getting the buildingID, address, image, description and open hours
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */

public class Building {
    private int buildingID;
    private String name;
    private String address;
    private String image;
    private String description;
    private List<String> openhours;
    private Bitmap bitmap;

    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }

    public int getBuildingID() {
        return buildingID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }

    public String getAddress() {
        return address;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getOpenhours() {
        return openhours;
    }

    public void setOpenhours(List<String> openhours) {
        this.openhours = openhours;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

