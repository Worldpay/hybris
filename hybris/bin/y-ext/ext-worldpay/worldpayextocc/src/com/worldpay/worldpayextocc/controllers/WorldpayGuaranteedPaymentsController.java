package com.worldpay.worldpayextocc.controllers;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/{baseSiteId}/worldpayapi/guaranteedpayments")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@ApiBaseSiteIdParam
public class WorldpayGuaranteedPaymentsController {

    @Resource
    protected WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;

    @GetMapping("/enabled")
    public boolean isGuaranteedPaymentsEnabled() {
        return worldpayPaymentCheckoutFacade.isGPEnabled();
    }

}
