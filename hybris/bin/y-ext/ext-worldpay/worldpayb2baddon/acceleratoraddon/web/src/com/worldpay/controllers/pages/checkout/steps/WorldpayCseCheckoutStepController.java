package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Worldpay controller to handle CSE
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/cse")
public class WorldpayCseCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldpayCseCheckoutStepController.class);

    protected static final String CSE_PUBLIC_KEY = "csePublicKey";
    protected static final String B2B_CSE_PAYMENT_FORM = "b2bCSEPaymentForm";
    protected static final String REDIRECT_TO_CSE_PAGE = "redirect:/checkout/multi/worldpay/cse/cse-data";
    protected static final String REDIRECT_TO_SUMMARY_PAGE = "redirect:/checkout/multi/worldpay/summary/view";
    protected static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN = "originEventDomain3DSFlex";

    @Resource
    protected Validator csePaymentDetailsFormValidator;
    @Resource
    protected Validator cseFormValidator;
    @Resource
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    protected WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Resource
    protected WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource
    protected WorldpayDDCFacade worldpayDDCFacade;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    /**
     * Returns the CSE payment details page
     *
     * @param model
     * @return
     * @throws CMSItemNotFoundException
     */
    @GetMapping(value = {"/cse-data", "/tokenize"})
    @RequireHardLogIn
    public String getCseDataPage(final Model model) throws CMSItemNotFoundException {
        if (getCheckoutFacade().hasCheckoutCart()) {
            setupAddPaymentPage(model);
            return worldpayAddonEndpointService.getCSEPaymentDetailsPage();
        } else {
            return REDIRECT_URL_CART;
        }
    }

    /**
     * Validates and saves form details on submit of the payment details form for Client Side Encryption flow and redirects to payment form
     *
     * @param model
     * @param paymentDetailsForm
     * @param bindingResult
     * @param redirectAttrs
     * @return The view to redirect to.
     * @throws CMSItemNotFoundException
     */
    @PostMapping(value = "/add-payment-address")
    @RequireHardLogIn
    public String addPaymentAddress(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final BindingResult bindingResult, final RedirectAttributes redirectAttrs)
        throws CMSItemNotFoundException {
        csePaymentDetailsFormValidator.validate(paymentDetailsForm, bindingResult);

        if (addGlobalErrors(model, bindingResult)) {
            model.addAttribute(PAYMENT_DETAILS_FORM, paymentDetailsForm);
            return handleFormErrors(model, paymentDetailsForm);
        }

        final String paymentMethod = paymentDetailsForm.getPaymentMethod();

        handleAndSaveAddresses(paymentDetailsForm);
        if (paymentMethodIsOnline(paymentMethod)) {
            return REDIRECT_TO_CSE_PAGE;
        }
        redirectAttrs.addFlashAttribute(SHOPPER_BANK_CODE, paymentDetailsForm.getShopperBankCode());
        redirectAttrs.addFlashAttribute(PAYMENT_METHOD_PARAM, paymentDetailsForm.getPaymentMethod());
        return getRedirectToPaymentMethod();
    }

    /**
     * Validates and saves form details on submit of the CSE payment form and tokenizes valid payment details.
     *
     * @param request
     * @param model
     * @param b2bCSEPaymentForm
     * @param bindingResult
     * @return
     * @throws CMSItemNotFoundException
     */
    @PostMapping(value = "/tokenize")
    @RequireHardLogIn
    public String addCseData(final HttpServletRequest request, final Model model, @Valid final B2BCSEPaymentForm b2bCSEPaymentForm,
                             final BindingResult bindingResult, final RedirectAttributes redirectAttributes)
        throws CMSItemNotFoundException {
        cseFormValidator.validate(b2bCSEPaymentForm, bindingResult);

        getSessionService().setAttribute(SAVED_CARD_SELECTED_ATTRIBUTE, Boolean.FALSE);

        if (addGlobalErrors(model, bindingResult)) {
            return getErrorView(model);
        }

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSEAdditionalAuthInfo(b2bCSEPaymentForm);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, b2bCSEPaymentForm, cseAdditionalAuthInfo);
        return handleResponse(model, cseAdditionalAuthInfo, worldpayAdditionalInfoData, redirectAttributes);
    }

    private String handleResponse(final Model model, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        try {
            worldpayDirectOrderFacade.tokenize(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
            if (worldpayPaymentCheckoutFacade.isFSEnabled()) {
                redirectAttributes.addFlashAttribute(BIRTHDAY_DATE, worldpayAdditionalInfoData.getDateOfBirth());
                redirectAttributes.addFlashAttribute(DEVICE_SESSION, worldpayAdditionalInfoData.getDeviceSession());
            }
            return REDIRECT_TO_SUMMARY_PAGE;
        } catch (WorldpayException e) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOGGER.error("There was an error tokenizing the transaction", e);
            return getErrorView(model);
        }
    }

    /**
     * Returns the DDC collection iframe for 3d secure 2 payments
     *
     * @param model
     * @return the iframe
     */
    @GetMapping(value = "/DDCIframe")
    @RequireHardLogIn
    public String getDDCIframeContent(final Model model) {
        setDDCIframeData(model);

        return worldpayAddonEndpointService.getDdcIframe3dSecureFlex();
    }

    /**
     * Returns the DDC collection iframe for 3d secure 2 payments
     *
     * @param model
     * @return the iframe
     */
    @GetMapping(value = "/challengeIframe")
    @RequireHardLogIn
    public String getChallengeIframeContent(final Model model) {
        return worldpayAddonEndpointService.getChallengeIframe3dSecureFlex();
    }

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final B2BCSEPaymentForm b2BCSEPaymentForm, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSecurityCode(b2BCSEPaymentForm.getSecurityCode());
        worldpayAdditionalInfo.setDateOfBirth(b2BCSEPaymentForm.getDateOfBirth());
        worldpayAdditionalInfo.setDeviceSession(b2BCSEPaymentForm.getDeviceSession());

        if (cseAdditionalAuthInfo.getAdditional3DS2() != null) {
            worldpayAdditionalInfo.setAdditional3DS2(cseAdditionalAuthInfo.getAdditional3DS2());
        }
        return worldpayAdditionalInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        super.setupAddPaymentPage(model);
        model.addAttribute(B2B_CSE_PAYMENT_FORM, new B2BCSEPaymentForm());
        model.addAttribute(IS_FS_ENABLED, worldpayPaymentCheckoutFacade.isFSEnabled());
        final SimpleDateFormat dateFormat = new SimpleDateFormat(BIRTH_DAY_DATE_FORMAT);
        model.addAttribute(CURRENT_DATE, dateFormat.format(new Date()));
        model.addAttribute(CSE_PUBLIC_KEY, worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCsePublicKey());
        model.addAttribute(THREEDSFLEX_EVENT_ORIGIN_DOMAIN, worldpayDDCFacade.getEventOriginDomainForDDC());
    }

    @Override
    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        return worldpayAddonEndpointService.getCSEPaymentDetailsPage();
    }
}
