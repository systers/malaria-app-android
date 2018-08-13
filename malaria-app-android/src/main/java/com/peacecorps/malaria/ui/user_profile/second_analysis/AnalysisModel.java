package com.peacecorps.malaria.ui.user_profile.second_analysis;

/**
 * Created by Anamika Tripathi on 8/8/18.
 */
public class AnalysisModel {
    private String month;
    private String percentage;

    public AnalysisModel(String month, String percentage) {
        this.month = month;
        this.percentage = percentage;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
