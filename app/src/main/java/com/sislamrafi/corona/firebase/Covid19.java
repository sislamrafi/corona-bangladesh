package com.sislamrafi.corona.firebase;

public class Covid19 {
    private int Active;
    private int Cases;
    private int Death;
    private int Recovered;
    private int Critical;
    private int New;
    private String lastUpdate;

    public Covid19(){}

    public Covid19(int active, int cases, int death, int recovered, int critical, int aNew) {
        Active = active;
        Cases = cases;
        Death = death;
        Recovered = recovered;
        Critical = critical;
        New = aNew;
    }

    public int getActive() {
        return Active;
    }

    public int getCases() {
        return Cases;
    }

    public int getDeath() {
        return Death;
    }

    public int getRecovered() {
        return Recovered;
    }

    public int getCritical() {
        return Critical;
    }

    public int getNew() {
        return New;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
