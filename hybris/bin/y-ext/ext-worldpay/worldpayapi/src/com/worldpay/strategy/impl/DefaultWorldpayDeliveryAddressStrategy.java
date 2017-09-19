package com.worldpay.strategy.impl;

import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.exceptions.ConfigurationException;

import java.util.List;

import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayDeliveryAddressStrategy implements WorldpayDeliveryAddressStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressModel getDeliveryAddress(final AbstractOrderModel abstractOrderModel) {
        if (abstractOrderModel.getDeliveryAddress() == null) {
            final List<AbstractOrderEntryModel> entries = abstractOrderModel.getEntries();
            for (final AbstractOrderEntryModel entry : entries) {
                if (entry.getDeliveryPointOfService() != null && entry.getDeliveryPointOfService().getAddress() != null) {
                    return entry.getDeliveryPointOfService().getAddress();
                }
            }
            throw new ConfigurationException(format("Checkout was attempted without a valid pickup or shipping address for cart with id [{0}]", abstractOrderModel.getCode()));
        } else {
            return abstractOrderModel.getDeliveryAddress();
        }
    }
}
