package com.worldpay.facades.payment.direct.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.facades.payment.direct.WorldpayB2BDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Implementation of the authorise operations that enables the Client Side Encryption with Worldpay
 */
@SuppressWarnings("java:S107")
public class DefaultWorldpayB2BDirectOrderFacade extends DefaultWorldpayDirectOrderFacade implements WorldpayB2BDirectOrderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayB2BDirectOrderFacade.class);

    private final B2BOrderService b2BOrderService;

    public DefaultWorldpayB2BDirectOrderFacade(final WorldpayDirectOrderService worldpayDirectOrderService,
                                               final CartService cartService,
                                               final AcceleratorCheckoutFacade acceleratorCheckoutFacade,
                                               final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                               final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade,
                                               final WorldpayCartService worldpayCartService,
                                               final B2BOrderService b2BOrderService,
                                               final Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoPopulator) {
        super(worldpayDirectOrderService, cartService, acceleratorCheckoutFacade, worldpayPaymentInfoService, worldpayMerchantConfigDataFacade, worldpayCartService, apmPaymentInfoPopulator);
        this.b2BOrderService = b2BOrderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseRecurringPayment(final String orderCode,
                                                        final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        final AbstractOrderModel abstractOrderModel = b2BOrderService.getOrderForCode(orderCode);
        return internalAuthoriseRecurringPayment(abstractOrderModel, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authorise3DSecureOnOrder(final String orderCode,
                                                       final String paResponse,
                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        final OrderModel orderModel = b2BOrderService.getOrderForCode(orderCode);
        return internalAuthorise3DSecure(orderModel, paResponse, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleAuthorisedResponse(final DirectResponseData response) throws InvalidCartException {
        response.setTransactionStatus(TransactionStatus.AUTHORISED);
    }

    @Override
    protected void handleACHAuthorisedResponse(final DirectResponseData response) throws InvalidCartException {
        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setTermsCheck(Boolean.TRUE);
        final OrderData orderData =  getB2BCheckoutFacade().placeOrder(placeOrderData);
        response.setOrderData(orderData);
        response.setTransactionStatus(TransactionStatus.AUTHORISED);
    }

    @Override
    public DirectResponseData authoriseACHDirectDebit(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final ACHDirectDebitAdditionalAuthInfo additionalAuthInfo) throws WorldpayException, InvalidCartException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseACHDirectDebit(cart, additionalAuthInfo, worldpayAdditionalInfoData);
            return handleACHDirectDebitResponse(serviceResponse, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error(THERE_IS_NO_CONFIGURATION);
            throw e;
        } catch (final InvalidCartException e) {
            throw new InvalidCartException(MessageFormat.format("There was an error placing the order for cart [{0}]", cart.getCode()));
        }
    }

    @Override
    protected DirectResponseData handleACHDirectDebitResponse(final DirectAuthoriseServiceResponse serviceResponse, final CartModel abstractOrderModel)
            throws WorldpayException, InvalidCartException {
        if (shouldProcessACHResponse(serviceResponse)) {
            return processACHDirectDebitDirectResponse(serviceResponse, abstractOrderModel);
        } else {
            return handleErrorOnServiceResponse(serviceResponse);
        }
    }

    protected CheckoutFacade getB2BCheckoutFacade() {
        return (CheckoutFacade) this.acceleratorCheckoutFacade;
    }
}
