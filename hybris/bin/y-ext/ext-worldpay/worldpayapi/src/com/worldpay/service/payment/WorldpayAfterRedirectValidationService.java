package com.worldpay.service.payment;

import com.worldpay.data.MerchantInfo;

import java.util.Map;

/**
 * Handles validation of the parameters in the URL on the redirect response from Worldpay
 */
public interface WorldpayAfterRedirectValidationService {

    /**
     * Checks that the response parameters that have been returned are valid. Ensures mac code is correct if supplied
     *
     * @param merchantInfo The {@link MerchantInfo} object to be used with this call to worldpay
     * @param resultMap    The object containing the parameters returned from Worldpay redirect
     * @return true if the response is valid
     */
    boolean validateRedirectResponse(final MerchantInfo merchantInfo, final Map<String, String> resultMap);
}
