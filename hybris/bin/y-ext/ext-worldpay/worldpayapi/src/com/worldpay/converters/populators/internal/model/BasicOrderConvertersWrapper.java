package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Address;
import com.worldpay.data.Amount;
import com.worldpay.data.Session;
import com.worldpay.data.Shopper;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class BasicOrderConvertersWrapper {

    protected final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter;
    protected final Converter<Shopper, com.worldpay.internal.model.Shopper> internalShopperConverter;
    protected final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter;
    protected final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter;

    public BasicOrderConvertersWrapper(final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter,
                                       final Converter<Shopper, com.worldpay.internal.model.Shopper> internalShopperConverter,
                                       final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter,
                                       final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter) {
        this.internalAmountConverter = internalAmountConverter;
        this.internalShopperConverter = internalShopperConverter;
        this.internalAddressConverter = internalAddressConverter;
        this.internalSessionConverter = internalSessionConverter;
    }
}
