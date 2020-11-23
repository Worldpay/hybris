package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractWorldpayDirectCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    protected static final String CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID = "checkout.error.paymentethod.formentry.invalid";
    protected static final String THREEDSECURE_JWT_FLEX_DDC = "jwt3DSecureFlexDDC";
    protected static final String THREEDSECURE_FLEX_DDC_URL = "threeDSecureDDCUrl";
    protected static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    protected static final String THREED_SECURE_FLOW = "3D-Secure-Flow";

    @Resource
    protected WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource
    protected WorldpayDirectResponseFacade worldpayDirectResponseFacade;
    @Resource
    protected WorldpayDDCFacade worldpayDDCFacade;

    public String handleDirectResponse(final Model model, final DirectResponseData directResponseData, final HttpServletResponse response) throws CMSItemNotFoundException, WorldpayConfigurationException {
        if (worldpayDirectResponseFacade.isAuthorised(directResponseData)) {
            return redirectToOrderConfirmationPage(directResponseData.getOrderData());
        }

        if (worldpayDirectResponseFacade.is3DSecureLegacyFlow(directResponseData)) {
            final Map<String, String> attributes = worldpayDirectResponseFacade.retrieveAttributesForLegacy3dSecure(directResponseData);
            model.addAllAttributes(attributes);
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.FALSE.toString());
            return worldpayAddonEndpointService.getAutoSubmit3DSecure();
        }

        if (worldpayDirectResponseFacade.is3DSecureFlexFlow(directResponseData)) {
            final Map<String, String> attributes = worldpayDirectResponseFacade.retrieveAttributesForFlex3dSecure(directResponseData);
            model.addAllAttributes(attributes);
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.TRUE.toString());
            return worldpayAddonEndpointService.getAutoSubmit3DSecureFlex();
        }

        if (worldpayDirectResponseFacade.isCancelled(directResponseData)) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE_DEFAULT);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorView(model);
        }

        GlobalMessages.addErrorMessage(model, getLocalisedDeclineMessage(directResponseData.getReturnCode()));
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return getErrorView(model);

    }

    public void setDDCIframeData(final Model model) {
        final String ddcUrl = Optional.ofNullable(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData())
                .map(WorldpayMerchantConfigData::getThreeDSFlexJsonWebTokenSettings)
                .map(ThreeDSFlexJsonWebTokenCredentials::getDdcUrl)
                .orElse(null);
        model.addAttribute(THREEDSECURE_FLEX_DDC_URL, ddcUrl);
        model.addAttribute(THREEDSECURE_JWT_FLEX_DDC, worldpayDDCFacade.createJsonWebTokenForDDC());
    }

    protected abstract String getErrorView(Model model) throws CMSItemNotFoundException;
}
