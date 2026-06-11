package com.worldpay.core.checkout.attribute;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhandler.DynamicAttributesOrderStatusDisplayByMap;


/**
 * Dynamic attribute handler for the worldpay order status display
 */
public class WorldpayOrderStatusDisplayDynamicAttributeHandler extends DynamicAttributesOrderStatusDisplayByMap {

    protected static final String APM_OPEN = "APM_OPEN";

    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public WorldpayOrderStatusDisplayDynamicAttributeHandler(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(final OrderModel order) {
        String result;

        if (order != null && worldpayPaymentTransactionService.isAnyPaymentTransactionApmOpenForOrder(order)) {
            result = getStatusDisplayMap().get(APM_OPEN);
        } else {
            result = invokeSuperGet(order);
        }

        return result;
    }

    protected String invokeSuperGet(final OrderModel order) {
        return super.get(order);
    }

}
