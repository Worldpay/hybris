package com.worldpay.service.payment.impl;

import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import com.worldpay.service.payment.WorldpayLevel23Strategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Abstract implementation of {@link WorldpayLevel23Strategy}
 */
public abstract class AbstractWorldpayLevel23Strategy implements WorldpayLevel23Strategy {

    /**
     * Populates the customer reference. Override this method with real values based on business requirements
     */
    protected abstract void setCustomerReference(AbstractOrderModel order, Purchase purchase);

    /**
     * Populates the product description. Override this method with real values based on business requirements
     */
    protected abstract void setProductDescription(ProductModel product, Item item);

    /**
     * Populates the duty amount. Override this method with real values based on business requirements
     */
    protected abstract void setDutyAmount(AbstractOrderModel order, Purchase purchase);
}
