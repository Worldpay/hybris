package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.springframework.ui.Model;

import javax.annotation.Resource;

import static com.worldpay.payment.TransactionStatus.*;

public abstract class AbstractWorldpayDirectCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {
    protected static final String TERM_URL_PARAM_NAME = "termURL";
    protected static final String PA_REQUEST_PARAM_NAME = "paRequest";
    protected static final String ISSUER_URL_PARAM_NAME = "issuerURL";
    protected static final String MERCHANT_DATA_PARAM_NAME = "merchantData";
    protected static final String CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID = "checkout.error.paymentethod.formentry.invalid";

    @Resource
    private WorldpayUrlService worldpayUrlService;
    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    public String handleDirectResponse(final Model model, final DirectResponseData directResponseData) throws CMSItemNotFoundException, WorldpayConfigurationException {
        if (AUTHORISED == directResponseData.getTransactionStatus()) {
            return redirectToOrderConfirmationPage(directResponseData.getOrderData());
        } else if (AUTHENTICATION_REQUIRED == directResponseData.getTransactionStatus()) {
            model.addAttribute(ISSUER_URL_PARAM_NAME, directResponseData.getIssuerURL());
            model.addAttribute(PA_REQUEST_PARAM_NAME, directResponseData.getPaRequest());
            model.addAttribute(TERM_URL_PARAM_NAME, worldpayUrlService.getFullThreeDSecureTermURL());
            model.addAttribute(MERCHANT_DATA_PARAM_NAME, getCheckoutFacade().getCheckoutCart().getWorldpayOrderCode());
            return worldpayAddonEndpointService.getAutoSubmit3DSecure();
        } else if (CANCELLED == directResponseData.getTransactionStatus()) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE_DEFAULT);
            return getErrorView(model);
        } else {
            GlobalMessages.addErrorMessage(model, getLocalisedDeclineMessage(directResponseData.getReturnCode()));
            return getErrorView(model);
        }
    }

    protected abstract String getErrorView(Model model) throws CMSItemNotFoundException;
}
