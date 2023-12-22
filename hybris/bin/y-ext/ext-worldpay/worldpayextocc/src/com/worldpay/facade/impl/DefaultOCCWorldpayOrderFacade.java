package com.worldpay.facade.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facade.OCCWorldpayOrderFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.hosted.WorldpayHOPNoReturnParamsStrategy;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.hop.WorldpayOrderCodeVerificationService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultOCCWorldpayOrderFacade extends DefaultOrderFacade implements OCCWorldpayOrderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultOCCWorldpayOrderFacade.class);
    private static final String FAILED_TO_PLACE_ORDER = "Failed to place Order";

    private final WorldpayHOPNoReturnParamsStrategy worldpayHOPNoReturnParamsStrategy;
    private final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private final Converter<AbstractOrderModel, OrderData> orderConverter;
    private final WorldpayCheckoutFacadeDecorator worldpayCheckoutFacadeDecorator;
    private final Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter;
    private final WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    private final  WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationService;

    public DefaultOCCWorldpayOrderFacade(final WorldpayHOPNoReturnParamsStrategy worldpayHOPNoReturnParamsStrategy,
                                         final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                         final Converter<AbstractOrderModel, OrderData> orderConverter,
                                         final WorldpayCheckoutFacadeDecorator worldpayCheckoutFacadeDecorator,
                                         final Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter,
                                         final WorldpayHostedOrderFacade worldpayHostedOrderFacade,
                                         final WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationService) {
        this.worldpayHOPNoReturnParamsStrategy = worldpayHOPNoReturnParamsStrategy;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.orderConverter = orderConverter;
        this.worldpayCheckoutFacadeDecorator = worldpayCheckoutFacadeDecorator;
        this.redirectAuthoriseResultConverter = redirectAuthoriseResultConverter;
        this.worldpayHostedOrderFacade = worldpayHostedOrderFacade;
        this.worldpayOrderCodeVerificationService = worldpayOrderCodeVerificationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderData handleHopResponseWithoutPaymentStatus(final RedirectAuthoriseResult result) throws WorldpayException {
        if (!worldpayCheckoutFacadeDecorator.hasValidCart()) {
            return findOrder(result);
        }
        final RedirectAuthoriseResult redirectAuthoriseResult = worldpayHOPNoReturnParamsStrategy.authoriseCart();
        worldpayHostedOrderFacade.completeRedirectAuthorise(redirectAuthoriseResult);
        return placeOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderData handleHopResponseWithPaymentStatus(final RedirectAuthoriseResult result) throws WorldpayException {
        if (!worldpayCheckoutFacadeDecorator.hasValidCart()) {
            return findOrder(result);
        }
        worldpayHostedOrderFacade.completeRedirectAuthorise(result);
        return placeOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedirectAuthoriseResult getRedirectAuthoriseResult(final Map<String, String> requestParameterMap) {
        return redirectAuthoriseResultConverter.convert(requestParameterMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidEncryptedOrderCode(final String orderCode) {
        return worldpayOrderCodeVerificationService.isValidEncryptedOrderCode(orderCode);
    }

    /**
     * Places the order
     * @return the order data
     * @throws WorldpayException
     */
    protected OrderData placeOrder() throws WorldpayException {
        try {
            return worldpayCheckoutFacadeDecorator.placeOrder();
        } catch (final InvalidCartException e) {
            LOG.error(FAILED_TO_PLACE_ORDER, e);
            throw new WorldpayException(FAILED_TO_PLACE_ORDER);
        }
    }

    /**
     * Find the order from the order code into the response
     * @param response the response
     * @return the order data
     * @throws WorldpayException
     */
    protected OrderData findOrder(final RedirectAuthoriseResult response) throws WorldpayException {
        final PaymentTransactionModel paymentTransactionFromCode = worldpayPaymentTransactionService
                .getPaymentTransactionFromCode(response.getOrderCode());

        if (paymentTransactionFromCode != null && paymentTransactionFromCode.getOrder() instanceof OrderModel) {
            return orderConverter.convert(paymentTransactionFromCode.getOrder());
        }

        LOG.error(FAILED_TO_PLACE_ORDER);
        throw new WorldpayException(FAILED_TO_PLACE_ORDER);
    }

    public OrderData findOrderByCodeAndUserId(final String orderCode, final String userId) {
        final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
        OrderModel order;

        try {
            order = getCustomerAccountService().getOrderForCode(orderCode, baseStore);
        } catch (final Exception e) {
            throw new UnknownIdentifierException("Order with orderGUID " + orderCode + " not found in current BaseStore");
        }

        if (!order.getUser().getUid().equals(userId)) {
            throw new UnknownIdentifierException("Order with orderGUID " + orderCode + " not found for user " + userId);
        }

        return orderConverter.convert(order);
    }

}
