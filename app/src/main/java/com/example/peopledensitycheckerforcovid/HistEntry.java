package com.example.peopledensitycheckerforcovid;

public class HistEntry {

    private String histName;
    private String histCon;
    private String histTemp;
    private String histTime;
    private String histLoc;

    public HistEntry (String histName,String histCon,String histTemp,String histTime, String histLoc ){
        this.histName = histName;
        this.histCon = histCon;
        this.histTemp = histTemp;
        this.histTime = histTime;
        this.histLoc = histLoc;
    }

    public String getHistName() {
        return histName;
    }

    public void setHistName(String histName) {
        this.histName = histName;
    }

    public String getHistCon() {
        return histCon;
    }

    public void setHistCon(String histCon) {
        this.histCon = histCon;
    }

    public String getHistTemp() {
        return histTemp;
    }

    public void setHistTemp(String histTemp) {
        this.histTemp = histTemp;
    }

    public String getHistTime() {
        return histTime;
    }

    public void setHistTime(String histTime) {
        this.histTime = histTime;
    }

    public String getHistLoc() {
        return histLoc;
    }

    public void setHistLoc(String histLoc) {
        this.histLoc = histLoc;
    }
}
