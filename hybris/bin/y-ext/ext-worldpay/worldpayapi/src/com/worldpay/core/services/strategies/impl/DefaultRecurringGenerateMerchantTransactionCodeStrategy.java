package com.worldpay.core.services.strategies.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Strategy to generate the WorldpayOrderCode to send to Worldpay. Identifies an order in Worldpay.
 */
public class DefaultRecurringGenerateMerchantTransactionCodeStrategy implements RecurringGenerateMerchantTransactionCodeStrategy {

    protected final ModelService modelService;
    protected final CartService cartService;

    public DefaultRecurringGenerateMerchantTransactionCodeStrategy(final ModelService modelService, final CartService cartService) {
        this.modelService = modelService;
        this.cartService = cartService;
    }

    @Override
    public String generateCode(final CartModel cartModel) {
        return internalGenerateCode(cartModel);
    }

    @Override
    public String generateCode(final AbstractOrderModel abstractOrderModel) {
        return internalGenerateCode(abstractOrderModel);
    }

    protected String internalGenerateCode(final AbstractOrderModel abstractOrderModel) {
        AbstractOrderModel order = abstractOrderModel;
        if (order == null) {
            order = cartService.getSessionCart();
        }
        final String worldpayOrderCode = order.getCode() + "-" + getTime();
        order.setWorldpayOrderCode(worldpayOrderCode);
        modelService.save(order);
        return worldpayOrderCode;
    }

    protected long getTime() {
        return System.currentTimeMillis();
    }
}
