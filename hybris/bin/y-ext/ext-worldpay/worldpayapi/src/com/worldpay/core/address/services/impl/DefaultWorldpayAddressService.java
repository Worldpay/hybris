package com.worldpay.core.address.services.impl;

import com.worldpay.core.address.services.WorldpayAddressService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.impl.DefaultAddressService;

/**
 * Default implementation of the {@link WorldpayAddressService}
 */
public class DefaultWorldpayAddressService extends DefaultAddressService implements WorldpayAddressService {

    protected final ModelService modelService;

    public DefaultWorldpayAddressService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCartPaymentAddress(final CartModel cartModel, final AddressModel addressModel) {
        cartModel.setPaymentAddress(addressModel);
        modelService.save(cartModel);
    }
}
