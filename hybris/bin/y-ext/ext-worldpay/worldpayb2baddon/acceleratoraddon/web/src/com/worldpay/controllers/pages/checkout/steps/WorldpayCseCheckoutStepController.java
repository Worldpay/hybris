package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Worldpay controller to handle CSE
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/cse")
public class WorldpayCseCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpayCseCheckoutStepController.class);

    protected static final String CSE_PUBLIC_KEY = "csePublicKey";
    protected static final String B2B_CSE_PAYMENT_FORM = "b2bCSEPaymentForm";
    protected static final String REDIRECT_TO_CSE_PAGE = "redirect:/checkout/multi/worldpay/cse/cse-data";
    protected static final String REDIRECT_TO_SUMMARY_PAGE = "redirect:/checkout/multi/worldpay/summary/view";
    protected static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN = "originEventDomain3DSFlex";

    @Resource
    private Validator csePaymentDetailsFormValidator;
    @Resource
    private Validator cseFormValidator;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource
    private WorldpayDDCFacade worldpayDDCFacade;

    /**
     * Returns the CSE payment details page
     *
     * @param model
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = {"/cse-data", "/tokenize"}, method = GET)
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
    @RequestMapping(value = "/add-payment-address", method = POST)
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
    @RequestMapping(value = "/tokenize", method = POST)
    @RequireHardLogIn
    public String addCseData(final HttpServletRequest request, final Model model, @Valid final B2BCSEPaymentForm b2bCSEPaymentForm, final BindingResult bindingResult)
            throws CMSItemNotFoundException {
        cseFormValidator.validate(b2bCSEPaymentForm, bindingResult);

        getSessionService().setAttribute(SAVED_CARD_SELECTED_ATTRIBUTE, Boolean.FALSE);

        if (addGlobalErrors(model, bindingResult)) {
            return getErrorView(model);
        }

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSEAdditionalAuthInfo(b2bCSEPaymentForm);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, b2bCSEPaymentForm.getCvc(), cseAdditionalAuthInfo);
        return handleResponse(model, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
    }

    private String handleResponse(final Model model, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws CMSItemNotFoundException {
        try {
            worldpayDirectOrderFacade.tokenize(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
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

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = getWorldpayAdditionalInfoFacade().createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSecurityCode(cvc);
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
        model.addAttribute(CSE_PUBLIC_KEY, worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCsePublicKey());
        model.addAttribute(THREEDSFLEX_EVENT_ORIGIN_DOMAIN, worldpayDDCFacade.getEventOriginDomainForDDC());
    }

    @Override
    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        return worldpayAddonEndpointService.getCSEPaymentDetailsPage();
    }
}
