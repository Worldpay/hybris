package com.worldpay.worldpayextb2bocc.controllers;

import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.worldpayextb2bocc.exceptions.WorldpayInvalidPaymentInfoException;
import com.worldpay.worldpayocccommons.controllers.AbstractWorldpayController;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Tag(name = "Cart Payments")
public class WorldpayCartPaymentsController extends AbstractWorldpayController {

    @Resource(name = "worldpayB2BAcceleratorCheckoutFacadeDecorator")
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator worldpayB2BAcceleratorCheckoutFacadeDecorator;

    @RequestMappingOverride
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PutMapping(value = "/{cartId}/paymentdetails")
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceCartPaymentDetails", summary = "Sets credit card payment details for the cart.", description = "Sets credit card payment details for the specified cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void replaceCartPaymentDetails(@Parameter(description = "Payment details identifier.", required = true) @RequestParam final String paymentDetailsId) throws WorldpayInvalidPaymentInfoException {
        setPaymentDetailsInternal(paymentDetailsId);
    }

    public CartData setPaymentDetailsInternal(final String paymentDetailsId) throws WorldpayInvalidPaymentInfoException {
        if (worldpayB2BAcceleratorCheckoutFacadeDecorator.setPaymentDetails(paymentDetailsId)) {
            return cartFacadeCommercewebservices.getSessionCart();
        }

        throw new WorldpayInvalidPaymentInfoException(paymentDetailsId);
    }
}
