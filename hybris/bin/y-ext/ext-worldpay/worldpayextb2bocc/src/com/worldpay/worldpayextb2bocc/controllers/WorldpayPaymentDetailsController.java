package com.worldpay.worldpayextb2bocc.controllers;

import com.worldpay.facades.WorldpayUserFacade;
import com.worldpay.worldpayocccommons.controllers.AbstractWorldpayController;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Payment Details")
public class WorldpayPaymentDetailsController extends AbstractWorldpayController {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayPaymentDetailsController.class);

    @Resource(name = "userFacade")
    protected WorldpayUserFacade worldpayUserFacade;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @GetMapping("/carts/{cartId}/paymentdetails")
    @ResponseBody
    @Operation(operationId = "getPaymentDetailsList", summary = "Retrieves all credit card payment details of the customer.", description = "Retrieves all credit card payment details made by the customer.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentDetailsListWsDTO getPaymentDetailsList(
            @Parameter(description = "The flag to mark if the detailed payment is a saved one.") @RequestParam(defaultValue = "false") final boolean saved,
            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {
        LOG.debug("getPaymentDetailsList");
        final CCPaymentInfoDatas paymentInfoDataList = new CCPaymentInfoDatas();
        paymentInfoDataList.setPaymentInfos(worldpayUserFacade.getAvailableCCPaymentInfos(saved));

        return dataMapper.map(paymentInfoDataList, PaymentDetailsListWsDTO.class, fields);
    }


    @RequestMappingOverride
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @PatchMapping(value = "/paymentdetails/{paymentDetailsId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "updatePaymentDetails", summary = "Updates existing detailed information for a specific credit card payment. ", description =
            "Updates existing credit card payment details made by the customer using the paymentDetailsId. "
                    + "Only those attributes provided in the request will be updated.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public void updatePaymentDetails(
            @Parameter(description = "Payment details identifier.", required = true) @PathVariable final String paymentDetailsId) {
        final CCPaymentInfoData paymentInfoData = callSuperGetPaymentInfo(paymentDetailsId);
        final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

        if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo)
        {
            userFacade.setDefaultPaymentInfo(paymentInfoData);
        }
    }

    protected CCPaymentInfoData callSuperGetPaymentInfo(final String paymentDetailsId) {
        return super.getPaymentInfo(paymentDetailsId);
    }

}
