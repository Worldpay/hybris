package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.AuthenticatedShopperID;
import com.worldpay.data.Browser;
import com.worldpay.data.Session;
import com.worldpay.data.Shopper;
import com.worldpay.data.payment.PayAsOrder;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PayAsOrder} with the information of a {@link PayAsOrder}.
 */
public class ShopperPopulator implements Populator<Shopper, com.worldpay.internal.model.Shopper> {

    protected final Converter<Browser, com.worldpay.internal.model.Browser> internalBrowserConverter;
    protected final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter;

    public ShopperPopulator(final Converter<Browser, com.worldpay.internal.model.Browser> internalBrowserConverter, final Converter<Session, com.worldpay.internal.model.Session> internalSessionConverter) {
        this.internalBrowserConverter = internalBrowserConverter;
        this.internalSessionConverter = internalSessionConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final Shopper source, final com.worldpay.internal.model.Shopper target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getShopperEmailAddress())
            .ifPresent(target::setShopperEmailAddress);

        Optional.ofNullable(source.getAuthenticatedShopperID()).ifPresent(authenticatedShopperID -> {
            final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
            intAuthenticatedShopperID.setvalue(authenticatedShopperID);
            target.setAuthenticatedShopperID(intAuthenticatedShopperID);
        });

        Optional.ofNullable(source.getBrowser())
            .map(internalBrowserConverter::convert)
            .ifPresent(target::setBrowser);

        Optional.ofNullable(source.getSession())
            .map(internalSessionConverter::convert)
            .ifPresent(target::setSession);
    }
}
