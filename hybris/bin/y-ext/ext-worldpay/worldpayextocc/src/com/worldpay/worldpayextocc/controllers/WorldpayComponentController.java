package com.worldpay.worldpayextocc.controllers;

import com.worldpay.dto.cms.WorldpayAPMComponentsData;
import com.worldpay.dto.cms.WorldpayAPMComponentsWsDTO;
import com.worldpay.facades.WorldpayAPMComponentFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/cms/components")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@ApiVersion("v2")
@Tag(name = "Components")
public class WorldpayComponentController extends AbstractWorldpayController {

    @Resource(name = "worldpayAPMComponentFacade")
    private WorldpayAPMComponentFacade worldpayAPMComponentFacade;

    @GetMapping(value = "/availableapmcomponents")
    @Operation(operationId = "Gets all available apm components", description = "Returns all available cms apm components", summary = "getAvailableApmComponents")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public WorldpayAPMComponentsWsDTO getAvailableApmComponents(
        @Parameter(description = "Response configuration (list of fields, which should be returned in response)", schema = @Schema(type = "string", allowableValues = { "BASIC", "DEFAULT", "FULL" })) @RequestParam(defaultValue = "DEFAULT") final String fields) {
        final WorldpayAPMComponentsData worldpayAPMComponentsData = new WorldpayAPMComponentsData();
        worldpayAPMComponentsData.setApmComponents(worldpayAPMComponentFacade.getAllAvailableWorldpayAPMComponents());

        return dataMapper.map(worldpayAPMComponentsData, WorldpayAPMComponentsWsDTO.class, fields);
    }
}
