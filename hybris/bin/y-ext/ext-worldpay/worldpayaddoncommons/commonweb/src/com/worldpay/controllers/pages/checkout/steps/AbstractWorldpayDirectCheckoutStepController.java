package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractWorldpayDirectCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    protected static final String CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID = "checkout.error.paymentethod.formentry.invalid";
    private static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    private static final String THREED_SECURE_FLOW = "3D-Secure-Flow";

    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource
    private WorldpayDirectResponseFacade worldpayDirectResponseFacade;

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

    protected abstract String getErrorView(Model model) throws CMSItemNotFoundException;
}
