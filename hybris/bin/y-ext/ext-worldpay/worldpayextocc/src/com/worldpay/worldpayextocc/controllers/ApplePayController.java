package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.ApplePayAuthorisationRequest;
import com.worldpay.data.ApplePayOrderUpdate;
import com.worldpay.data.ApplePayPaymentMethodUpdateRequest;
import com.worldpay.dto.applepay.ValidateMerchantRequestWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.payment.applepay.ValidateMerchantRequestData;
import de.hybris.platform.order.InvalidCartException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/applepay")
@Profile("applepay")
public class ApplePayController extends AbstractWorldpayController {
    @Resource
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    protected WorldpayApplePayPaymentCheckoutFacade worldpayApplePayPaymentCheckoutFacade;
    @Resource(name = "worldpayApplePayRestTemplate")
    protected RestTemplate restTemplate;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/request-session", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object requestPaymentSession(@RequestBody final ValidateMerchantRequestWsDTO validateMerchantRequestWsDTO) {
        final ValidateMerchantRequestData validateMerchantRequestData = dataMapper.map(validateMerchantRequestWsDTO, ValidateMerchantRequestData.class);
        final ValidateMerchantRequestDTO requestDTO = worldpayApplePayPaymentCheckoutFacade.getValidateMerchantRequestDTO();

        return restTemplate.postForObject(validateMerchantRequestData.getValidationURL(), requestDTO, Object.class);
    }

    @Secured(
            {"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/authorise-order", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public DirectResponseData authoriseOrder(@RequestBody final ApplePayAuthorisationRequest authorisationRequest) throws WorldpayException, InvalidCartException {
        worldpayApplePayPaymentCheckoutFacade.saveBillingAddresses(authorisationRequest.getBillingContact());

        return worldpayDirectOrderFacade.authoriseApplePayDirect(authorisationRequest.getToken().getPaymentData());
    }

    @Secured(
            {"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/update-payment-method", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ApplePayOrderUpdate updatePaymentMethod(@RequestBody final ApplePayPaymentMethodUpdateRequest paymentMethodUpdateRequest) {
        return worldpayDirectOrderFacade.updatePaymentMethod(paymentMethodUpdateRequest);
    }
}
