package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayDirectCheckoutStepController.BIRTHDAY_DATE;
import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;

@Controller
@RequestMapping(value = "/checkout/multi/worldpay/payment-method")
public class WorldpayPaymentMethodCheckoutStepController extends AbstractWorldpayPaymentMethodCheckoutStepController {

    private static final Logger LOGGER = LogManager.getLogger(WorldpayPaymentMethodCheckoutStepController.class);

    protected static final String HOP_DEBUG_MODE_CONFIG = "hop.debug.mode";
    protected static final String CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB = "checkout.multi.paymentMethod.breadcrumb";
    protected static final String PREFERRED_PAYMENT_METHOD_PARAM = "preferredPaymentMethod";
    protected static final String KLARNA_RESPONSE_PAGE_DATA_PARAM = "KLARNA_VIEW_DATA";

    @Resource
    protected WorldpayBankConfigurationFacade worldpayBankConfigurationFacade;
    @Resource
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    protected WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource
    protected WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    protected List<String> klarnaPayments;

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping(value = "/add")
    @RequireHardLogIn
    @PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD_STEP_NAME)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);

        // Use the checkout PCI strategy for getting the URL for creating new subscriptions.
        setCheckoutStepLinksForModel(model, getCheckoutStep());
        // Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
        try {
            final String paymentMethod = (String) model.asMap().get(PAYMENT_METHOD_PARAM);

            if (isBankTransferAPM(paymentMethod)) {
                return REDIRECT_PREFIX + getRedirectURLForBankTransfer(model, paymentMethod);

            } else if (isKlarnaAPM(paymentMethod)) {
                return getViewContentForKlarna(model);
            }

            final PaymentData paymentData = redirectAuthorise(model, paymentMethod);
            populateModel(model, paymentData);
            // Forward to the hosted order page which outputs the paymentData params into a form and (if not debugging) autosubmits to the
            // supplied postUrl
            return worldpayAddonEndpointService.getHostedOrderPostPage();
        } catch (final WorldpayException e) {
            LOGGER.error("Failed to communicate successfully with worldpay", e);
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, COMMUNICATION_ERROR);
            return REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
        }
    }

    @Override
    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", getCheckoutFlowFacade().hasNoPaymentInfo());
        prepareDataForPage(model);
        model.addAttribute(BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB));
        final ContentPageModel contentPage = getContentPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }

    protected PaymentData redirectAuthorise(final Model model, final String paymentMethod) throws WorldpayException {
        final Boolean savePaymentInfo = getSavePaymentInfo(model);
        final AdditionalAuthInfo additionalAuthInfo = createAdditionalAuthInfo(savePaymentInfo, paymentMethod);
        final PaymentData hostedOrderPageData = worldpayHostedOrderFacade.redirectAuthorise(additionalAuthInfo, createWorldpayAdditionalInfo(model));
        if (!paymentMethodIsOnline(paymentMethod)) {
            hostedOrderPageData.getParameters().put(PREFERRED_PAYMENT_METHOD_PARAM, paymentMethod);
        }
        return hostedOrderPageData;
    }

    protected String getRedirectURLForBankTransfer(final Model model, final String paymentMethod) throws WorldpayException {
        final String shopperBankCode = (String) model.asMap().get(SHOPPER_BANK_CODE);
        final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo = createBankTransferAdditionalAuthInfo(paymentMethod, shopperBankCode);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData((HttpServletRequest) model.asMap().get(REQUEST));
        return worldpayDirectOrderFacade.authoriseBankTransferRedirect(bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
    }

    protected String getViewContentForKlarna(final Model model) throws WorldpayException {
        final Boolean savePaymentInfo = getSavePaymentInfo(model);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData((HttpServletRequest) model.asMap().get(REQUEST));
        final AdditionalAuthInfo additionalAuthInfo = createAdditionalAuthInfo(savePaymentInfo, PaymentType.KLARNASSL.getMethodCode());
        final String klarnaRedirectContent = worldpayDirectOrderFacade.authoriseKlarnaRedirect(worldpayAdditionalInfoData, additionalAuthInfo);
        model.addAttribute(KLARNA_RESPONSE_PAGE_DATA_PARAM, klarnaRedirectContent);

        return worldpayAddonEndpointService.getKlarnaResponsePage();
    }

    protected boolean isBankTransferAPM(final String paymentMethod) {
        if (paymentMethodIsOnline(paymentMethod)) {
            return false;
        }
        return worldpayBankConfigurationFacade.isBankTransferApm(paymentMethod);
    }

    protected boolean isKlarnaAPM(final String paymentMethod) {
        return PaymentType.KLARNASSL.getMethodCode().equals(paymentMethod);
    }

    protected void populateModel(final Model model, final PaymentData hostedOrderPageData) {
        model.addAttribute(HOSTED_ORDER_PAGE_DATA, hostedOrderPageData);

        final boolean hopDebugMode = getSiteConfigService().getBoolean(HOP_DEBUG_MODE_CONFIG, false);
        model.addAttribute(HOP_DEBUG_MODE_PARAM, hopDebugMode);
    }

    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    @Override
    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(PAYMENT_METHOD_STEP_NAME);
    }

    public List<String> getKlarnaPayments() {
        return klarnaPayments;
    }

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final Model model) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData((HttpServletRequest) model.asMap().get(REQUEST));
        if (worldpayPaymentCheckoutFacade.isFSEnabled()) {
            worldpayAdditionalInfoData.setDateOfBirth((Date) model.asMap().get(BIRTHDAY_DATE));
        }
        return worldpayAdditionalInfoData;
    }
}
