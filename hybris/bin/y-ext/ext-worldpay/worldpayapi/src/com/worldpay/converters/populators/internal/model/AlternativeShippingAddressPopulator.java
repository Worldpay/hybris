package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Address;
import com.worldpay.data.AlternativeShippingAddress;
import com.worldpay.enums.alternativeShippingAddress.ShippingMethod;
import com.worldpay.enums.alternativeShippingAddress.ShippingType;
import com.worldpay.internal.model.ShippingSummary;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class AlternativeShippingAddressPopulator implements Populator<AlternativeShippingAddress, com.worldpay.internal.model.AlternativeShippingAddress> {

    protected final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter;

    public AlternativeShippingAddressPopulator(final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter) {
        this.internalAddressConverter = internalAddressConverter;
    }

    @Override
    public void populate(final AlternativeShippingAddress source, final com.worldpay.internal.model.AlternativeShippingAddress target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final ShippingSummary shippingSummary = new ShippingSummary();

        Optional.ofNullable(source.getShippingMethod())
                .map(this::parseShippingMethod)
                .ifPresent(shippingSummary::setShippingMethod);
        Optional.ofNullable(source.getShippingType())
                .map(ShippingType::name)
                .map(String::toLowerCase).ifPresent(shippingSummary::setShippingType);
        Optional.ofNullable(source.getAddress())
                .map(internalAddressConverter::convert)
                .ifPresent(target::setAddress);
        target.setShippingSummary(shippingSummary);
    }

    private String parseShippingMethod(final ShippingMethod source) {
        switch (source) {
            case STORE_PICK_UP:
                return "store pick-up";
            case PICK_UP_POINT:
                return "pick-up point";
            case REGISTERED_BOX:
                return "registered box";
            case UNREGISTERED_BOX:
                return "unregistered box";
            default:
                return null;
        }
    }
}
