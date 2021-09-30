package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.Amount;
import com.worldpay.service.request.CaptureServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#capture(CaptureServiceRequest)}  capture()} in the
 * WorldpayServiceGateway
 *
 * <p>On top of the standard parameters it provides the amount captured</p>
 *
 */
public class CaptureServiceResponse extends AbstractServiceResponse {

    private Amount amount;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

}
