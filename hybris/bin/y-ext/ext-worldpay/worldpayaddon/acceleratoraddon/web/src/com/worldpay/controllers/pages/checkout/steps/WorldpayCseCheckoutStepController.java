package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.order.InvalidCartException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Web controller to handle a CSE checkout step
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/cse")
@SuppressWarnings({"java:S110", "common-java:DuplicatedBlocks"})
public class WorldpayCseCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOGGER = LogManager.getLogger(WorldpayCseCheckoutStepController.class);

    protected static final String CSE_PUBLIC_KEY = "csePublicKey";
    protected static final String CSE_PAYMENT_FORM = "csePaymentForm";
    protected static final String REDIRECT_TO_CSE_PAGE = "redirect:/checkout/multi/worldpay/cse/cse-data";
    protected static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN = "originEventDomain3DSFlex";

    @Resource
    protected Validator csePaymentDetailsFormValidator;
    @Resource
    protected Validator cseFormValidator;
    @Resource
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    /**
     * Returns the CSE payment details page
     *
     * @param model
     * @return
     * @throws CMSItemNotFoundException
     */
    @GetMapping(value = {"/cse-data", "/place-order"})
    @RequireHardLogIn
    @SuppressWarnings("common-java:DuplicatedBlocks")
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
    @SuppressWarnings("common-java:DuplicatedBlocks")
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
        redirectAttrs.addFlashAttribute(SAVE_PAYMENT_INFO, paymentDetailsForm.getSaveInAccount());
        return getRedirectToPaymentMethod();
    }

    /**
     * Validates and saves form details on submit of the CSE payment form and authorises valid payment details.
     *
     * @param request
     * @param model
     * @param csePaymentForm
     * @param bindingResult
     * @return
     * @throws CMSItemNotFoundException
     */
    @PostMapping(value = "/place-order")
    @RequireHardLogIn
    public String addCseData(final HttpServletRequest request, final Model model, @Valid final CSEPaymentForm csePaymentForm, final BindingResult bindingResult, final HttpServletResponse response)
            throws CMSItemNotFoundException {
        cseFormValidator.validate(csePaymentForm, bindingResult);

        if (addGlobalErrors(model, bindingResult)) {
            return getErrorView(model);
        }

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSEAdditionalAuthInfo(csePaymentForm);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, csePaymentForm, cseAdditionalAuthInfo);

        return authoriseAndHandleResponse(model, cseAdditionalAuthInfo, worldpayAdditionalInfoData, response);
    }

    protected String authoriseAndHandleResponse(final Model model, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final HttpServletResponse response) throws CMSItemNotFoundException {
        try {
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseAndTokenize(worldpayAdditionalInfoData, cseAdditionalAuthInfo);
            return handleDirectResponse(model, directResponseData, response);
        } catch (final InvalidCartException | WorldpayException e) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOGGER.error("There was an error authorising the transaction", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
     * @return the iframe
     */
    @GetMapping(value = "/challengeIframe")
    @RequireHardLogIn
    public String getChallengeIframeContent(final Model model) {
        return worldpayAddonEndpointService.getChallengeIframe3dSecureFlex();
    }

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final CSEPaymentForm csePaymentForm, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setDateOfBirth(csePaymentForm.getDateOfBirth());
        worldpayAdditionalInfo.setSecurityCode(csePaymentForm.getCvc());
        worldpayAdditionalInfo.setDeviceSession(csePaymentForm.getDeviceSession());
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

        model.addAttribute(CSE_PAYMENT_FORM, new CSEPaymentForm());
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
