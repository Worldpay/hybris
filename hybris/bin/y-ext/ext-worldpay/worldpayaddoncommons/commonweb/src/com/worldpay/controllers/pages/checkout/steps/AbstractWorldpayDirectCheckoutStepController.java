package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addErrorMessage;

@SuppressWarnings("java:S110")
public abstract class AbstractWorldpayDirectCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    protected static final String CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID = "checkout.error.paymentethod.formentry.invalid";
    protected static final String THREEDSECURE_JWT_FLEX_DDC = "jwt3DSecureFlexDDC";
    protected static final String THREEDSECURE_FLEX_DDC_URL = "threeDSecureDDCUrl";
    protected static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    protected static final String THREED_SECURE_FLOW = "3D-Secure-Flow";
    protected static final String BIRTH_DAY_DATE_FORMAT = "dd/MM/yyyy";
    protected static final String BIRTHDAY_DATE = "birthdayDate";
    protected static final String DEVICE_SESSION = "DEVICE_SESSION";

    @Resource
    protected WorldpayDirectResponseFacade worldpayDirectResponseFacade;
    @Resource
    protected WorldpayDDCFacade worldpayDDCFacade;

    public String handleDirectResponse(final Model model, final DirectResponseData directResponseData, final HttpServletResponse response) throws CMSItemNotFoundException, WorldpayConfigurationException {
        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.isAuthorised(directResponseData))) {
            return redirectToOrderConfirmationPage(directResponseData.getOrderData());
        }

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.is3DSecureLegacyFlow(directResponseData))) {
            final Map<String, String> attributes = worldpayDirectResponseFacade.retrieveAttributesForLegacy3dSecure(directResponseData);
            model.addAllAttributes(attributes);
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.FALSE.toString());
            return worldpayAddonEndpointService.getAutoSubmit3DSecure();
        }

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.is3DSecureFlexFlow(directResponseData))) {
            final Map<String, String> attributes = worldpayDirectResponseFacade.retrieveAttributesForFlex3dSecure(directResponseData);
            model.addAllAttributes(attributes);
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.TRUE.toString());
            return worldpayAddonEndpointService.getAutoSubmit3DSecureFlex();
        }

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.isCancelled(directResponseData))) {
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


    protected boolean hasDeliveryAddress(final Model model) {
        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            addErrorMessage(model, "checkout.deliveryAddress.notSelected");
            return false;
        }
        return true;
    }

    protected boolean hasDeliveryMode(final Model model) {
        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            addErrorMessage(model, "checkout.deliveryMethod.notSelected");
            return false;
        }
        return true;
    }

    protected boolean hasPaymentInfo(final Model model) {
        if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
            addErrorMessage(model, "checkout.paymentMethod.notSelected");
            return false;
        }
        return true;
    }
}
