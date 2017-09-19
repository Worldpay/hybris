package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Worldpay response controller
 */
@Controller
@RequestMapping (value = "/checkout/multi/worldpay")
public class WorldpayResponseController extends WorldpayChoosePaymentMethodCheckoutStepController {

    protected static final String BILLING_ADDRESS_FORM = "wpBillingAddressForm";

    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    /**
     * Endpoint to get billing address form
     * @param countryIsoCode
     * @param useDeliveryAddress
     * @param model
     * @return
     */
    @RequestMapping (value = "/billingaddressform", method = RequestMethod.GET)
    public String getCountryAddressForm(@RequestParam ("countryIsoCode") final String countryIsoCode,
                                        @RequestParam ("useDeliveryAddress") final boolean useDeliveryAddress, final Model model) {
        model.addAttribute("supportedCountries", getCountries());
        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
        model.addAttribute("country", countryIsoCode);

        final PaymentDetailsForm wpPaymentDetailsForm = new PaymentDetailsForm();
        model.addAttribute(BILLING_ADDRESS_FORM, wpPaymentDetailsForm);
        populateAddressForm(countryIsoCode, useDeliveryAddress, wpPaymentDetailsForm);
        return worldpayAddonEndpointService.getBillingAddressForm();
    }

    /**
     * {@inheritDoc}
     */
    protected Map<String, String> getRequestParameterMap(final HttpServletRequest request) {
        final Enumeration parameterNames = request.getParameterNames();
        final Map<String, String> map = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            final String paramName = (String) parameterNames.nextElement();
            final String paramValue = request.getParameter(paramName);
            map.put(paramName, paramValue);
        }
        return map;
    }

    protected void populateAddressForm(final String countryIsoCode, final boolean useDeliveryAddress, final PaymentDetailsForm paymentDetailsForm) {
        if (useDeliveryAddress) {
            final AddressData deliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
            final AddressForm addressForm = new AddressForm();

            final RegionData region = deliveryAddress.getRegion();
            if (region != null && !StringUtils.isEmpty(region.getIsocode())) {
                addressForm.setRegionIso(region.getIsocode());
            }
            addressForm.setFirstName(deliveryAddress.getFirstName());
            addressForm.setLastName(deliveryAddress.getLastName());
            addressForm.setLine1(deliveryAddress.getLine1());
            addressForm.setLine2(deliveryAddress.getLine2());
            addressForm.setTownCity(deliveryAddress.getTown());
            addressForm.setPostcode(deliveryAddress.getPostalCode());
            addressForm.setCountryIso(countryIsoCode);
            addressForm.setPhone(deliveryAddress.getPhone());
            paymentDetailsForm.setBillingAddress(addressForm);
        }
    }
}
