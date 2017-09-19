package com.worldpay.core.services.strategies.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy to generate the WorldpayOrderCode to send to Worldpay. Identifies an order in Worldpay.
 */
public class DefaultRecurringGenerateMerchantTransactionCodeStrategy implements RecurringGenerateMerchantTransactionCodeStrategy {

    private ModelService modelService;
    private CartService cartService;

    @Override
    public String generateCode(final CartModel cartModel) {
        return internalGenerateCode(cartModel);
    }

    @Override
    public String generateCode(final AbstractOrderModel abstractOrderModel) {
        return internalGenerateCode(abstractOrderModel);
    }

    protected String internalGenerateCode(final AbstractOrderModel abstractOrderModel) {
        AbstractOrderModel parameterOrder = abstractOrderModel;
        if (parameterOrder == null) {
            parameterOrder = cartService.getSessionCart();
        }
        final String worldpayOrderCode = parameterOrder.getCode() + "-" + getTime();
        parameterOrder.setWorldpayOrderCode(worldpayOrderCode);
        modelService.save(parameterOrder);
        return worldpayOrderCode;
    }

    protected long getTime() {
        return System.currentTimeMillis();
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
