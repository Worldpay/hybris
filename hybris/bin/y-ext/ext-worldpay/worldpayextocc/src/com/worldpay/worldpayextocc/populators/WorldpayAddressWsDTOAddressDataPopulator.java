package com.worldpay.worldpayextocc.populators;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.Populator;

import java.util.Optional;

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
            .ifPresent(region -> addressData.setRegion(i18NFacade.getRegion(addressWsDTO.getCountry().getIsocode(), region.getIsocode())));
        addressData.setShippingAddress(Boolean.TRUE.equals(addressWsDTO.getShippingAddress()));
        addressData.setEmail(checkoutCustomerStrategy.getCurrentUserForCheckout().getContactEmail());
    }
}
