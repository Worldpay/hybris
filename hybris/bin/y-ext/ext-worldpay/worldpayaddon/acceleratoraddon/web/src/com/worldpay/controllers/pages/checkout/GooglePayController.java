package com.worldpay.controllers.pages.checkout;

import com.worldpay.data.GooglePayAddressData;
import com.worldpay.data.GooglePayAuthorisationRequest;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.MessageFormat;

@RestController
@RequestMapping(value = "/checkout/multi/worldpay/googlepay")
public class GooglePayController extends AbstractCheckoutController {

    private static final Logger LOG = Logger.getLogger(GooglePayController.class);

    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    @Resource
    private UserFacade userFacade;
    @Resource
    private I18NFacade i18NFacade;

    @RequireHardLogIn
    @PostMapping(value = "/authorise-order", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public DirectResponseData authoriseOrder(@RequestBody final GooglePayAuthorisationRequest authorisationRequest) throws WorldpayException, InvalidCartException {
        saveBillingAddresses(authorisationRequest.getBillingAddress());

        return worldpayDirectOrderFacade.authoriseGooglePayDirect(authorisationRequest.getToken());
    }

    protected void saveBillingAddresses(final GooglePayAddressData address) {
        final AddressData addressData = new AddressData();
        addressData.setBillingAddress(true);
        addressData.setFirstName(address.getName());
        addressData.setLine1(address.getAddress1());
        addressData.setLine2(StringUtils.join(new String[]{address.getAddress2(), address.getAddress3()}, " ").trim());
        addressData.setTown(address.getLocality());
        addressData.setPostalCode(address.getPostalCode());
        addressData.setCountry(i18NFacade.getCountryForIsocode(address.getCountryCode()));
        addressData.setEmail(checkoutCustomerStrategy.getCurrentUserForCheckout().getContactEmail());

        setRegion(addressData, address);

        userFacade.addAddress(addressData);
        worldpayPaymentCheckoutFacade.setBillingDetails(addressData);
    }

    protected void setRegion(final AddressData addressData, final GooglePayAddressData address) {
        final String administrativeArea = address.getAdministrativeArea();

        if (StringUtils.isEmpty(administrativeArea)) {
            return;
        }

        final String countryIsoCode = address.getCountryCode();
        final RegionData region = i18NFacade.getRegion(countryIsoCode, administrativeArea);
        if (region != null) {
            addressData.setRegion(region);
        } else {
            LOG.debug(MessageFormat.format("Failed to determine region from country {0} and region code {1}", countryIsoCode, administrativeArea));
        }

    }
}
