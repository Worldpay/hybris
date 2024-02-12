package com.worldpay.worldpayextocc.controllers;

import com.worldpay.dto.cms.WorldpayAPMComponentsData;
import com.worldpay.dto.cms.WorldpayAPMComponentsWsDTO;
import com.worldpay.facades.WorldpayAPMComponentFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/cms/components")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@ApiVersion("v2")
@Api(tags = "Components")
public class WorldpayComponentController extends AbstractWorldpayController {

    @Resource(name = "worldpayAPMComponentFacade")
    private WorldpayAPMComponentFacade worldpayAPMComponentFacade;

    @GetMapping(value = "/availableapmcomponents")
    @ApiOperation(value = "Gets all available apm components", notes = "Returns all available cms apm components", nickname = "getAvailableApmComponents")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public WorldpayAPMComponentsWsDTO getAvailableApmComponents(
            @ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "DEFAULT") final String fields) {
        final WorldpayAPMComponentsData worldpayAPMComponentsData = new WorldpayAPMComponentsData();
        worldpayAPMComponentsData.setApmComponents(worldpayAPMComponentFacade.getAllAvailableWorldpayAPMComponents());

        return dataMapper.map(worldpayAPMComponentsData, WorldpayAPMComponentsWsDTO.class, fields);
    }
}
