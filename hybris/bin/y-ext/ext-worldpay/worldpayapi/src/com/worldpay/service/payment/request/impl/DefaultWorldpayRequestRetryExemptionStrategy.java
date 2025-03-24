package com.worldpay.service.payment.request.impl;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.service.payment.WorldpayExemptionService;
import com.worldpay.service.payment.request.WorldpayRequestRetryStrategy;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DefaultWorldpayRequestRetryExemptionStrategy implements WorldpayRequestRetryStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayRequestRetryExemptionStrategy.class);

    protected final WorldpayExemptionService worldpayExemptionService;

    public DefaultWorldpayRequestRetryExemptionStrategy(final WorldpayExemptionService worldpayExemptionService) {
        this.worldpayExemptionService = worldpayExemptionService;
    }

    @Override
    public boolean isRequestToBeRetried(final DirectAuthoriseServiceRequest request,
                                        final DirectAuthoriseServiceResponse response) {
        return !response.isError() && worldpayExemptionService.isRequestWithExemptionToBeRetriedWithoutExemption(response);
    }

    @Override
    public DirectAuthoriseServiceRequest getDirectAuthoriseServiceRequestToRetry(final DirectAuthoriseServiceRequest request,
                                                                                 final DirectAuthoriseServiceResponse response) {
        final String reason = Optional.ofNullable(response)
            .map(DirectAuthoriseServiceResponse::getExemptionResponseInfo)
            .map(ExemptionResponseInfo::getReason)
            .orElse(null);
        final String result = Optional.ofNullable(response)
            .map(DirectAuthoriseServiceResponse::getExemptionResponseInfo)
            .map(ExemptionResponseInfo::getResult)
            .orElse(null);

        LOG.warn("Request with Exemption has been rejected and hasn't been authorized by Worldpay. Reason {}, Result: {}", reason, result);
        LOG.warn("Retrying request without Exemption");

        request.getOrder().setExemption(null);

        return request;
    }
}
