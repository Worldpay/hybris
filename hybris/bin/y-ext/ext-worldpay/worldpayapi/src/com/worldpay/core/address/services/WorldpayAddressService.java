package com.worldpay.core.address.services;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.AddressService;

/**
 * Worldpay AddressService to handle operations on Addresses
 */
public interface WorldpayAddressService extends AddressService {

    /**
     * Sets the billing address in the given cart model
     *
     * @param cartModel    the given cart model
     * @param addressModel the address to save
     */
    void setCartPaymentAddress(CartModel cartModel, AddressModel addressModel);
}
