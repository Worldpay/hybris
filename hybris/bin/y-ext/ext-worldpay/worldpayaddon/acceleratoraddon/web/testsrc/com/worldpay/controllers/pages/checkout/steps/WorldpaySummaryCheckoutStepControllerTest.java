package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.worldpay.controllers.pages.checkout.steps.WorldpaySummaryCheckoutStepController.*;
import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController.REDIRECT_PREFIX;
import static de.hybris.platform.commercefacades.product.ProductOption.BASIC;
import static de.hybris.platform.commercefacades.product.ProductOption.PRICE;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpaySummaryCheckoutStepControllerTest {

    private static final String SECURITY_CODE = "securityCode";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String MULTI_STEP_CHECKOUT_SUMMARY = "multiStepCheckoutSummary";
    private static final String PAGE_TITLE = "pageTitle";
    private static final String RESOLVED_CONTENT_PAGE_TITLE = "resolvedContentPageTitle";
    private static final String SOME_FLOW_GROUP = "someFlowGroup";
    private static final String SUMMARY = "summary";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String PRODUCT_CODE = "productCode";
    private static final String NOINDEX_NOFOLLOW = "noindex,nofollow";
    private static final String CSE_PAYMENT_FORM = "csePaymentForm";
    private static final String REDIRECT_VIEW = "redirectView";
    private static final String CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE = "/checkout/multi/delivery-method/choose";
    private static final String CHECKOUT_SUMMARY_PAGE = "CheckoutSummaryPage";
    private static final String THREEDSECURE_FLEX_DDC_URL_VALUE = "threeDSecureDDCUrlValue";
    private static final String THREEDSFLEX_JSON_WEB_TOKEN_VALUE = "THREEDSFLEX_JSON_WEB_TOKEN_VALUE";
    private static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN_VALUE = "EVENT_ORIGIN_DOMAIN_VALUE";
    private static final String THREDSFLEX_DDC_PAGE = "ddcIframePage";

    @Spy
    @InjectMocks
    private WorldpaySummaryCheckoutStepController testObj;
    @Mock
    private CMSPageService cmsPageServiceMock;
    @Mock
    private CMSPreviewService cmsPreviewServiceMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private CSEPaymentForm formMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CartModificationData cartModificationDataMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CartData cartDataMock;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private PageTitleResolver pageTitleResolverMock;
    @Mock
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilderMock;
    @Mock(name = "checkoutFlowGroupMap")
    private Map<String, CheckoutGroup> checkoutFlowGroupMapMock;
    @Mock
    private Map<String, CheckoutStep> checkoutStepMapMock;
    @Mock
    private CheckoutGroup checkoutGroupMock;
    @Mock
    private CheckoutStep checkoutStepMock;
    @Mock
    private AddressData deliveryAddressMock;
    @Mock
    private DeliveryModeData deliveryModeDataMock;
    @Mock
    private CCPaymentInfoData paymentInfoMock;
    @Mock
    private OrderEntryData orderEntryDataMock;
    @Mock
    private ProductData productDataMock;
    @Mock
    private ProductFacade productFacadeMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private CartRestorationData cartRestorationDataMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private PagePreviewCriteriaData pagePreviewCriteriaMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private HttpServletResponse responseMock;

    private Model modelMock = new ExtendedModelMap();

    @Before
    public void setUp() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(PRODUCT_CODE, Arrays.asList(BASIC, PRICE))).thenReturn(productDataMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        doNothing().when(testObj).setupAddPaymentPage(modelMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void enterStepShouldReturnCheckoutSummaryPage() throws CMSItemNotFoundException {
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartDataMock.getDeliveryMode()).thenReturn(deliveryModeDataMock);
        when(paymentInfoMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock, responseMock);

        assertEquals(cartDataMock, modelMock.asMap().get(CART_DATA));

        final List<OrderEntryData> orderEntryDatas = (List<OrderEntryData>) modelMock.asMap().get(ALL_ITEMS);
        assertEquals(1, orderEntryDatas.size());
        assertEquals(orderEntryDataMock, orderEntryDatas.get(0));

        assertEquals(deliveryAddressMock, modelMock.asMap().get(DELIVERY_ADDRESS));
        assertEquals(deliveryModeDataMock, modelMock.asMap().get(DELIVERY_MODE));
        assertEquals(paymentInfoMock, modelMock.asMap().get(PAYMENT_INFO));

        assertEquals(true, modelMock.asMap().get(REQUEST_SECURITY_CODE));
        assertNull(((CSEPaymentForm) modelMock.asMap().get(CSE_PAYMENT_FORM)).getSecurityCode());
        assertFalse(((CSEPaymentForm) modelMock.asMap().get(CSE_PAYMENT_FORM)).isTermsCheck());
        assertEquals(contentPageModelMock, modelMock.asMap().get(CMS_PAGE_MODEL));
        assertEquals(EMPTY_LIST, modelMock.asMap().get(BREADCRUMBS_KEY));
        assertEquals(NOINDEX_NOFOLLOW, modelMock.asMap().get(META_ROBOTS));

        assertEquals(StringUtils.remove(checkoutStepMock.previousStep(), REDIRECT_PREFIX), modelMock.asMap().get("previousStepUrl"));
        assertEquals(StringUtils.remove(checkoutStepMock.nextStep(), REDIRECT_PREFIX), modelMock.asMap().get("nextStepUrl"));
        assertEquals(StringUtils.remove(checkoutStepMock.currentStep(), REDIRECT_PREFIX), modelMock.asMap().get("currentStepUrl"));
        assertEquals(checkoutStepMock.getProgressBarId(), modelMock.asMap().get("progressBarId"));

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldRedirectToOrderConfirmationPage() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(worldpayDirectOrderFacadeMock.authoriseRecurringPayment(worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        doReturn(REDIRECT_VIEW).when(testObj).handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_VIEW, result);
        verify(worldpayAdditionalInfoDataMock).setSecurityCode(SECURITY_CODE);
    }

    @Test
    public void shouldRedirectsToSummaryPageOnInvalidCartException() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        doThrow(new InvalidCartException(EXCEPTION_MESSAGE)).when(worldpayDirectOrderFacadeMock).authoriseRecurringPayment(worldpayAdditionalInfoDataMock);
        when(worldpayMerchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getDdcUrl()).thenReturn(THREEDSECURE_FLEX_DDC_URL_VALUE);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldRedirectsToSummaryPageOnWorldpayException() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayDirectOrderFacadeMock).authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldRedirectToCartPageWhenCartIsInvalid() throws CMSItemNotFoundException, CommerceCartModificationException {
        when(cartFacadeMock.validateCartData()).thenReturn(singletonList(cartModificationDataMock));

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CART_SUFFIX, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenSecurityCodeIsBlank() throws CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo().getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(formMock.getSecurityCode()).thenReturn(StringUtils.EMPTY);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenNoDeliveryAddress() throws CMSItemNotFoundException {
        when(checkoutFlowFacadeMock.hasNoDeliveryAddress()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenNoDeliveryMode() throws CMSItemNotFoundException {
        when(checkoutFlowFacadeMock.hasNoDeliveryMode()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenNoPaymentInfo() throws CMSItemNotFoundException {
        when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenTermsAreNotChecked() throws CMSItemNotFoundException {
        // By default, the mock returns false, but we explicitly mock the answer here for the sake of clarity
        when(formMock.isTermsCheck()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenCartDoesNotContainTaxValues() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void shouldStayOnSummaryPageWhenCartIsNotCalculated() throws CMSItemNotFoundException {
        when(cartDataMock.isCalculated()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectToCartPageWhenItIsRestored() throws CMSItemNotFoundException {
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(cartRestorationDataMock);
        when(cartRestorationDataMock.getModifications()).thenReturn(singletonList(cartModificationDataMock));

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CART_SUFFIX, result);
        verify(checkoutFlowFacadeMock, never()).hasValidCart();
    }

    @Test
    public void performExpressCheckoutShouldRedirectSummaryPageWhenCartIsValidAndPerformExpressCheckoutIsSUCCESS() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.SUCCESS);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectAddDeliveryAddressWhenCartIsValidAndPerformExpressCheckoutIsErrorDeliveryAddress() throws
            CMSItemNotFoundException {
        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_DELIVERY_ADDRESS);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + "/checkout/multi/delivery-address/add", result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectChooseDeliveryMethodWhenCartIsValidAndPerformExpressCheckoutIsErrorDeliveryMode() throws
            CMSItemNotFoundException {
        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_DELIVERY_MODE);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE, result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectChooseDeliveryMethodWhenCartIsValidAndPerformExpressCheckoutIsCheapestDeliveryMode() throws
            CMSItemNotFoundException {
        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_CHEAPEST_DELIVERY_MODE);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE, result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectChoosePaymentMethodWhenCartIsValidAndPerformExpressCheckoutIsErrorPaymentInfo() throws
            CMSItemNotFoundException {
        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_PAYMENT_INFO);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectCartPageWhenCartIsValidAndPerformExpressCheckoutIsErrorNotAvailable() throws
            CMSItemNotFoundException {
        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_NOT_AVAILABLE);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CART_SUFFIX, result);
    }

    @Test
    public void shouldPopulate3DSecureJsonWebToken() {
        when(worldpayDirectOrderFacadeMock.createJsonWebTokenForDDC()).thenReturn(THREEDSFLEX_JSON_WEB_TOKEN_VALUE);
        when(worldpayDirectOrderFacadeMock.getEventOriginDomainForDDC()).thenReturn(THREEDSFLEX_EVENT_ORIGIN_DOMAIN_VALUE);
        when(worldpayAddonEndpointServiceMock.getDdcIframe3dSecureFlex()).thenReturn(THREDSFLEX_DDC_PAGE);
        when(worldpayMerchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getDdcUrl()).thenReturn(THREEDSECURE_FLEX_DDC_URL_VALUE);

        final String result = testObj.getDDCIframeContent(modelMock);

        assertThat(result).isEqualTo(THREDSFLEX_DDC_PAGE);
    }
}
