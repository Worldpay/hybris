package com.worldpay.service.payment.impl;

import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import com.worldpay.service.payment.WorldpayLevel23DataValidator;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Extension of {@link DefaultWorldpayLevel23Strategy} in order to allow the testing of Level 2/3 functionality
 */
public class DefaultTestWorldpayLevel23Strategy extends DefaultWorldpayLevel23Strategy {

    public DefaultTestWorldpayLevel23Strategy(final WorldpayMerchantStrategy worldpayMerchantStrategy,
                                              final WorldpayOrderService worldpayOrderService,
                                              final WorldpayLevel23DataValidator worldpayLevel23DataValidator) {
        super(worldpayMerchantStrategy, worldpayOrderService, worldpayLevel23DataValidator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setCustomerReference(final AbstractOrderModel order, final Purchase purchase) {
        purchase.setCustomerReference(RandomStringUtils.randomAlphanumeric(17));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setProductDescription(final ProductModel product, final Item item) {
        final String name = product.getName();
        final String productName = name.length() > 26 ? name.substring(0, 26) : name;
        item.setDescription(productName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDutyAmount(final AbstractOrderModel order, final Purchase purchase) {
        purchase.setDutyAmount(worldpayOrderService.createAmount(order.getCurrency(), 0));
    }
}
