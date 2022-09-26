package com.example.tm470_careapp;

public class SUContactsCardModel {

    private String suContactName;
    private String suContactType;
    private String suContactParentOrg;
    private String suContactNumberMain;
    private String suContactNumberWork;
    private String suContactNumberHome;
    private String suContactEmailMain;
    private String suContactEmailSecondary;
    private String suContactAddress;
    private Boolean AddressIsVisible;

    public SUContactsCardModel(String suContactName, String suContactType, String suContactParentOrg, String suContactNumberMain, String suContactNumberWork, String suContactNumberHome, String suContactEmailMain, String suContactEmailSecondary, String suContactAddress) {
        this.suContactName = suContactName;
        this.suContactType = suContactType;
        this.suContactParentOrg = suContactParentOrg;
        this.suContactNumberMain = suContactNumberMain;
        this.suContactNumberWork = suContactNumberWork;
        this.suContactNumberHome = suContactNumberHome;
        this.suContactEmailMain = suContactEmailMain;
        this.suContactEmailSecondary = suContactEmailSecondary;
        this.suContactAddress = suContactAddress;
        AddressIsVisible = false;
    }

    public String getSuContactName() {
        return suContactName;
    }

    public String getSuContactType() {
        return suContactType;
    }

    public String getSuContactParentOrg() {
        return suContactParentOrg;
    }

    public String getSuContactNumberMain() {
        return suContactNumberMain;
    }

    public String getSuContactNumberWork() {
        return suContactNumberWork;
    }

    public String getSuContactNumberHome() {
        return suContactNumberHome;
    }

    public String getSuContactEmailMain() {
        return suContactEmailMain;
    }

    public String getSuContactEmailSecondary() {
        return suContactEmailSecondary;
    }

    public String getSuContactAddress() {
        return suContactAddress;
    }

    public Boolean getAddressIsVisible() {
        return AddressIsVisible;
    }

    public void setAddressIsVisible(Boolean addressIsVisible) {
        AddressIsVisible = addressIsVisible;
    }
}
