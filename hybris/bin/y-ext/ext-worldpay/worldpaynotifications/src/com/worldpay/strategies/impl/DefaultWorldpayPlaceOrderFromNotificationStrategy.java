package com.worldpay.strategies.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.data.Amount;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.strategies.WorldpayPlaceOrderFromNotificationStrategy;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.ERROR_PLACING_ORDER;

/**
 * Implementation of the strategy to place order from notifications
 */
public class DefaultWorldpayPlaceOrderFromNotificationStrategy implements WorldpayPlaceOrderFromNotificationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayPlaceOrderFromNotificationStrategy.class);

    private OrderNotificationService orderNotificationService;
    private WorldpayRedirectOrderService worldpayRedirectOrderService;
    private WorldpayOrderService worldpayOrderService;
    private ImpersonationService impersonationService;
    private CartService cartService;
    private CommerceCheckoutService commerceCheckoutService;
    private ModelService modelService;

    public DefaultWorldpayPlaceOrderFromNotificationStrategy(final OrderNotificationService orderNotificationService,
                                                             final WorldpayRedirectOrderService worldpayRedirectOrderService,
                                                             final WorldpayOrderService worldpayOrderService,
                                                             final ImpersonationService impersonationService,
                                                             final CartService cartService,
                                                             final CommerceCheckoutService commerceCheckoutService,
                                                             final ModelService modelService) {
        this.orderNotificationService = orderNotificationService;
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
        this.worldpayOrderService = worldpayOrderService;
        this.impersonationService = impersonationService;
        this.cartService = cartService;
        this.commerceCheckoutService = commerceCheckoutService;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void placeOrderFromNotification(final WorldpayOrderModificationModel orderModificationModel, final CartModel cart) {
        final OrderNotificationMessage notificationMessage = orderNotificationService.deserialiseNotification(orderModificationModel.getOrderNotificationMessage());
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
                LOG.info("Order placed: {}", orderModel.getCode());
            }
            orderNotificationService.setNonDefectiveAndProcessed(orderModificationModel);
        } catch (final InvalidCartException e) {
            LOG.error("There was an error while placing the order from cart [{}]", cart.getCode(), e);
            orderNotificationService.setDefectiveReason(orderModificationModel, ERROR_PLACING_ORDER);
            orderNotificationService.setDefectiveModification(orderModificationModel, null, false);
        }
        return null;
    }
}
