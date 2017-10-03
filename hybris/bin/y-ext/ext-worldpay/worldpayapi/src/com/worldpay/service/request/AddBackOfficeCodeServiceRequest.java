package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#addBackOfficeCode(AddBackOfficeCodeServiceRequest) addBackOfficeCode()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the back office code that needs to be sent</p>
 */
public class AddBackOfficeCodeServiceRequest extends AbstractServiceRequest {

    private String backOfficeCode;

    protected AddBackOfficeCodeServiceRequest(MerchantInfo merchantInfo, String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the AddBackOfficeCodeServiceRequest
     *
     * @param merch          merchantInfo to be used in the Worldpay call
     * @param orderCode      orderCode to be used in the Worldpay call
     * @param backOfficeCode backOfficeCode to be used in the Worldpay call
     * @return new instance of the AddBackOfficeCodeServiceRequest initialised with input parameters
     */
    public static AddBackOfficeCodeServiceRequest createAddBackOfficeCodeRequest(MerchantInfo merch, String orderCode, String backOfficeCode) {
        if (merch == null || orderCode == null || backOfficeCode == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo, Order Code and Back Office Code cannot be null");
        }
        AddBackOfficeCodeServiceRequest addBackOfficeCodeRequest = new AddBackOfficeCodeServiceRequest(merch, orderCode);
        addBackOfficeCodeRequest.setBackOfficeCode(backOfficeCode);

        return addBackOfficeCodeRequest;
    }

    public String getBackOfficeCode() {
        return backOfficeCode;
    }

    public void setBackOfficeCode(String backOfficeCode) {
        this.backOfficeCode = backOfficeCode;
    }
}
