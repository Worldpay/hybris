package com.worldpay.converters.populators;

import com.worldpay.data.Address;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.text.StringEscapeUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills data from an {@AddressModel } to a {@link Address}
 */
public class WorldpayAddressPopulator implements Populator<AddressModel, Address> {

    /**
     * Fills the necessary fields from an {@AddressModel} into a {@Address}
     *
     * @param source an {@AddressModel} that contains the information
     * @param target an {@Address} that receives the information
     */
    @Override
    public void populate(final AddressModel source, final Address target) {
        validateParameterNotNull(source, "Parameter source (AddressModel) cannot be null");

        target.setAddress1(StringEscapeUtils.escapeXml10(source.getLine1()));
        target.setAddress2(StringEscapeUtils.escapeXml10(source.getLine2()));
        target.setCity(StringEscapeUtils.escapeXml10(source.getTown()));

        if (source.getCountry() != null) {
            target.setCountryCode(source.getCountry().getIsocode());
        }

        target.setFirstName(StringEscapeUtils.escapeXml10(source.getFirstname()));
        target.setLastName(StringEscapeUtils.escapeXml10(source.getLastname()));
        target.setPostalCode(StringEscapeUtils.escapeXml10(source.getPostalcode()));
        target.setTelephoneNumber(source.getPhone1());

        if (source.getRegion() != null) {
            target.setState(source.getRegion().getIsocode());
        }
    }
}
