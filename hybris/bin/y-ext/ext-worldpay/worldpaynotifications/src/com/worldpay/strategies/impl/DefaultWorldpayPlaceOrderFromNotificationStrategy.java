package com.worldpay.strategies.impl;

import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.model.Amount;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.strategies.WorldpayPlaceOrderFromNotificationStrategy;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.ERROR_PLACING_ORDER;

/**
 * Implementation of the strategy to place order from notifications
 */
public class DefaultWorldpayPlaceOrderFromNotificationStrategy implements WorldpayPlaceOrderFromNotificationStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayPlaceOrderFromNotificationStrategy.class);

    private OrderModificationSerialiser orderModificationSerialiser;
    private WorldpayRedirectOrderService worldpayRedirectOrderService;
    private WorldpayOrderService worldpayOrderService;
    private ImpersonationService impersonationService;
    private CartService cartService;
    private CommerceCheckoutService commerceCheckoutService;
    private ModelService modelService;
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void placeOrderFromNotification(final WorldpayOrderModificationModel orderModificationModel, final CartModel cart) {
        final OrderNotificationMessage notificationMessage = orderModificationSerialiser.deserialise(orderModificationModel.getOrderNotificationMessage());
        final Amount amount = notificationMessage.getPaymentReply().getAmount();
        final ImpersonationContext context = new ImpersonationContext();
        context.setOrder(cart);
        context.setSite(cart.getSite());
        context.setUser(cart.getUser());
        impersonationService.executeInContext(context, (ImpersonationService.Executor<Void, ImpersonationService.Nothing>) () ->
                placeOrderInContext(orderModificationModel, cart, notificationMessage, amount)
        );
    }

    protected Void placeOrderInContext(final WorldpayOrderModificationModel orderModificationModel, final CartModel cart, final OrderNotificationMessage notificationMessage, final Amount amount) {
        try {
            cartService.setSessionCart(cart);
            worldpayRedirectOrderService.completeConfirmedRedirectAuthorise(worldpayOrderService.convertAmount(amount), notificationMessage.getMerchantCode(), cart);
            final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
            parameter.setEnableHooks(true);
            parameter.setCart(cart);
            final OrderModel orderModel = commerceCheckoutService.placeOrder(parameter).getOrder();
            if (orderModel != null) {
                cartService.removeSessionCart();
                modelService.refresh(orderModel);
                LOG.info(MessageFormat.format("Order placed: {0}", orderModel.getCode()));
            }
            worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModificationModel);
        } catch (final InvalidCartException e) {
            LOG.error(MessageFormat.format("There was an error while placing the order from cart [{0}]", cart.getCode()), e);
            worldpayOrderNotificationHandler.setDefectiveReason(orderModificationModel, ERROR_PLACING_ORDER);
            worldpayOrderNotificationHandler.setDefectiveModification(orderModificationModel, null, false);
        }
        return null;
    }

    @Required
    public void setWorldpayRedirectOrderService(final WorldpayRedirectOrderService worldpayRedirectOrderService) {
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
    }

    @Required
    public void setWorldpayOrderService(final WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }

    @Required
    public void setImpersonationService(final ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    @Required
    public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService) {
        this.commerceCheckoutService = commerceCheckoutService;
    }

    @Required
    public void setOrderModificationSerialiser(final OrderModificationSerialiser orderModificationSerialiser) {
        this.orderModificationSerialiser = orderModificationSerialiser;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setWorldpayOrderNotificationHandler(final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler) {
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
    }
}
