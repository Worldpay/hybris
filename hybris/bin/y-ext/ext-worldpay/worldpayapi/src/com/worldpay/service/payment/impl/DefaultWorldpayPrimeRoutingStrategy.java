package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPrimeRoutingService;
import com.worldpay.enums.PaymentAction;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.payment.WorldpayLevel23Strategy;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Default implementation of {@link WorldpayLevel23Strategy}.
 */
public class DefaultWorldpayPrimeRoutingStrategy implements WorldpayAdditionalDataRequestStrategy {

    protected final WorldpayPrimeRoutingService worldpayPrimeRoutingService;

    public DefaultWorldpayPrimeRoutingStrategy(final WorldpayPrimeRoutingService worldpayPrimeRoutingService) {
        this.worldpayPrimeRoutingService = worldpayPrimeRoutingService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        final CartModel cartModel = (CartModel) cart;
        if (worldpayPrimeRoutingService.isPrimeRoutingEnabled(cartModel)) {
            authoriseRequestParametersCreator
                .withPaymentDetailsAction(PaymentAction.SALE);
            worldpayPrimeRoutingService.setAuthorisedWithPrimeRoutingOnCart(cartModel);
        }
    }
}
