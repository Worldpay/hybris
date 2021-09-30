package com.worldpay.core.checkout.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;

/**
 * Provides specific behaviour to the commerce checkout service to set the merchant transaction code in the same style
 * as other places that use worldpay
 */
public class DefaultWorldpayCheckoutService implements WorldpayCheckoutService {

    protected final ModelService modelService;
    protected final AddressService addressService;

    public DefaultWorldpayCheckoutService(final ModelService modelService, final AddressService addressService) {
        this.modelService = modelService;
        this.addressService = addressService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentAddress(final CartModel cartModel, final AddressModel addressModel) {
        cartModel.setPaymentAddress(addressModel);
        modelService.save(cartModel);

        modelService.refresh(cartModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShippingAndPaymentAddress(final CartModel cartModel, final AddressModel addressModel) {
        cartModel.setDeliveryAddress(addressModel);
        final AddressModel paymentAddress = addressService.cloneAddress(addressModel);
        cartModel.setPaymentAddress(paymentAddress);
        modelService.saveAll(paymentAddress, cartModel);

        modelService.refresh(cartModel);
    }
}
