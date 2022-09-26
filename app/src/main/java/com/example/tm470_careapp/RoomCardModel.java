package com.example.tm470_careapp;

import android.graphics.Bitmap;

public class RoomCardModel {

    private String roomOccupantKnownAs;
    private String roomOccupantLastName;
    private String roomOccupantFullName;
    private String roomNumber;
    private String roomOccupantSex;
    private String roomType;
    private String roomServiceUserId;
    Bitmap imageBitmap;

    public RoomCardModel(String roomOccupantKnownAs, String roomOccupantLastName, String roomNumber, String roomOccupantSex, String roomType, String roomServiceUserId, Bitmap imageBitmap) {
        this.roomOccupantKnownAs = roomOccupantKnownAs;
        this.roomOccupantFullName = roomOccupantKnownAs + ' ' + roomOccupantLastName;
        this.roomNumber = roomNumber;
        this.roomOccupantSex = "("+roomOccupantSex+")";
        this.roomType = roomType + " Room";
        this.roomServiceUserId = roomServiceUserId;
        this.imageBitmap = imageBitmap;
    }

    public String getRoomOccupantKnownAs() {
        return roomOccupantKnownAs;
    }

    public String getRoomOccupantFullName() {
        return roomOccupantFullName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomOccupantSex() {
        return roomOccupantSex;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomServiceUserId() {
        return roomServiceUserId;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
