package com.worldpay.service.payment;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;

/**
 * Service to retrieve the JWT for DDC for 3dSecure2 implementation
 */
public interface WorldpayJsonWebTokenService {

    /**
     * Creates a json web token for DDC
     * @return
     */
    String createJsonWebTokenForDDC(final WorldpayMerchantConfigData worldpayMerchantConfigData);

    /**
     * Create a json web token for 3DSecure Flex Challenge Iframe using the values stored in {@code worldpayMerchantConfigData}
     * and the values stored in {@code directResponseData}
     *
     * @param worldpayMerchantConfigData
     * @param directResponseData
     * @return
     */
    String createJsonWebTokenFor3DSecureFlexChallengeIframe(final WorldpayMerchantConfigData worldpayMerchantConfigData, DirectResponseData directResponseData) throws WorldpayConfigurationException;
}
