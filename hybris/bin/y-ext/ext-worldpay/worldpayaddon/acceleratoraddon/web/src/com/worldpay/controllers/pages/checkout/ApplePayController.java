package com.worldpay.controllers.pages.checkout;

import com.worldpay.data.ApplePayAuthorisationRequest;
import com.worldpay.data.ApplePayOrderUpdate;
import com.worldpay.data.ApplePayPaymentMethodUpdateRequest;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.payment.applepay.ValidateMerchantRequestData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.order.InvalidCartException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/checkout/multi/worldpay/applepay")
@Profile("applepay")
public class ApplePayController extends AbstractCheckoutController {

    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayApplePayPaymentCheckoutFacade worldpayApplePayPaymentCheckoutFacade;
    @Resource(name = "worldpayApplePayRestTemplate")
    private RestTemplate restTemplate;

    @RequireHardLogIn
    @PostMapping(value = "/request-session", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object requestPaymentSession(@RequestBody final ValidateMerchantRequestData validateMerchantRequestData) {
        final ValidateMerchantRequestDTO requestDTO = worldpayApplePayPaymentCheckoutFacade.getValidateMerchantRequestDTO();

        return restTemplate.postForObject(validateMerchantRequestData.getValidationURL(), requestDTO, Object.class);
    }

    @RequireHardLogIn
    @PostMapping(value = "/authorise-order", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public DirectResponseData authoriseOrder(@RequestBody final ApplePayAuthorisationRequest authorisationRequest) throws WorldpayException, InvalidCartException {
        worldpayApplePayPaymentCheckoutFacade.saveBillingAddresses(authorisationRequest.getBillingContact());

        return worldpayDirectOrderFacade.authoriseApplePayDirect(authorisationRequest.getToken().getPaymentData());
    }

    @RequireHardLogIn
    @PostMapping(value = "/update-payment-method", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ApplePayOrderUpdate updatePaymentMethod(@RequestBody final ApplePayPaymentMethodUpdateRequest paymentMethodUpdateRequest) {
        return worldpayDirectOrderFacade.updatePaymentMethod(paymentMethodUpdateRequest);
    }
}
