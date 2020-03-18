package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
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
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayDirectCheckoutStepController.CMS_PAGE_MODEL;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.*;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayCseCheckoutStepController.*;
import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static com.worldpay.service.model.payment.PaymentType.PAYPAL;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController.REDIRECT_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCseCheckoutStepControllerTest {

    private static final String CHECKOUT_ORDER_CONFIRMATION = "/checkout/orderConfirmation/";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CSE_PUBLIC_KEY_VALUE = "csePublicKeyValue";
    private static final String REDIRECT_TO_PAYMENT_METHOD = "redirectToPaymentMethod";
    private static final String CART_DATA = "cartData";
    private static final String SESSION_ID = "sessionID";
    private static final String BILLING_ERROR_VIEW = "billingErrorView";
    private static final String RESOLVED_PAGE_TITLE = "resolvedPageTitle";
    private static final String CSE_PAYMENT_DETAILS_PAGE = "CSEPaymentDetailsPage";
    private static final String THREEDSFLEX_JSON_WEB_TOKEN_VALUE = "THREEDSFLEX_JSON_WEB_TOKEN_VALUE";
    private static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN_VALUE = "EVENT_ORIGIN_DOMAIN_VALUE";
    private static final String CVC = "111";
    private static final String ID_REFERENCE = "idReference";
    private static final String WINDOW_SIZE = "windowSize";
    private static final String THREDSFLEX_DDC_PAGE = "ddcIframePage";
    private static final String THREEDS_FLEX_CHALLENGE_IFRAME = "threeDSFlexIframePage";
    private static final String THREEDSECURE_FLEX_DDC_URL_VALUE = "threeDSecureDDCUrlValue";

    @Spy
    @InjectMocks
    private WorldpayCseCheckoutStepController testObj;

    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private CMSPreviewService cmsPreviewServiceMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private Model modelMock;
    @Mock
    private CSEPaymentForm csePaymentFormMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock(name = "cseFormValidator")
    private Validator cseFormValidatorMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
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
    private Breadcrumb breadcrumbMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private PagePreviewCriteriaData pagePreiewCriteriaMock;
    @Mock
    private Additional3DS2Info additional3DS2InfoMock;
    @Mock
    private HttpServletResponse responseMock;

    @Before
    public void setUp() throws CMSItemNotFoundException {
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        doReturn(BILLING_ERROR_VIEW).when(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(httpServletRequestMock.getSession().getId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(addressFormMock);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreiewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL, pagePreiewCriteriaMock)).thenReturn(contentPageModelMock);
        doReturn(checkoutStepMock).when(testObj).getCheckoutStep();
        doReturn(false).when(testObj).addGlobalErrors(modelMock, bindingResultMock);
        when(worldpayPaymentCheckoutFacadeMock.hasBillingDetails()).thenReturn(true);
        when(userFacadeMock.getCCPaymentInfos(true)).thenReturn(Collections.singletonList(ccPaymentInfoMock));
        when(siteConfigServiceMock.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(resourceBreadcrumbBuilder.getBreadcrumbs(anyString())).thenReturn(Collections.singletonList(breadcrumbMock));
        when(pageTitleResolverMock.resolveContentPageTitle(anyString())).thenReturn(RESOLVED_PAGE_TITLE);
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(worldpayAddonEndpointServiceMock.getCSEPaymentDetailsPage()).thenReturn(CSE_PAYMENT_DETAILS_PAGE);
    }

    @Test
    public void addCseData_shouldRedirectToOrderConfirmation_WhenThereIsNotExceptionsOrErrors() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        doReturn(CHECKOUT_ORDER_CONFIRMATION).when(testObj).handleDirectResponse(modelMock, directResponseDataMock, responseMock);
        when(worldpayDirectOrderFacadeMock.authoriseAndTokenize(eq(worldpayAdditionalInfoDataMock), any(CSEAdditionalAuthInfo.class))).thenReturn(directResponseDataMock);

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, csePaymentFormMock, bindingResultMock, responseMock);

        assertEquals(CHECKOUT_ORDER_CONFIRMATION, result);
    }

    @Test
    public void addCseData_ShouldRedirectToError_WhenAnExceptionIsThrownByFacade() throws CMSItemNotFoundException, InvalidCartException, WorldpayException {
        when(worldpayDirectOrderFacadeMock.authoriseAndTokenize(eq(worldpayAdditionalInfoDataMock), any(CSEAdditionalAuthInfo.class))).thenThrow(new WorldpayException("errorMessage"));

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, csePaymentFormMock, bindingResultMock, responseMock);

        assertEquals(CSE_PAYMENT_DETAILS_PAGE, result);
    }

    @Test
    public void addCseData_ShouldDisplayCsePaymentPage_WhenThereIsAnErrorAuthorisingPayment() throws CMSItemNotFoundException, InvalidCartException, WorldpayException {
        when(worldpayDirectOrderFacadeMock.authoriseAndTokenize(eq(worldpayAdditionalInfoDataMock), any(CSEAdditionalAuthInfo.class))).thenThrow(new WorldpayException("errorMessage"));

        testObj.addCseData(httpServletRequestMock, modelMock, csePaymentFormMock, bindingResultMock, responseMock);

        verify(modelMock).addAttribute(eq(CSE_PAYMENT_FORM), any(CSEPaymentForm.class));
    }

    @Test
    public void shouldValidateCseFormAndRedirectToCseForm() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        final String result = testObj.addCseData(httpServletRequestMock, modelMock, csePaymentFormMock, bindingResultMock, responseMock);

        verify(cseFormValidatorMock).validate(csePaymentFormMock, bindingResultMock);
        assertEquals(CSE_PAYMENT_DETAILS_PAGE, result);
    }

    @Test
    public void shouldRedirectToCseDetailsIfNoErrorsInAddressFormAndPaymentMethodCreditCard() throws CMSItemNotFoundException {
        when(bindingResultMock.hasGlobalErrors()).thenReturn(false);
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
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYPAL.getMethodCode());
        doReturn(REDIRECT_TO_PAYMENT_METHOD).when(testObj).getRedirectToPaymentMethod();

        final String result = testObj.addPaymentAddress(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(redirectAttrsMock).addFlashAttribute(SHOPPER_BANK_CODE, paymentDetailsFormMock.getShopperBankCode());
        verify(redirectAttrsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, PAYPAL.getMethodCode());
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
    public void shouldAddCMSContentForPageToModel() throws CMSItemNotFoundException {
        testObj.getCseDataPage(modelMock);

        verify(modelMock).addAttribute(CMS_PAGE_MODEL, contentPageModelMock);
    }

    @Test
    public void shouldAddCartToModel() throws CMSItemNotFoundException {
        testObj.getCseDataPage(modelMock);

        verify(modelMock).addAttribute(CART_DATA, cartDataMock);
    }

    @Test
    public void shouldPopulateCSEPublicKey() throws CMSItemNotFoundException {
        when(worldpayMerchantConfigDataMock.getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY_VALUE);

        testObj.getCseDataPage(modelMock);

        verify(modelMock).addAttribute(CSE_PUBLIC_KEY, CSE_PUBLIC_KEY_VALUE);
    }

    @Test
    public void shouldPopulateCSEPublicKeyForCseErrorsPage() throws CMSItemNotFoundException {
        when(worldpayMerchantConfigDataMock.getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY_VALUE);

        testObj.getErrorView(modelMock);

        verify(modelMock).addAttribute(CSE_PUBLIC_KEY, CSE_PUBLIC_KEY_VALUE);
    }

    @Test
    public void shouldPopulate3DSecureJsonWebToken() {
        when(worldpayDirectOrderFacadeMock.createJsonWebTokenForDDC()).thenReturn(THREEDSFLEX_JSON_WEB_TOKEN_VALUE);
        when(worldpayDirectOrderFacadeMock.getEventOriginDomainForDDC()).thenReturn(THREEDSFLEX_EVENT_ORIGIN_DOMAIN_VALUE);
        when(worldpayAddonEndpointServiceMock.getDdcIframe3dSecureFlex()).thenReturn(THREDSFLEX_DDC_PAGE);
        when(worldpayMerchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getDdcUrl()).thenReturn(THREEDSECURE_FLEX_DDC_URL_VALUE);

        final String result = testObj.getDDCIframeContent(modelMock);

        verify(modelMock).addAttribute(THREEDSECURE_JWT_FLEX_DDC, THREEDSFLEX_JSON_WEB_TOKEN_VALUE);
        verify(modelMock).addAttribute(THREEDSECURE_FLEX_DDC_URL, THREEDSECURE_FLEX_DDC_URL_VALUE);
        assertThat(result).isEqualTo(THREDSFLEX_DDC_PAGE);
    }

    @Test
    public void shouldCreateWorldpayAdditionalInfo() {
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(cseAdditionalAuthInfoMock.getAdditional3DS2()).thenReturn(additional3DS2InfoMock);
        when(cseAdditionalAuthInfoMock.getAdditional3DS2().getDfReferenceId()).thenReturn(ID_REFERENCE);
        when(cseAdditionalAuthInfoMock.getAdditional3DS2().getChallengeWindowSize()).thenReturn(WINDOW_SIZE);

        testObj.createWorldpayAdditionalInfo(httpServletRequestMock, CVC, cseAdditionalAuthInfoMock);

        verify(worldpayAdditionalInfoFacadeMock).createWorldpayAdditionalInfoData(httpServletRequestMock);
        verify(worldpayAdditionalInfoDataMock).setSecurityCode(CVC);
        verify(worldpayAdditionalInfoDataMock).setAdditional3DS2(additional3DS2InfoMock);
    }

    @Test
    public void shouldRenderIframePageForChallenge() {
        when(worldpayAddonEndpointServiceMock.getChallengeIframe3dSecureFlex()).thenReturn(THREEDS_FLEX_CHALLENGE_IFRAME);

        final String result = testObj.getChallengeIframeContent(modelMock);

        assertThat(result).isEqualTo(THREEDS_FLEX_CHALLENGE_IFRAME);
    }
}
