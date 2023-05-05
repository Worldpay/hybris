package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.service.WorldpayAddonEndpointService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * Worldpay response controller
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay")
@SuppressWarnings("java:S110")
public class WorldpayResponseController extends WorldpayChoosePaymentMethodCheckoutStepController {

    /**
     * Endpoint to get billing address form
     *
     * @param countryIsoCode     the country iso code
     * @param useDeliveryAddress the delivery address
     * @param model              the {@link Model} to be used
     * @return the address form
     */
    @GetMapping(value = "/billingaddressform")
    public String getCountryAddressForm(@RequestParam("countryIsoCode") final String countryIsoCode,
                                        @RequestParam("useDeliveryAddress") final boolean useDeliveryAddress,
                                        final Model model) {
        return super.getCountryAddressForm(countryIsoCode, useDeliveryAddress, model);
    }
}
