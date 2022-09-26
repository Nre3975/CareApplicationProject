package com.example.tm470_careapp;

public class SUMedicalConditionCardModel {

    private String suMedicalCondition;
    private String suMedicalConditionCategory;
    private String suMedicalConditionDateDiagnosed;
    private String suMedicalConditionDateEnded;
    private Boolean isVisible;

    public SUMedicalConditionCardModel(String suMedicalCondition, String suMedicalConditionCategory, String suMedicalConditionDateDiagnosed, String suMedicalConditionDateEnded) {
        this.suMedicalCondition = suMedicalCondition;
        this.suMedicalConditionCategory = "Medical Category: " + suMedicalConditionCategory;
        this.suMedicalConditionDateDiagnosed = suMedicalConditionDateDiagnosed;
        this.suMedicalConditionDateEnded = suMedicalConditionDateEnded;
        this.isVisible = true;
    }

    public String getSUMedicalCondition() {
        return suMedicalCondition;
    }

    public String getSUMedicalConditionCategory() {
        return suMedicalConditionCategory;
    }

    public String getSUMedicalConditionDateDiagnosed() {
        return suMedicalConditionDateDiagnosed;
    }

    public String getSUMedicalConditionDateEnded() {
        return suMedicalConditionDateEnded;
    }

    public Boolean getIsVisible() {
        return this.isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
}
