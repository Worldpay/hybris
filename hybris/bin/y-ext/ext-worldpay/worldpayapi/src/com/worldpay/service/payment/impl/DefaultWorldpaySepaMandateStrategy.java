package com.worldpay.service.payment.impl;

import com.worldpay.internal.model.SEPADIRECTDEBITSSL;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Collection;
import java.util.stream.Stream;

public class DefaultWorldpaySepaMandateStrategy implements WorldpayAdditionalDataRequestStrategy {

    private static final String MANDATE_TYPE = "ONE-OFF";

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {

        Stream.ofNullable(authoriseRequestParametersCreator.build().getIncludedPTs())
            .flatMap(Collection::stream)
            .filter(paymentType -> SEPADIRECTDEBITSSL.class.equals(paymentType.getModelClass()))
            .findAny()
            .ifPresent(paymentType -> authoriseRequestParametersCreator.withMandateType(MANDATE_TYPE));
    }
}
