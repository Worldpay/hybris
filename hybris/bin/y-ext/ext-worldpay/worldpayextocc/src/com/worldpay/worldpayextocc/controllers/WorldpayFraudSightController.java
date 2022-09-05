package com.worldpay.worldpayextocc.controllers;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/{baseSiteId}/worldpayapi/fraudsight")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class WorldpayFraudSightController {

    @Resource
    protected WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;

    @GetMapping("/enabled")
    @ResponseBody
    @ApiBaseSiteIdParam
    public boolean isFraudSightEnabled() {
        return worldpayPaymentCheckoutFacade.isFSEnabled();
    }

}
