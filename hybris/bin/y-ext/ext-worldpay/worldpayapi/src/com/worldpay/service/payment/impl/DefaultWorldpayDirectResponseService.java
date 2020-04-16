package com.worldpay.service.payment.impl;

import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.payment.WorldpayDirectResponseService;

import static com.worldpay.payment.TransactionStatus.*;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayDirectResponseService implements WorldpayDirectResponseService {
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAuthorised(final DirectResponseData directResponseData) {
        return AUTHORISED.equals(directResponseData.getTransactionStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isCancelled(final DirectResponseData directResponseData) {
        return CANCELLED == directResponseData.getTransactionStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean is3DSecureLegacyFlow(final DirectResponseData directResponseData) {
        return isAuthenticationRequired(directResponseData) && directResponseData.getIssuerURL() != null && directResponseData.getPaRequest() != null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean is3DSecureFlexFlow(final DirectResponseData directResponseData) {
        return isAuthenticationRequired(directResponseData) && areResponseFlexMandatoryAttributesPopulated(directResponseData);
    }

    private boolean areResponseFlexMandatoryAttributesPopulated(final DirectResponseData directResponseData) {
        return directResponseData.getIssuerURL() != null
                && directResponseData.getIssuerPayload() != null && directResponseData.getMajor3DSVersion() != null && directResponseData.getTransactionId3DS() != null;
    }

    private boolean isAuthenticationRequired(final DirectResponseData directResponseData) {
        return AUTHENTICATION_REQUIRED == directResponseData.getTransactionStatus();
    }
}
