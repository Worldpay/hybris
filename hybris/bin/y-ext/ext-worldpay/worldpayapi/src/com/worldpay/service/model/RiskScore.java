package com.worldpay.service.model;

import java.io.Serializable;

public class RiskScore implements Serializable {
    private String value;
    private String provider;
    private String id;
    private String finalScore;
    private String rgid;
    private String tScore;
    private String tRisk;
    private String message;
    private String extendedResponse;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String value) {
        this.provider = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getFinalScore() {
        return this.finalScore;
    }

    public void setFinalScore(String value) {
        this.finalScore = value;
    }

    public String getRGID() {
        return this.rgid;
    }

    public void setRGID(String value) {
        this.rgid = value;
    }

    public String getTScore() {
        return this.tScore;
    }

    public void setTScore(String value) {
        this.tScore = value;
    }

    public String getTRisk() {
        return this.tRisk;
    }

    public void setTRisk(String value) {
        this.tRisk = value;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getExtendedResponse() {
        return this.extendedResponse;
    }

    public void setExtendedResponse(String value) {
        this.extendedResponse = value;
    }

    @Override
    public String toString() {
        return "RiskScore{" +
                "value='" + value + '\'' +
                ", provider='" + provider + '\'' +
                ", id='" + id + '\'' +
                ", finalScore='" + finalScore + '\'' +
                ", rgid='" + rgid + '\'' +
                ", tScore='" + tScore + '\'' +
                ", tRisk='" + tRisk + '\'' +
                ", message='" + message + '\'' +
                ", extendedResponse='" + extendedResponse + '\'' +
                '}';
    }
}
