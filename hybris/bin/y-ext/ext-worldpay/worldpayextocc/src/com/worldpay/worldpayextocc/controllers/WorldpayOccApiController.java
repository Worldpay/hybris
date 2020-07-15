package com.worldpay.worldpayextocc.controllers;

import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/{baseSiteId}/worldpayapi")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class WorldpayOccApiController {

    @Resource
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    @GetMapping(value = "/cse-public-key", produces = "application/json")
    public String getCsePublicKey() {
        return worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCsePublicKey();
    }
}
