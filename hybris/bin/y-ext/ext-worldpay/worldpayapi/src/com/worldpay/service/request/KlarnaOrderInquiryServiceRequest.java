package com.worldpay.service.request;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#orderInquiry(AbstractServiceRequest)} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>No further parameters are required on top of the standard ones</p>
 */
public class KlarnaOrderInquiryServiceRequest extends AbstractServiceRequest {

    protected KlarnaOrderInquiryServiceRequest(final WorldpayConfig config, final MerchantInfo merchantInfo, final String orderCode) {
        super(config, merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating a new KlarnaOrderInquiryServiceRequest
     *
     * @param config    worldpayConfig to be used in the Worldpay call
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderCode orderCode to be used in the Worldpay call
     * @return new instance of the KlarnaOrderInquiryServiceRequest initialised with input parameters
     */
    public static KlarnaOrderInquiryServiceRequest createKlarnaOrderInquiryRequest(final WorldpayConfig config, final MerchantInfo merch, final String orderCode) {
        if (config == null || merch == null || orderCode == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo and Order Code cannot be null");
        }
        return new KlarnaOrderInquiryServiceRequest(config, merch, orderCode);
    }
}
