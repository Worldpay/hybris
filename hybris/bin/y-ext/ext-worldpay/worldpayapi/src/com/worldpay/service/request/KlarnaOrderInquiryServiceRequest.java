package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.MerchantInfo;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#orderInquiry(AbstractServiceRequest)} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>No further parameters are required on top of the standard ones</p>
 */
public class KlarnaOrderInquiryServiceRequest extends AbstractServiceRequest {

    protected KlarnaOrderInquiryServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating a new KlarnaOrderInquiryServiceRequest
     *
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderCode orderCode to be used in the Worldpay call
     * @return new instance of the KlarnaOrderInquiryServiceRequest initialised with input parameters
     */
    public static KlarnaOrderInquiryServiceRequest createKlarnaOrderInquiryRequest(final MerchantInfo merch, final String orderCode) {
        if (merch == null || orderCode == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo and Order Code cannot be null");
        }
        return new KlarnaOrderInquiryServiceRequest(merch, orderCode);
    }
}
