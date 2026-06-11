package com.worldpay.controllers.pages.checkout.steps;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayPaymentMethodCheckoutStepControllerTest {

    private static final String REQUEST = "request";
    private static final String ORDER_CODE = "orderCode";
    private static final String REDIRECT_URL = "redirectUrl";
    private static final String CMS_PAGE_TITLE = "pageTitle";
    private static final String BIRTHDAY_DATE = "birthdayDate";
    private static final String HOP_DEBUG_MODE_PARAM = "hopDebugMode";
    private static final String SAVE_PAYMENT_INFO = "savePaymentInfo";
    private static final String SHOPPER_BANK_CODE = "shopperBankCode";
    private static final String PAYMENT_METHOD_PARAM = "paymentMethod";
    private static final String PAYMENT_METHOD_KLARNA = "KLARNA_V2-SSL";
    private static final String HOSTED_ORDER_PAGE = "hostedOrderPostPage";
    private static final String PAYMENT_METHOD_VALUE = "paymentMethodValue";
    private static final String SHOPPER_BANK_CODE_VALUE = "shopperBankCode";
    private static final String HOSTED_ORDER_PAGE_DATA = "hostedOrderPageData";
    private static final String CHECKOUT_FLOW_GROUP_KEY = "checkoutFlowGroupKey";
    private static final String PREFERRED_PAYMENT_METHOD_PARAM = "preferredPaymentMethod";
    private static final String PAYMENT_METHOD_BANK_TRANSFER = "PAYMENT_METHOD_BANK_TRANSFER";
    private static final String CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB = "checkout.multi.paymentMethod.breadcrumb";
    private static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = "redirect:/checkout/multi/worldpay/choose-payment-method";

    private static final LocalDate BIRTHDAY_DATE_VALUE = LocalDate.of(1990, 5, 17);

    @Spy
    @InjectMocks
    private WorldpayPaymentMethodCheckoutStepController testObj;

    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilderMock;
    @Mock
    private CMSPageService cmsPageServiceMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private PageTitleResolver pageTitleResolver;
    @Mock(name = "checkoutFlowGroupMap")
    private Map<String, CheckoutGroup> checkoutGroupMap;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayBankConfigurationFacade bankConfigurationFacadeMock;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CheckoutGroup checkoutGroupMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Model modelMock;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private Map<String, String> paymentDataParametersMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    @SuppressWarnings("unused")
    private CMSPreviewService cmsPreviewServiceMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private OrderData orderDataMock;

    @Test
    void testEnterStepForCard() throws CMSItemNotFoundException, WorldpayException {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(contentPageModelMock.getTitle()).thenReturn(CMS_PAGE_TITLE);
        when(siteConfigServiceMock.getBoolean("oms.enabled", false)).thenReturn(false);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_VALUE);
        when(modelMock.asMap().get(SAVE_PAYMENT_INFO)).thenReturn(true);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(any(), eq(worldpayAdditionalInfoDataMock))).thenReturn(paymentDataMock);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayAddonEndpointServiceMock.getHostedOrderPostPage()).thenReturn(HOSTED_ORDER_PAGE);
        when(modelMock.asMap().get(BIRTHDAY_DATE)).thenReturn(BIRTHDAY_DATE_VALUE);

        doReturn(true).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD_VALUE);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(siteConfigServiceMock).getBoolean("oms.enabled", false);
        verify(checkoutFlowFacadeMock).hasNoPaymentInfo();
        verify(acceleratorCheckoutFacadeMock).getCountries(CountryType.SHIPPING);
        verify(acceleratorCheckoutFacadeMock).getCountries(CountryType.BILLING);
        verify(resourceBreadcrumbBuilderMock).getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB);
        verify(pageTitleResolver).resolveContentPageTitle(CMS_PAGE_TITLE);
        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(modelMock).addAttribute(HOP_DEBUG_MODE_PARAM, false);
        verify(paymentDataParametersMock, never()).put(eq(PREFERRED_PAYMENT_METHOD_PARAM), any());
        assertEquals(HOSTED_ORDER_PAGE, result);
        verify(worldpayAdditionalInfoDataMock).setDateOfBirth(BIRTHDAY_DATE_VALUE);
    }

    @Test
    void testEnterStepRedirectsToBankTransfer() throws CMSItemNotFoundException, WorldpayException {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(contentPageModelMock.getTitle()).thenReturn(CMS_PAGE_TITLE);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(modelMock.asMap().get(SHOPPER_BANK_CODE)).thenReturn(SHOPPER_BANK_CODE_VALUE);

        doReturn(false).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD_BANK_TRANSFER);

        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_BANK_TRANSFER);
        when(bankConfigurationFacadeMock.isBankTransferApm(PAYMENT_METHOD_BANK_TRANSFER)).thenReturn(true);
        when(worldpayDirectOrderFacadeMock.authoriseBankTransferRedirect(any(BankTransferAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock))).thenReturn(REDIRECT_URL);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(siteConfigServiceMock).getBoolean("oms.enabled", false);
        verify(checkoutFlowFacadeMock).hasNoPaymentInfo();
        verify(acceleratorCheckoutFacadeMock).getCountries(CountryType.SHIPPING);
        verify(acceleratorCheckoutFacadeMock).getCountries(CountryType.BILLING);
        verify(worldpayPaymentCheckoutFacadeMock).hasBillingDetails();
        verify(resourceBreadcrumbBuilderMock).getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB);
        verify(pageTitleResolver).resolveContentPageTitle(CMS_PAGE_TITLE);

        assertEquals("redirect:" + REDIRECT_URL, result);
    }

    @Test
    void testEnterStepRedirectsToACHDirectDebitPayment() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        doReturn(directResponseDataMock).when(worldpayDirectOrderFacadeMock).authoriseACHDirectDebit(any(), any());
        when(worldpayDirectResponseFacadeMock.isAuthorised(directResponseDataMock)).thenReturn(true);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);

        doReturn(false).when(checkoutCustomerStrategyMock).isAnonymousCheckout();

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(worldpayDirectOrderFacadeMock).authoriseACHDirectDebit(any(), any());
        assertEquals("redirect:/checkout/orderConfirmation/orderCode", result);

    }

    @Test
    void testEnterStepRedirectToHopPageIfNotBankTransferPayment() throws CMSItemNotFoundException, WorldpayException {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(modelMock.asMap().get(SAVE_PAYMENT_INFO)).thenReturn(true);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(any(), eq(worldpayAdditionalInfoDataMock))).thenReturn(paymentDataMock);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayAddonEndpointServiceMock.getHostedOrderPostPage()).thenReturn(HOSTED_ORDER_PAGE);
        when(paymentDataMock.getParameters()).thenReturn(paymentDataParametersMock);

        doReturn(false).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD_VALUE);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_VALUE);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(paymentDataParametersMock).put(PREFERRED_PAYMENT_METHOD_PARAM, PAYMENT_METHOD_VALUE);
        assertEquals(HOSTED_ORDER_PAGE, result);
    }

    @Test
    void shouldReturnToChoosePaymentMethodOnWorldpayException() throws WorldpayException, CMSItemNotFoundException {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(contentPageModelMock.getTitle()).thenReturn(CMS_PAGE_TITLE);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_VALUE);
        when(modelMock.asMap().get(SAVE_PAYMENT_INFO)).thenReturn(true);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);

        doThrow(WorldpayException.class).when(worldpayHostedOrderFacadeMock).redirectAuthorise(any(), any());

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    void shouldRedirectToInternalUrlWithKlarnaContentWhenPaymentTypeIsKlarna() throws Exception {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(modelMock.asMap().get(SAVE_PAYMENT_INFO)).thenReturn(true);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(any(), eq(worldpayAdditionalInfoDataMock))).thenReturn(paymentDataMock);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayAddonEndpointServiceMock.getHostedOrderPostPage()).thenReturn(HOSTED_ORDER_PAGE);
        when(paymentDataMock.getParameters()).thenReturn(paymentDataParametersMock);

        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_KLARNA);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(paymentDataParametersMock).put(PREFERRED_PAYMENT_METHOD_PARAM, PaymentType.KLARNAV2SSL.getMethodCode());
        assertEquals(HOSTED_ORDER_PAGE, result);
    }
}
