package com.worldpay.facades.order.impl;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.ApplePayLineItem;
import com.worldpay.data.ApplePayPaymentContact;
import com.worldpay.data.ApplePayPaymentRequest;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;

/**
 * Worldpay apple pay checkout facade to ensure Worldpay applepay details are included in correct place
 */
public class DefaultWorldpayApplePayPaymentCheckoutFacade implements WorldpayApplePayPaymentCheckoutFacade {
    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayApplePayPaymentCheckoutFacade.class);
    private static final String TOTAL_LINE_ITEM_TYPE = "final";
    private static final String REQUIRED_POSTAL_ADDR = "postalAddress";

    protected final Converter<ApplePayConfigData, ValidateMerchantRequestDTO> applePayConfigDataToValidateMerchantRequestDTOPopulatingConverter;
    protected final WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    protected final I18NFacade i18NFacade;
    protected final CheckoutCustomerStrategy checkoutCustomerStrategy;
    protected final UserFacade userFacade;
    protected final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    public DefaultWorldpayApplePayPaymentCheckoutFacade(final Converter<ApplePayConfigData, ValidateMerchantRequestDTO> applePayConfigDataToValidateMerchantRequestDTOPopulatingConverter,
                                                        final WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade,
                                                        final I18NFacade i18NFacade,
                                                        final CheckoutCustomerStrategy checkoutCustomerStrategy,
                                                        final UserFacade userFacade,
                                                        final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade) {
        this.applePayConfigDataToValidateMerchantRequestDTOPopulatingConverter = applePayConfigDataToValidateMerchantRequestDTOPopulatingConverter;
        this.worldpayPaymentCheckoutFacade = worldpayPaymentCheckoutFacade;
        this.i18NFacade = i18NFacade;
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
        this.userFacade = userFacade;
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
    }

    @Override
    public void saveBillingAddresses(final ApplePayPaymentContact billingContact) {
        final AddressData addressData = new AddressData();
        addressData.setBillingAddress(true);
        addressData.setFirstName(billingContact.getGivenName());
        addressData.setLastName(billingContact.getFamilyName());

        if (billingContact.getAddressLines().size() >= 1) {
            addressData.setLine1(IterableUtils.get(billingContact.getAddressLines(), 0));
        }
        if (billingContact.getAddressLines().size() > 1) {
            addressData.setLine2(IterableUtils.get(billingContact.getAddressLines(), 1));
        }
        addressData.setTown(billingContact.getAdministrativeArea());
        addressData.setPostalCode(billingContact.getPostalCode());
        addressData.setCountry(i18NFacade.getCountryForIsocode(StringUtils.upperCase(billingContact.getCountryCode())));
        addressData.setEmail(checkoutCustomerStrategy.getCurrentUserForCheckout().getContactEmail());
        addressData.setTown(billingContact.getLocality());

        setRegion(addressData, billingContact);

        userFacade.addAddress(addressData);
        worldpayPaymentCheckoutFacade.setBillingDetails(addressData);
    }

    @Override
    public void setRegion(final AddressData addressData, final ApplePayPaymentContact address) {
        final String administrativeArea = address.getAdministrativeArea();

        if (StringUtils.isNotEmpty(administrativeArea)) {
            final String countryIsoCode = address.getCountryCode();
            try {
                addressData.setRegion(i18NFacade.getRegion(countryIsoCode, administrativeArea));
            } catch (final UnknownIdentifierException e) {
                LOG.debug("Failed to determine region from country {} and region code {}", countryIsoCode, administrativeArea, e);
            }
        }
    }

    @Override
    public ValidateMerchantRequestDTO getValidateMerchantRequestDTO() {
        final ApplePayConfigData applePaySettings = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getApplePaySettings();
        return applePayConfigDataToValidateMerchantRequestDTOPopulatingConverter.convert(applePaySettings);
    }

    @Override
    public ApplePayPaymentRequest getApplePayPaymentRequest(final CartData sessionCart) {
        final WorldpayMerchantConfigData merchantConfig = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();
        final ApplePayConfigData applePaySettings = merchantConfig.getApplePaySettings();

        final PriceData totalPrice = sessionCart.getTotalPrice();
        final ApplePayLineItem total = new ApplePayLineItem();
        total.setType(TOTAL_LINE_ITEM_TYPE);
        total.setLabel(applePaySettings.getMerchantName());
        total.setAmount(totalPrice.getValue().toString());

        final ApplePayPaymentRequest paymentRequest = new ApplePayPaymentRequest();
        paymentRequest.setTotal(total);
        paymentRequest.setMerchantCapabilities(applePaySettings.getMerchantCapabilities());
        paymentRequest.setSupportedNetworks(applePaySettings.getSupportedNetworks());
        paymentRequest.setCurrencyCode(totalPrice.getCurrencyIso());
        paymentRequest.setCountryCode(applePaySettings.getCountryCode());
        paymentRequest.setRequiredBillingContactFields(List.of(REQUIRED_POSTAL_ADDR));

        return paymentRequest;
    }

}
