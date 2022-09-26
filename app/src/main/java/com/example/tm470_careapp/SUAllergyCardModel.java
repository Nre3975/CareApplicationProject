package com.example.tm470_careapp;

public class SUAllergyCardModel {

    private String suAllergyType;
    private String suAllergyCategory;
    private String suAllergyDateDiagnosed;
    private String suAllergyDateEnded;
    private Boolean isVisible;

    public SUAllergyCardModel(String suAllergyType, String suAllergyCategory, String suAllergyDateDiagnosed, String suAllergyDateEnded) {
        this.suAllergyType = suAllergyType;
        this.suAllergyCategory = "Allergy Category: " + suAllergyCategory;
        this.suAllergyDateDiagnosed = suAllergyDateDiagnosed;
        this.suAllergyDateEnded = suAllergyDateEnded;
        this.isVisible = false;
    }

    public String getSuAllergyType() {
        return suAllergyType;
    }

    public String getSuAllergyCategory() {
        return suAllergyCategory;
    }

    public String getSuAllergyDateDiagnosed() {
        return suAllergyDateDiagnosed;
    }

    public String getSuAllergyDateEnded() {
        return suAllergyDateEnded;
    }

    public Boolean getIsVisible() {
        return this.isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
}
