package com.vermaxdevs.covid19dashboard;

public class States {
    String sName, confirmed, active, recovered;

    public States(String state_name, String sConfirmed, String sActive, String sRecovered) {
        this.setsName(state_name);
        this.setConfirmed(sConfirmed);
        this.setActive(sActive);
        this.setRecovered(sRecovered);
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }
}
