package com.worldpay.service.model.threeds2;

import java.io.Serializable;

public class AuthenticationRiskData implements Serializable {
    private RiskDateData authenticationTimestamp;

    private String authenticationMethod;

    public RiskDateData getAuthenticationTimestamp() {
        return authenticationTimestamp;
    }

    public void setAuthenticationTimestamp(RiskDateData authenticationTimestamp) {
        this.authenticationTimestamp = authenticationTimestamp;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    @Override
    public String toString() {
        return "AuthenticationRiskData{" +
                "authenticationTimestamp=" + authenticationTimestamp +
                ", authenticationMethod='" + authenticationMethod + '\'' +
                '}';
    }
}
