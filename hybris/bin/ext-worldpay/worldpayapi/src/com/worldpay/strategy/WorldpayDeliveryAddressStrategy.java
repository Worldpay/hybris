package com.worldpay.strategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Exposes methods to select a delivery address.
 */
public interface WorldpayDeliveryAddressStrategy {

    /**
     * Method for getting the address that will act as main delivery address for an abstract order
     *
     * @param abstractOrderModel
     * @return The delivery address best representing the given abstract order.
     */
    AddressModel getDeliveryAddress(final AbstractOrderModel abstractOrderModel);
}
