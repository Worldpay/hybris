package com.worldpay.service.payment.request;

import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;

public interface WorldpayRequestRetryStrategy {

    /**
     * Determines if a request made needs to be made again according to the response data
     *
     * @param request to check
     * @param response to check
     * @return whether the request is to be retried
     */
    boolean isRequestToBeRetried(final DirectAuthoriseServiceRequest request, final DirectAuthoriseServiceResponse response);

    /**
     * Modifies the request data in order to retry the request again if needed
     *
     * @param request to modify
     * @param response originally received
     * @return the modified request that will be retried
     */
    DirectAuthoriseServiceRequest getDirectAuthoriseServiceRequestToRetry(final DirectAuthoriseServiceRequest request,
                                                                          final DirectAuthoriseServiceResponse response);
}
