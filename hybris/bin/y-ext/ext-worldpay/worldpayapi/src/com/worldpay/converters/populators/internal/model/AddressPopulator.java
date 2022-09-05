package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.*;
import com.worldpay.data.Address;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Address} with the information of a {@link Address}
 */
public class AddressPopulator implements Populator<Address, com.worldpay.internal.model.Address> {

    /**
     * Populates the data from the {@link Address} to a {@link com.worldpay.internal.model.Address}
     *
     * @param source a {@link Address} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Address} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Address source, final com.worldpay.internal.model.Address target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPostalCode(source.getPostalCode());
        target.setCity(source.getCity());
        target.setState(source.getState());

        Optional.ofNullable(source.getCountryCode()).ifPresent(country -> {
            CountryCode countryCode = new CountryCode();
            countryCode.setvalue(source.getCountryCode());
            target.setCountryCode(countryCode);
        });

        final List<Object> addressDetails = target.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();

        Optional.ofNullable(source.getStreet()).ifPresent(street -> {
            final Street intStreet = new Street();
            intStreet.setvalue(street);
            addressDetails.add(intStreet);
        });

        Optional.ofNullable(source.getHouseName()).ifPresent(houseName -> {
            final HouseName intHouseName = new HouseName();
            intHouseName.setvalue(houseName);
            addressDetails.add(intHouseName);
        });

        Optional.ofNullable(source.getHouseNumber()).ifPresent(houseNumber -> {
            final HouseNumber intHouseNumber = new HouseNumber();
            intHouseNumber.setvalue(houseNumber);
            addressDetails.add(intHouseNumber);
        });

        Optional.ofNullable(source.getHouseNumberExtension()).ifPresent(houseNumberExtension -> {
            final HouseNumberExtension intHouseNumberExt = new HouseNumberExtension();
            intHouseNumberExt.setvalue(houseNumberExtension);
            addressDetails.add(intHouseNumberExt);
        });

        Optional.ofNullable(source.getAddress1()).ifPresent(address1 -> {
            final Address1 intAddress1 = new Address1();
            intAddress1.setvalue(address1);
            addressDetails.add(intAddress1);
        });

        Optional.ofNullable(source.getAddress2()).ifPresent(address2 -> {
            final Address2 intAddress2 = new Address2();
            intAddress2.setvalue(address2);
            addressDetails.add(intAddress2);
        });

        Optional.ofNullable(source.getAddress3()).ifPresent(address3 -> {
            final Address3 intAddress3 = new Address3();
            intAddress3.setvalue(address3);
            addressDetails.add(intAddress3);
        });

        if (StringUtils.isNotBlank(source.getTelephoneNumber())) {
            target.setTelephoneNumber(source.getTelephoneNumber());
        }
    }
}
