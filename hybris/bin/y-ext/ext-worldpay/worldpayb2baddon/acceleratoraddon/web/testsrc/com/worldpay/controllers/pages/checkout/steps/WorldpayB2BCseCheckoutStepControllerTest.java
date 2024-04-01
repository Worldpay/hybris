package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayDirectCheckoutStepController.CMS_PAGE_MODEL;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.*;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayB2BCseCheckoutStepController.*;
import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static com.worldpay.service.model.payment.PaymentType.PAYPAL_EXPRESS;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController.REDIRECT_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayB2BCseCheckoutStepControllerTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CSE_PUBLIC_KEY_VALUE = "csePublicKeyValue";
    private static final String REDIRECT_TO_PAYMENT_METHOD = "redirectToPaymentMethod";
    private static final String CART_DATA = "cartData";
    private static final String SESSION_ID = "sessionID";
    private static final String BILLING_ERROR_VIEW = "billingErrorView";
    private static final String RESOLVED_PAGE_TITLE = "resolvedPageTitle";
    private static final String REDIRECT_TO_SUMMARY_PAGE = "redirect:/checkout/multi/worldpay/summary/view";
    private static final String CSE_PAYMENT_DETAILS_PAGE = "CSEPaymentDetailsPage";
    private static final String EVENT_ORIGIN_DOMAIN = "EVENT_ORIGIN_DOMAIN";
    private static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN = "originEventDomain3DSFlex";
    private static final String BIRTHDAY_DATE = "birthdayDate";
    private static final Date BIRTHDAY_DATE_VALUE = new Date(1990, Calendar.MAY, 17);
    private static final String DEVICE_SESSION = "DEVICE_SESSION";
    private static final String DEVICE_SESSION_VALUE = "device_session_value";

    @Spy
    @InjectMocks
    private WorldpayB2BCseCheckoutStepController testObj;

    @Mock
    private CMSPreviewService cmsPreviewServiceMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Mock
    private SessionService sessionService;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private WorldpayDDCFacade workdpayDDCFacadeMock;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private Model modelMock;
    @Mock
    private B2BCSEPaymentForm b2bCSEPaymentFormMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock(name = "cseFormValidator")
    private Validator cseFormValidatorMock;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private RedirectAttributes redirectAttrsMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private CartData cartDataMock;
    @Mock(name = "csePaymentDetailsFormValidator")
    private Validator csePaymentDetailsFormValidatorMock;
    @Mock
    private AddressForm addressFormMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
    @Mock(answer = RETURNS_DEEP_STUBS, name = "checkoutFacade")
    private WorldpayCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private CMSPageService cmsPageServiceMock;
    @Mock
    private PageTitleResolver pageTitleResolverMock;
    @Mock
    private CheckoutStep checkoutStepMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoMock;
    @Mock
    private CountryData countryDataMock;
    @Mock
    private Breadcrumb breadcrumbMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private PagePreviewCriteriaData pagePreviewCriteriaMock;
    @Mock
    private Session session;

    @Before
    public void setUp() throws CMSItemNotFoundException {
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        doReturn(BILLING_ERROR_VIEW).when(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        lenient().when(cartDataMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(httpServletRequestMock.getSession().getId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(b2bCSEPaymentFormMock.getDateOfBirth()).thenReturn(BIRTHDAY_DATE_VALUE);
        when(worldpayAdditionalInfoDataMock.getDateOfBirth()).thenReturn(BIRTHDAY_DATE_VALUE);
        lenient().when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(b2bCSEPaymentFormMock.getDeviceSession()).thenReturn(DEVICE_SESSION_VALUE);
        when(worldpayAdditionalInfoDataMock.getDeviceSession()).thenReturn(DEVICE_SESSION_VALUE);
        lenient().when(paymentDetailsFormMock.getBillingAddress()).thenReturn(addressFormMock);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        doReturn(checkoutStepMock).when(testObj).getCheckoutStep();
        doReturn(false).when(testObj).addGlobalErrors(modelMock, bindingResultMock);
        when(worldpayPaymentCheckoutFacadeMock.hasBillingDetails()).thenReturn(true);
        when(userFacadeMock.getCCPaymentInfos(true)).thenReturn(Collections.singletonList(ccPaymentInfoMock));
        when(siteConfigServiceMock.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        lenient().when(cartFacadeMock.getDeliveryCountries()).thenReturn(Collections.singletonList(countryDataMock));
        when(resourceBreadcrumbBuilder.getBreadcrumbs(anyString())).thenReturn(Collections.singletonList(breadcrumbMock));
        lenient().when(pageTitleResolverMock.resolveContentPageTitle(anyString())).thenReturn(RESOLVED_PAGE_TITLE);
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(worldpayAddonEndpointService.getCSEPaymentDetailsPage()).thenReturn(CSE_PAYMENT_DETAILS_PAGE);
        lenient().when(sessionService.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void addCseData_WhenNoExceptionAndFSDisabled_shouldRedirectToSummary() throws CMSItemNotFoundException {
        // When no errors are thrown
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, b2bCSEPaymentFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(REDIRECT_TO_SUMMARY_PAGE, result);
        verifyNoInteractions(redirectAttrsMock);
    }

    @Test
    public void addCseData_WhenNoExceptionAndFSEnabled_shouldRedirectToSummary() throws CMSItemNotFoundException {
        // When no errors are thrown
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, b2bCSEPaymentFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(REDIRECT_TO_SUMMARY_PAGE, result);
        verify(redirectAttrsMock).addFlashAttribute(BIRTHDAY_DATE, BIRTHDAY_DATE_VALUE);
        verify(redirectAttrsMock).addFlashAttribute(DEVICE_SESSION, DEVICE_SESSION_VALUE);
    }

    @Test
    public void shouldRedirectToErrorIfExceptionThrownByFacade() throws CMSItemNotFoundException, WorldpayException {
        doThrow(new WorldpayException("errorMessage")).when(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, b2bCSEPaymentFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(CSE_PAYMENT_DETAILS_PAGE, result);
    }

    @Test
    public void shouldDisplayCsePaymentPageIfErrorTokenizing() throws CMSItemNotFoundException, WorldpayException {
        doThrow(new WorldpayException("errorMessage")).when(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        testObj.addCseData(httpServletRequestMock, modelMock, b2bCSEPaymentFormMock, bindingResultMock, redirectAttrsMock);

        verify(modelMock).addAttribute(eq(B2B_CSE_PAYMENT_FORM), any(CSEPaymentForm.class));
    }

    @Test
    public void shouldValidateCseFormAndRedirectToCseForm() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, b2bCSEPaymentFormMock, bindingResultMock, redirectAttrsMock);

        verify(cseFormValidatorMock).validate(b2bCSEPaymentFormMock, bindingResultMock);
        assertEquals(CSE_PAYMENT_DETAILS_PAGE, result);
    }

    @Test
    public void shouldRedirectToCseDetailsIfNoErrorsInAddressFormAndPaymentMethodCreditCard() throws CMSItemNotFoundException {
        lenient().when(bindingResultMock.hasGlobalErrors()).thenReturn(false);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());

        final String result = testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(REDIRECT_TO_CSE_PAGE, result);
    }

    @Test
    public void shouldRedirectBackToPaymentDetailsIfGlobalErrorsInAddressForm() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        final String result = testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(BILLING_ERROR_VIEW, result);
    }

    @Test
    public void shouldRedirectToPaymentMethodIfNotCreditCardAndNoErrors() throws CMSItemNotFoundException {
        lenient().when(bindingResultMock.hasErrors()).thenReturn(false);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYPAL_EXPRESS.getMethodCode());
        doReturn(REDIRECT_TO_PAYMENT_METHOD).when(testObj).getRedirectToPaymentMethod();

        final String result = testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(redirectAttrsMock).addFlashAttribute(SHOPPER_BANK_CODE, paymentDetailsFormMock.getShopperBankCode());
        verify(redirectAttrsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, PAYPAL_EXPRESS.getMethodCode());
        assertEquals(REDIRECT_TO_PAYMENT_METHOD, result);
    }

    @Test
    public void shouldHandleFormErrorsWhenGlobalErrorsOccur() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);
    }

    @Test
    public void shouldSetBillingDetailsIfValidAddress() throws CMSItemNotFoundException {
        testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
    }

    @Test
    public void shouldValidatePaymentFormUsingCorrectValidator() throws CMSItemNotFoundException {
        testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(csePaymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
    }

    @Test
    public void shouldHandleFormErrorsAndRedirectToErrorView() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        final String result = testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);
        assertEquals(BILLING_ERROR_VIEW, result);
    }

    @Test
    public void shouldCheckForValidCart() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(false);

        final String result = testObj.getCseDataPage(modelMock);

        assertEquals(REDIRECT_PREFIX + "/cart", result);
    }

    @Test
    public void getCseDataPage_ShouldAddNecessaryAttributes_pageModel_eventOriginDomain_cardData_csePublicKey() throws CMSItemNotFoundException {
        when(workdpayDDCFacadeMock.getEventOriginDomainForDDC()).thenReturn(EVENT_ORIGIN_DOMAIN);
        when(worldpayMerchantConfigDataMock.getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY_VALUE);

        testObj.getCseDataPage(modelMock);

        verify(modelMock).addAttribute(CMS_PAGE_MODEL, contentPageModelMock);
        verify(modelMock).addAttribute(THREEDSFLEX_EVENT_ORIGIN_DOMAIN, EVENT_ORIGIN_DOMAIN);
        verify(modelMock).addAttribute(CART_DATA, cartDataMock);
        verify(modelMock).addAttribute(CSE_PUBLIC_KEY, CSE_PUBLIC_KEY_VALUE);
    }

    @Test
    public void shouldPopulateCSEPublicKeyForCseErrorsPage() throws CMSItemNotFoundException {
        when(worldpayMerchantConfigDataMock.getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY_VALUE);

        testObj.getErrorView(modelMock);

        verify(modelMock).addAttribute(CSE_PUBLIC_KEY, CSE_PUBLIC_KEY_VALUE);
    }
}
