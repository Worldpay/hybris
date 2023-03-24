package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.WorldpayCartFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.internal.model.KLARNAV2SSL;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
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
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayDirectCheckoutStepController.BIRTHDAY_DATE;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.*;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayPaymentMethodCheckoutStepController.REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayPaymentMethodCheckoutStepController.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayPaymentMethodCheckoutStepControllerTest {

    private static final String CHECKOUT_FLOW_GROUP_KEY = "checkoutFlowGroupKey";
    private static final String PAYMENT_METHOD_PARAM = "paymentMethod";
    private static final String PAYMENT_METHOD_VALUE = "paymentMethodValue";
    private static final String PAYMENT_METHOD_BANK_TRANSFER = "PAYMENT_METHOD_BANK_TRANSFER";
    private static final String PAYMENT_METHOD_KLARNA = "KLARNA_V2-SSL";
    private static final String SHOPPER_BANK_CODE_VALUE = "shopperBankCode";
    private static final String REDIRECT_URL = "redirectUrl";
    private static final String HOSTED_ORDER_PAGE = "hostedOrderPostPage";
    private static final String KLARNA_RESPONSE = "klarnaContent";
    private static final String KLARNA_RESPONSE_PAGE_DATA_PARAM = "KLARNA_VIEW_DATA";
    private static final String KLARNA_RESPONSE_PAGE = "pages/klarna/klarnaResponseContentPage";
    private static final Date BIRTHDAY_DATE_VALUE = new Date(1990, Calendar.MAY, 17);

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
    private CartFacade cartFacadeMock;
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

    @Mock(answer = RETURNS_DEEP_STUBS)
    private CheckoutGroup checkoutGroupMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Model modelMock;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private CheckoutStep checkoutStepMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private Map<String, String> paymentDataParametersMock;
    @Mock
    private Map<String, CheckoutStep> checkoutStepMapMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private CMSPreviewService cmsPreviewServiceMock;
    @Mock
    private WorldpayCartFacade worldpayCartFacadeMock;

    @Before
    public void setUp() throws Exception {
        doReturn(additionalAuthInfoMock).when(testObj).createAdditionalAuthInfo(anyBoolean(), anyString());

        when(cmsPageServiceMock.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModelMock);
        when(contentPageModelMock.getTitle()).thenReturn(CMS_PAGE_TITLE);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(anyString())).thenReturn(checkoutStepMock);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(paymentDataMock);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_VALUE);
        when(modelMock.asMap().get(SAVE_PAYMENT_INFO)).thenReturn(true);
        when(modelMock.asMap().get(SHOPPER_BANK_CODE)).thenReturn(SHOPPER_BANK_CODE_VALUE);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(paymentDataMock.getParameters()).thenReturn(paymentDataParametersMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(siteConfigServiceMock.getBoolean(HOP_DEBUG_MODE_CONFIG, false)).thenReturn(false);
        when(worldpayAddonEndpointServiceMock.getHostedOrderPostPage()).thenReturn(HOSTED_ORDER_PAGE);
        when(modelMock.asMap().get(BIRTHDAY_DATE)).thenReturn(BIRTHDAY_DATE_VALUE);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
    }

    @Test
    public void testEnterStepForCard() throws CMSItemNotFoundException {
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
    public void testEnterStepRedirectsToBankTransfer() throws CMSItemNotFoundException, WorldpayException {
        doReturn(false).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD_VALUE);
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
    public void testEnterStepRedirectToHopPageIfNotBankTransferPayment() throws CMSItemNotFoundException {
        doReturn(false).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD_VALUE);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_VALUE);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(paymentDataParametersMock).put(PREFERRED_PAYMENT_METHOD_PARAM, PAYMENT_METHOD_VALUE);
        assertEquals(HOSTED_ORDER_PAGE, result);
    }

    @Test
    public void shouldReturnToChoosePaymentMethodOnWorldpayException() throws WorldpayException, CMSItemNotFoundException {
        doThrow(WorldpayException.class).when(worldpayHostedOrderFacadeMock).redirectAuthorise(any(), any());

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    public void shouldRedirectToInternalUrlWithKlarnaContentWhenPaymentTypeIsKlarna() throws Exception {
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_KLARNA);
        when(worldpayDirectOrderFacadeMock.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(KLARNA_RESPONSE);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(paymentDataParametersMock).put(PREFERRED_PAYMENT_METHOD_PARAM, PaymentType.KLARNAV2SSL.getMethodCode());
        assertEquals(HOSTED_ORDER_PAGE, result);
    }
}
