package com.worldpay.worldpayextocc.controllers;

import com.worldpay.config.merchant.GooglePayConfigData;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAddressData;
import com.worldpay.data.GooglePayAuthorisationRequest;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/google")
public class WorldpayGooglePayController extends AbstractWorldpayController {

    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private I18NFacade i18NFacade;
    @Resource
    private UserFacade userFacade;
    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Resource
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @GetMapping(value = "/merchant-configuration", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<GooglePayConfigData> getGooglePaySettings() {
        return ResponseEntity.ok(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getGooglePaySettings());
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/authorise-order", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PlaceOrderResponseWsDTO authoriseOrder(@RequestBody final GooglePayAuthorisationRequest authorisationRequest, @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
                                                  final HttpServletResponse response) throws WorldpayException, InvalidCartException {
        saveBillingAddress(authorisationRequest.getBillingAddress());

        final GooglePayAdditionalAuthInfo token = authorisationRequest.getToken();
        token.setSaveCard(authorisationRequest.getSaved());
        final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseGooglePayDirect(token);

        return handleDirectResponse(directResponseData, response, fields);
    }

    protected void saveBillingAddress(final GooglePayAddressData address) {
        final AddressData addressData = new AddressData();
        addressData.setBillingAddress(true);
        addressData.setFirstName(address.getName());
        addressData.setLastName(StringUtils.EMPTY);
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

        if (StringUtils.isBlank(administrativeArea)) {
            return;
        }

        final String countryIsoCode = address.getCountryCode();
        final RegionData region = i18NFacade.getRegion(countryIsoCode, administrativeArea);
        if (region != null) {
            addressData.setRegion(region);
        }
    }
}
