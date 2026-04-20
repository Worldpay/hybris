package com.worldpay.worldpayextocc.controllers;

import com.worldpay.dto.cms.WorldpayAPMComponentWsDTO;
import com.worldpay.dto.cms.WorldpayAPMComponentsData;
import com.worldpay.dto.cms.WorldpayAPMComponentsWsDTO;
import com.worldpay.facades.WorldpayAPMComponentFacade;
import com.worldpay.worldpayocccommons.controllers.AbstractWorldpayController;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/cms/components")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
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

    @GetMapping(value = "/{apmComponentId}")
    @Operation(operationId = "Gets apm component", description = "Returns apm component", summary = "getApmComponent")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public WorldpayAPMComponentWsDTO getApmComponent(
            @Parameter(description = "APM component identifier.", required = true) @PathVariable final String apmComponentId,
            @Parameter(description = "Response configuration (list of fields, which should be returned in response)", schema = @Schema(type = "string", allowableValues = { "BASIC", "DEFAULT", "FULL" })) @RequestParam(defaultValue = "DEFAULT") final String fields) {

        return dataMapper.map(worldpayAPMComponentFacade.getWorldpayAPMComponentByCode(apmComponentId), WorldpayAPMComponentWsDTO.class, fields);
    }
}
