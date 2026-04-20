package com.worldpay.worldpayextb2bocc.facades.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.facades.payment.direct.impl.DefaultWorldpayB2BDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;

/**
 * Implementation of the authorise operations that enables the Client Side Encryption with Worldpay
 */
@SuppressWarnings("java:S107")
public class DefaultWorldpayOCCB2BDirectOrderFacade extends DefaultWorldpayB2BDirectOrderFacade {

    public DefaultWorldpayOCCB2BDirectOrderFacade(final WorldpayDirectOrderService worldpayDirectOrderService,
                                                  final CartService cartService,
                                                  final AcceleratorCheckoutFacade acceleratorCheckoutFacade,
                                                  final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                                  final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade,
                                                  final WorldpayCartService worldpayCartService,
                                                  final B2BOrderService b2BOrderService,
                                                  final Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoPopulator) {

        super(worldpayDirectOrderService, cartService, acceleratorCheckoutFacade, worldpayPaymentInfoService,
                worldpayMerchantConfigDataFacade, worldpayCartService, b2BOrderService, apmPaymentInfoPopulator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleAuthorisedResponse(final DirectResponseData response) throws InvalidCartException {
        final OrderData orderData = acceleratorCheckoutFacade.placeOrder();
        response.setOrderData(orderData);
        response.setTransactionStatus(TransactionStatus.AUTHORISED);
    }

}
