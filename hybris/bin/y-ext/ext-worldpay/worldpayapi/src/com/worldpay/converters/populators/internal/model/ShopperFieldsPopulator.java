package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.BirthDate;
import com.worldpay.internal.model.ShopperAddress;
import com.worldpay.data.Address;
import com.worldpay.data.Date;
import com.worldpay.data.ShopperFields;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.ShopperFields} with the information of a {@link ShopperFields}
 */
public class ShopperFieldsPopulator implements Populator<ShopperFields, com.worldpay.internal.model.ShopperFields> {

    protected final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter;
    protected final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter;

    public ShopperFieldsPopulator(final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter,
                                  final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter) {
        this.internalDateConverter = internalDateConverter;
        this.internalAddressConverter = internalAddressConverter;
    }

    /**
     * Populates the data from the {@link ShopperFields} to a {@link com.worldpay.internal.model.ShopperFields}
     *
     * @param source a {@link ShopperFields} from Worldpay
     * @param target a {@link com.worldpay.internal.model.ShopperFields} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final ShopperFields source,
                         final com.worldpay.internal.model.ShopperFields target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setShopperName(source.getShopperName());
        target.setShopperId(source.getShopperId());

        Optional.ofNullable(source.getBirthDate()).ifPresent(date -> {
            final BirthDate intBirthDateData = new BirthDate();
            intBirthDateData.setDate(internalDateConverter.convert(date));
            target.setBirthDate(intBirthDateData);
        });

        Optional.ofNullable(source.getShopperAddress()).ifPresent(address -> {
            final ShopperAddress intShopperAddress = new ShopperAddress();
            intShopperAddress.setAddress(internalAddressConverter.convert(source.getShopperAddress()));
            target.setShopperAddress(intShopperAddress);
        });
    }
}
