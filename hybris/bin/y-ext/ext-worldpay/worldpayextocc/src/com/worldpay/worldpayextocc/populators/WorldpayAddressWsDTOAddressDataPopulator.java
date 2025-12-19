package com.worldpay.worldpayextocc.populators;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.Populator;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
/**
 * Populates country, region and shipping address from AddressWsDTO
 */
public class WorldpayAddressWsDTOAddressDataPopulator implements Populator<AddressWsDTO, AddressData> {
    private final I18NFacade i18NFacade;
    private final CheckoutCustomerStrategy checkoutCustomerStrategy;

    public WorldpayAddressWsDTOAddressDataPopulator(final I18NFacade i18NFacade, final CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.i18NFacade = i18NFacade;
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

    @Override
    public void populate(final AddressWsDTO addressWsDTO, final AddressData addressData) {
        addressData.setCountry(i18NFacade.getCountryForIsocode(addressWsDTO.getCountry().getIsocode()));

        Optional.ofNullable(addressWsDTO.getRegion())
                .ifPresent(region -> addressData.setRegion(findRegion(addressWsDTO)));

        addressData.setShippingAddress(Boolean.TRUE.equals(addressWsDTO.getShippingAddress()));
        addressData.setBillingAddress(Boolean.TRUE.equals(addressWsDTO.getBillingAddress()));
        addressData.setEmail(checkoutCustomerStrategy.getCurrentUserForCheckout().getContactEmail());
    }

    protected RegionData findRegion(final AddressWsDTO addressWsDTO) {
        final String countryIso = addressWsDTO.getCountry().getIsocode();
        final String regionIso = addressWsDTO.getRegion().getIsocode();
        final String regionIsoShort = addressWsDTO.getRegion().getIsocodeShort();

        return i18NFacade.getRegionsForCountryIso(countryIso).stream()
                .filter(regionData -> isMatchingRegion(regionData, regionIso, regionIsoShort))
                .findFirst()
                .orElse(null);
    }

    protected boolean isMatchingRegion(final RegionData regionData, final String regionIso, final String regionIsoShort) {
        if (StringUtils.isNotBlank(regionIso)) {
            return regionData.getIsocode().equals(regionIso);
        } else if (StringUtils.isNotBlank(regionIsoShort)) {
            return regionData.getIsocodeShort().equals(regionIsoShort);
        }
        return false;
    }
}
