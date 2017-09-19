package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.request.AddBackOfficeCodeServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#addBackOfficeCode(AddBackOfficeCodeServiceRequest) addBackOfficeCode()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the back office code</p>
 */
public class AddBackOfficeCodeServiceResponse extends AbstractServiceResponse {

    private String backOfficeCode;

    public String getBackOfficeCode() {
        return backOfficeCode;
    }

    public void setBackOfficeCode(String backOfficeCode) {
        this.backOfficeCode = backOfficeCode;
    }
}
