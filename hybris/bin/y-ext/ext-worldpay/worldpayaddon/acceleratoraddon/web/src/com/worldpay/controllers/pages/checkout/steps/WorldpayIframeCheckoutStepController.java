package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayCartFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Web controller to handle the Iframe in a checkout step
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/iframe")
public class WorldpayIframeCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    protected static final String SHOW_NGPP_IFRAME = "showNGPPIframe";
    protected static final String REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS = REDIRECT_PREFIX + "/checkout/multi/worldpay/iframe/add-payment-details";
    private static final Logger LOGGER = Logger.getLogger(WorldpayIframeCheckoutStepController.class);
    @Resource
    protected WorldpayCartFacade worldpayCartFacade;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    protected WorldpayHostedOrderFacade worldpayHostedOrderFacade;

    /**
     * @param model              the {@Model} to be used
     * @param paymentDetailsForm the payment details
     * @param bindingResult      the binding result
     * @param redirectAttrs      the {@RedirectAttributes} to be used
     * @return
     * @throws CMSItemNotFoundException
     */
    @PostMapping(value = "/add-payment-details")
    @RequireHardLogIn
    public String addPaymentDetails(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final HttpServletRequest httpServletRequest, final BindingResult bindingResult, final RedirectAttributes redirectAttrs)
        throws CMSItemNotFoundException {

        paymentDetailsFormValidator.validate(paymentDetailsForm, bindingResult);
        if (addGlobalErrors(model, bindingResult)) {
            return handleFormErrors(model, paymentDetailsForm);
        }
        handleAndSaveAddresses(paymentDetailsForm);

        final String shopperBankCode = paymentDetailsForm.getShopperBankCode();
        worldpayCartFacade.resetDeclineCodeAndShopperBankOnCart(shopperBankCode);

        final String paymentMethod = paymentDetailsForm.getPaymentMethod();
        if (isAPM(paymentMethod)) {
            redirectAttrs.addFlashAttribute(PAYMENT_METHOD_PARAM, paymentMethod);
            redirectAttrs.addFlashAttribute(SHOPPER_BANK_CODE, shopperBankCode);
            redirectAttrs.addFlashAttribute(SAVE_PAYMENT_INFO, paymentDetailsForm.getSaveInAccount());
            return getRedirectToPaymentMethod();
        }
        try {
            final AdditionalAuthInfo additionalAuthInfo = createAdditionalAuthInfo(paymentDetailsForm.getSaveInAccount(), paymentMethod);
            final PaymentData paymentData = worldpayHostedOrderFacade.redirectAuthorise(additionalAuthInfo, createWorldpayAdditionalInfo(httpServletRequest));
            worldpayHostedOrderFacade.createPaymentInfoModelOnCart(paymentDetailsForm.getSaveInAccount());
            redirectAttrs.addFlashAttribute(PAYMENT_DATA, paymentData);
            redirectAttrs.addFlashAttribute(SHOW_NGPP_IFRAME, true);
        } catch (final WorldpayException e) {
            redirectAttrs.addFlashAttribute(SHOW_NGPP_IFRAME, false);
            GlobalMessages.addFlashMessage(redirectAttrs, GlobalMessages.ERROR_MESSAGES_HOLDER, COMMUNICATION_ERROR);
            LOGGER.error("An error occurred during Worldpay redirect authorise: " + e.getMessage(), e);
        }
        return REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS;
    }

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request) {
        return worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
    }

}
