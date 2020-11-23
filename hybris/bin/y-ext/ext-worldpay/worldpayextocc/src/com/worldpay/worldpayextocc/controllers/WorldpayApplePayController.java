package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.ApplePayAuthorisationRequest;
import com.worldpay.data.ApplePayPaymentRequest;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.payment.applepay.ValidateMerchantRequestData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/apple")
@Profile("applepay")
public class WorldpayApplePayController extends AbstractWorldpayController {

    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayApplePayPaymentCheckoutFacade worldpayApplePayPaymentCheckoutFacade;
    @Resource(name = "worldpayApplePayRestTemplate")
    private RestTemplate restTemplate;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @GetMapping(value = "/payment-request", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<ApplePayPaymentRequest> getPaymentRequest() {
        return ResponseEntity.ok(worldpayApplePayPaymentCheckoutFacade.getApplePayPaymentRequest(checkoutFacade.getCheckoutCart()));
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/request-session", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object requestPaymentSession(@RequestBody final ValidateMerchantRequestData validateMerchantRequestData) {
        final ValidateMerchantRequestDTO requestDTO = worldpayApplePayPaymentCheckoutFacade.getValidateMerchantRequestDTO();

        return restTemplate.postForObject(validateMerchantRequestData.getValidationURL(), requestDTO, Object.class);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/authorise-order", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public PlaceOrderResponseWsDTO authoriseOrder(@RequestBody final ApplePayAuthorisationRequest authorisationRequest,
                                                  @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
                                                  final HttpServletResponse response) throws WorldpayException, InvalidCartException {
        worldpayApplePayPaymentCheckoutFacade.saveBillingAddresses(authorisationRequest.getBillingContact());

        final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseApplePayDirect(authorisationRequest.getToken().getPaymentData());
        return handleDirectResponse(directResponseData, response, fields);
    }
}
