package com.worldpay.service.payment;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;

public interface WorldpayExemptionService {

    /**
     * Checks if the response has an Exemption attribute and if so, whether the exemption was honoured
     *
     * @param exemptionResponseInfo with the exemption response from Worldpay
     * @return true if the exemption was honoured or not present, false if the exemption was rejected or out of scope
     */
    boolean isExemptionHonoured(final ExemptionResponseInfo exemptionResponseInfo);


    /**
     * Checks whether a request with an exemption should be retried without an exemption
     *
     * @param response containing the response data
     * @return true if the request needs to be retried and false if it doesn't
     */
    boolean isRequestWithExemptionToBeRetriedWithoutExemption(final DirectAuthoriseServiceResponse response);
}
