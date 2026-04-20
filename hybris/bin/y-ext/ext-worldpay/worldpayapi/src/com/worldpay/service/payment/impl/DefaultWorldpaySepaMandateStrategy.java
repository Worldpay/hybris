package com.worldpay.service.payment.impl;

import com.worldpay.data.token.TokenRequest;
import com.worldpay.internal.model.SEPADIRECTDEBITSSL;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Collection;
import java.util.stream.Stream;

public class DefaultWorldpaySepaMandateStrategy implements WorldpayAdditionalDataRequestStrategy {

    private static final String MANDATE_ONE_OFF = "ONE-OFF";
    private static final String MANDATE_RECURRING = "RECURRING";

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {

        final AuthoriseRequestParameters parameters = authoriseRequestParametersCreator.build();

        Stream.ofNullable(parameters.getIncludedPTs())
                .flatMap(Collection::stream)
                .filter(paymentType -> SEPADIRECTDEBITSSL.class.equals(paymentType.getModelClass()))
                .findAny()
                .ifPresent(paymentType -> addSepaPayment(parameters.getTokenRequest(), authoriseRequestParametersCreator));

    }

    protected void addSepaPayment(final TokenRequest tokenRequest, final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {

        if (tokenRequest != null) {
            authoriseRequestParametersCreator
                    .withMandateType(MANDATE_RECURRING);
        } else {
            authoriseRequestParametersCreator
                    .withMandateType(MANDATE_ONE_OFF);
        }
    }
}
