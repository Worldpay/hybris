package com.worldpay.service.payment;

import com.worldpay.payment.DirectResponseData;

/**
 * Service to control the flow of the response for a direct order response
 */
public interface WorldpayDirectResponseService {
    /**
     * Returns whether the response is Authorised or not
     *
     * @param directResponseData
     * @return
     */
    Boolean isAuthorised(DirectResponseData directResponseData);

    /**
     * Returns whether the response is Cancelled or not
     * @param directResponseData
     * @return
     */
    Boolean isCancelled(DirectResponseData directResponseData);

    /**
     * Returns whether the response is legacy 3Ds Flow
     * @param directResponseData
     * @return
     */
    Boolean is3DSecureLegacyFlow(DirectResponseData directResponseData);

    /**
     * Returns whether the response is Flex 3Ds Flow
     * @param directResponseData
     * @return
     */
    Boolean is3DSecureFlexFlow(DirectResponseData directResponseData);
}
