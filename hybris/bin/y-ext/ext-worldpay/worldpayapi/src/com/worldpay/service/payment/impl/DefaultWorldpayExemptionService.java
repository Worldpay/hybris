package com.worldpay.service.payment.impl;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.payment.WorldpayExemptionService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;

import java.util.Optional;

public class DefaultWorldpayExemptionService implements WorldpayExemptionService {

    protected static final String EXEMPTION_RESPONSE_HONOURED = "HONOURED";
    protected static final String LAST_EVENT_AUTHORISED = "AUTHORISED";

    @Override
    public boolean isExemptionHonoured(final ExemptionResponseInfo exemptionResponseInfo) {
        return Optional.ofNullable(exemptionResponseInfo)
            .map(ExemptionResponseInfo::getResult)
            .map(EXEMPTION_RESPONSE_HONOURED::equals)
            .orElse(false);
    }

    public boolean isRequestWithExemptionToBeRetriedWithoutExemption(final DirectAuthoriseServiceResponse response) {
        return !isExemptionHonoured(response.getExemptionResponseInfo()) && !isResponseRequiring3DsChallenge(response) && !isLastEventAuthorised(response);
    }

    private boolean isResponseRequiring3DsChallenge(final DirectAuthoriseServiceResponse response) {
        return response.getRequest3DInfo() != null;
    }

    private boolean isLastEventAuthorised(final DirectAuthoriseServiceResponse response) {
        final String lastEvent = Optional.ofNullable(response)
            .map(DirectAuthoriseServiceResponse::getPaymentReply)
            .map(PaymentReply::getAuthStatus)
            .map(Enum::name)
            .orElse(null);

        return LAST_EVENT_AUTHORISED.equals(lastEvent);
    }

}
