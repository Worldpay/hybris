package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.*;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class BasicOrderConvertersWrapper {

    protected final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter;
    protected final Converter<Shopper, com.worldpay.internal.model.Shopper> internalShopperConverter;
    protected final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter;
    protected final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter;

    protected final Converter<AlternativeShippingAddress, com.worldpay.internal.model.AlternativeShippingAddress> internalAlternativeShippingAddressConverter;

    public BasicOrderConvertersWrapper(final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter,
                                       final Converter<Shopper, com.worldpay.internal.model.Shopper> internalShopperConverter,
                                       final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter,
                                       final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter,
                                       final Converter<AlternativeShippingAddress, com.worldpay.internal.model.AlternativeShippingAddress> internalAlternativeShippingAddressConverter) {
        this.internalAmountConverter = internalAmountConverter;
        this.internalShopperConverter = internalShopperConverter;
        this.internalAddressConverter = internalAddressConverter;
        this.internalSessionConverter = internalSessionConverter;
        this.internalAlternativeShippingAddressConverter = internalAlternativeShippingAddressConverter;
    }
}
