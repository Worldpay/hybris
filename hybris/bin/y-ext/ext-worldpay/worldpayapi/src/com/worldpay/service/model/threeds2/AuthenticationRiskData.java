package com.worldpay.service.model.threeds2;

import com.worldpay.internal.model.AuthenticationTimestamp;
import com.worldpay.service.model.Date;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.Optional;

public class AuthenticationRiskData implements InternalModelTransformer, Serializable {

    private String authenticationMethod;
    private Date authenticationTimestamp;

    @Override
    public com.worldpay.internal.model.AuthenticationRiskData transformToInternalModel() {
        final com.worldpay.internal.model.AuthenticationRiskData internalAuthenticationRiskData = new com.worldpay.internal.model.AuthenticationRiskData();
        internalAuthenticationRiskData.setAuthenticationMethod(authenticationMethod);
        Optional.ofNullable(authenticationTimestamp)
            .map(Date::transformToInternalModel)
            .map(this::createIntAuthenticationTimestamp)
            .ifPresent(internalAuthenticationRiskData::setAuthenticationTimestamp);
        return internalAuthenticationRiskData;
    }

    private AuthenticationTimestamp createIntAuthenticationTimestamp(com.worldpay.internal.model.Date intDate) {
        final AuthenticationTimestamp intAuthenticationTimestamp = new AuthenticationTimestamp();
        intAuthenticationTimestamp.setDate(intDate);
        return intAuthenticationTimestamp;
    }

    public Date getAuthenticationTimestamp() {
        return authenticationTimestamp;
    }

    public void setAuthenticationTimestamp(final Date authenticationTimestamp) {
        this.authenticationTimestamp = authenticationTimestamp;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(final String authenticationMethod) {
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
