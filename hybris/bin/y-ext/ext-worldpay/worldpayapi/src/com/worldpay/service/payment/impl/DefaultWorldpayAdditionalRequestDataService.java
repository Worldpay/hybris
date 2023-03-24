package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.payment.WorldpayAdditionalRequestDataService;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAdditionalRequestDataService implements WorldpayAdditionalRequestDataService {

    protected final List<WorldpayAdditionalDataRequestStrategy> worldpayDirectDataRequestStrategies;
    protected final List<WorldpayAdditionalDataRequestStrategy> worldpayRedirectDataRequestStrategies;
    protected final WorldpayAdditionalDataRequestStrategy worldpayGuaranteedPaymentsStrategy;

    public DefaultWorldpayAdditionalRequestDataService(final List<WorldpayAdditionalDataRequestStrategy> worldpayDirectDataRequestStrategies,
                                                       final List<WorldpayAdditionalDataRequestStrategy> worldpayRedirectDataRequestStrategies,
                                                       final WorldpayAdditionalDataRequestStrategy worldpayGuaranteedPaymentsStrategy) {
        this.worldpayDirectDataRequestStrategies = worldpayDirectDataRequestStrategies;
        this.worldpayRedirectDataRequestStrategies = worldpayRedirectDataRequestStrategies;
        this.worldpayGuaranteedPaymentsStrategy = worldpayGuaranteedPaymentsStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateDirectRequestAdditionalData(final AbstractOrderModel cart,
                                                    final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                    final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        worldpayDirectDataRequestStrategies.forEach(strategy -> strategy.populateRequestWithAdditionalData(cart, worldpayAdditionalInfoData, authoriseRequestParametersCreator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRedirectRequestAdditionalData(final AbstractOrderModel cart,
                                                      final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                      final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        worldpayRedirectDataRequestStrategies.forEach(strategy -> strategy.populateRequestWithAdditionalData(cart, worldpayAdditionalInfoData, authoriseRequestParametersCreator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestGuaranteedPayments(final AbstractOrderModel cart,
                                                      final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                      final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        worldpayGuaranteedPaymentsStrategy.populateRequestWithAdditionalData(cart, worldpayAdditionalInfoData, authoriseRequestParametersCreator);
    }
}
