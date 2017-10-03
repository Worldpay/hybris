package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#orderInquiry(AbstractServiceRequest) orderInquiry()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>No further parameters are required on top of the standard ones</p>
 */
public class OrderInquiryServiceRequest extends AbstractServiceRequest {

    protected OrderInquiryServiceRequest(MerchantInfo merchantInfo, String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the OrderInquiryServiceRequest
     *
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderCode orderCode to be used in the Worldpay call
     * @return new instance of the OrderInquiryServiceRequest initialised with input parameters
     */
    public static OrderInquiryServiceRequest createOrderInquiryRequest(MerchantInfo merch, String orderCode) {
        if (merch == null || orderCode == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo and Order Code cannot be null");
        }
        return new OrderInquiryServiceRequest(merch, orderCode);
    }
}
