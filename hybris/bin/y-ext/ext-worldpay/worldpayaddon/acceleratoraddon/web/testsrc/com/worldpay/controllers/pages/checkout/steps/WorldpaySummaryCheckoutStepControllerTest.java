package com.worldpay.controllers.pages.checkout.steps;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
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
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class WorldpaySummaryCheckoutStepControllerTest {

    private static final String BIN = "bin";
    private static final String NEXT = "next";
    private static final String SUMMARY = "summary";
    private static final String BIN_VALUE = "78954";
    private static final String PREVIOUS = "previous";
    private static final String CART_SUFFIX = "/cart";
    private static final String CART_DATA = "cartData";
    private static final String ALL_ITEMS = "allItems";
    private static final String NEXT_PAGE = "/nextPage";
    private static final String PAGE_TITLE = "pageTitle";
    private static final String META_ROBOTS = "metaRobots";
    private static final String CMS_PAGE_MODEL = "cmsPage";
    private static final String BREADCRUMBS = "breadcrumbs";
    private static final String PAYMENT_INFO = "paymentInfo";
    private static final String PRODUCT_CODE = "productCode";
    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String DELIVERY_MODE = "deliveryMode";
    private static final String REDIRECT_VIEW = "redirectView";
    private static final String SECURITY_CODE = "securityCode";
    private static final String PREVIOUS_PAGE = "/previousPage";
    private static final String SOME_FLOW_GROUP = "someFlowGroup";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String CSE_PAYMENT_FORM = "csePaymentForm";
    private static final String DELIVERY_ADDRESS = "deliveryAddress";
    private static final String NOINDEX_NOFOLLOW = "noindex,nofollow";
    private static final String THREDSFLEX_DDC_PAGE = "ddcIframePage";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String CHECKOUT_SUMMARY_PAGE = "CheckoutSummaryPage";
    private static final String REQUEST_SECURITY_CODE = "requestSecurityCode";
    private static final String MULTI_STEP_CHECKOUT_SUMMARY = "multiStepCheckoutSummary";
    private static final String RESOLVED_CONTENT_PAGE_TITLE = "resolvedContentPageTitle";
    private static final String THREEDSFLEX_EVENT_ORIGIN_DOMAIN_VALUE = "EVENT_ORIGIN_DOMAIN_VALUE";
    private static final String THREEDSFLEX_JSON_WEB_TOKEN_VALUE = "THREEDSFLEX_JSON_WEB_TOKEN_VALUE";
    private static final String CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE = "/checkout/multi/delivery-method/choose";
    private static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = "redirect:/checkout/multi/worldpay/choose-payment-method";
    private static final String THREE_DS_FLEX_IFRAME = "3DSFlexChallengeIframe";

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
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayDDCFacade worldpayDDCFacadeMock;

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
    @Mock(name = "checkoutFacade")
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
    private PagePreviewCriteriaData pagePreviewCriteriaMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private HttpServletResponse responseMock;

    private final Map<String, String> transitions = Map.of(PREVIOUS, PREVIOUS_PAGE, NEXT, NEXT_PAGE);

    private final Model modelMock = new ExtendedModelMap();

    @Test
    @SuppressWarnings("unchecked")
    public void enterStepShouldReturnCheckoutSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartDataMock.getDeliveryMode()).thenReturn(deliveryModeDataMock);
        when(paymentInfoMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        when(checkoutStepMock.previousStep()).thenReturn(PREVIOUS);
        when(checkoutStepMock.nextStep()).thenReturn(NEXT);
        when(checkoutStepMock.currentStep()).thenReturn(SUMMARY);
        when(checkoutStepMock.getProgressBarId()).thenReturn(CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock, responseMock);

        assertEquals(cartDataMock, modelMock.asMap().get(CART_DATA));

        final List<OrderEntryData> orderEntryDatas = (List<OrderEntryData>) modelMock.asMap().get(ALL_ITEMS);
        assertEquals(1, orderEntryDatas.size());
        assertEquals(orderEntryDataMock, orderEntryDatas.getFirst());

        assertEquals(deliveryAddressMock, modelMock.asMap().get(DELIVERY_ADDRESS));
        assertEquals(deliveryModeDataMock, modelMock.asMap().get(DELIVERY_MODE));
        assertEquals(BIN_VALUE, modelMock.asMap().get(BIN));
        assertEquals(paymentInfoMock, modelMock.asMap().get(PAYMENT_INFO));

        assertEquals(true, modelMock.asMap().get(REQUEST_SECURITY_CODE));
        Assertions.assertNull(((CSEPaymentForm) modelMock.asMap().get(CSE_PAYMENT_FORM)).getSecurityCode());
        Assertions.assertFalse(((CSEPaymentForm) modelMock.asMap().get(CSE_PAYMENT_FORM)).isTermsCheck());
        assertEquals(contentPageModelMock, modelMock.asMap().get(CMS_PAGE_MODEL));
        assertEquals(emptyList(), modelMock.asMap().get(BREADCRUMBS));
        assertEquals(NOINDEX_NOFOLLOW, modelMock.asMap().get(META_ROBOTS));

        assertEquals(checkoutStepMock.previousStep().replace(REDIRECT_PREFIX, ""), modelMock.asMap().get("previousStepUrl"));
        assertEquals(checkoutStepMock.nextStep().replace(REDIRECT_PREFIX, ""), modelMock.asMap().get("nextStepUrl"));
        assertEquals(checkoutStepMock.currentStep().replace(REDIRECT_PREFIX, ""), modelMock.asMap().get("currentStepUrl"));
        assertEquals(checkoutStepMock.getProgressBarId(), modelMock.asMap().get("progressBarId"));

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void enterStep_IfPaymentInfoIsNull_ShouldNotThrowException() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);
        when(cartDataMock.getPaymentInfo()).thenReturn(null);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock, responseMock);

        Assertions.assertNull(modelMock.asMap().get(PAYMENT_INFO));
        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_shouldRedirectToOrderConfirmationPage() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(worldpayDirectOrderFacadeMock.authoriseRecurringPayment(worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        doReturn(REDIRECT_VIEW).when(testObj).handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_VIEW, result);
        verify(worldpayAdditionalInfoDataMock).setSecurityCode(SECURITY_CODE);
    }

    @Test
    public void placeOrder_OnInvalidCartException_ShouldRedirectsToSummaryPage() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        doNothing().when(testObj).setupAddPaymentPage(modelMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        doThrow(new InvalidCartException(EXCEPTION_MESSAGE)).when(worldpayDirectOrderFacadeMock).authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_OnWorldpayException_ShouldRedirectsToSummaryPage() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        doNothing().when(testObj).setupAddPaymentPage(modelMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayDirectOrderFacadeMock).authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenCartIsInvalid_ShouldRedirectToCartPage() throws CMSItemNotFoundException, CommerceCartModificationException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));

        when(cartFacadeMock.validateCartData()).thenReturn(singletonList(cartModificationDataMock));

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CART_SUFFIX, result);
    }

    @Test
    public void placeOrder_WhenSecurityCodeIsBlankAndPaymentInfoIsNotAPM_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);
        when(cartDataMock.getPaymentInfo().getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(formMock.getSecurityCode()).thenReturn(StringUtils.EMPTY);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenSecurityCodeIsBlankAndPaymentInfoIsAPM_ShouldRedirectToOrderConfirmationPage() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(cartDataMock.isCalculated()).thenReturn(true);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(worldpayDirectOrderFacadeMock.authoriseRecurringPayment(worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        doReturn(REDIRECT_VIEW).when(testObj).handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_VIEW, result);
    }

    @Test
    public void placeOrder_WhenNoDeliveryAddress_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFlowFacadeMock.hasNoDeliveryAddress()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenNoDeliveryMode_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFlowFacadeMock.hasNoDeliveryMode()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenNoPaymentInfo_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenTermsAreNotChecked_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        // By default, the mock returns false, but we explicitly mock the answer here for the sake of clarity
        when(formMock.isTermsCheck()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenCartDoesNotContainTaxValues_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.containsTaxValues()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenCartIsNotCalculated_ShouldStayOnSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(cartDataMock.isCalculated()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelMock, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void performExpressCheckout_WhenItIsRestored_ShouldRedirectToCartPage() throws CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);

        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(cartRestorationDataMock);
        when(cartRestorationDataMock.getModifications()).thenReturn(singletonList(cartModificationDataMock));

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CART_SUFFIX, result);
        verify(checkoutFlowFacadeMock, never()).hasValidCart();
    }

    @Test
    public void performExpressCheckout_WhenCartIsValidAndPerformExpressCheckoutIsSUCCESS_ShouldRedirectSummaryPage() throws CMSItemNotFoundException {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_STEP_CHECKOUT_SUMMARY, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        when(productFacadeMock.getProductForCodeAndOptions(eq(PRODUCT_CODE), anyList())).thenReturn(productDataMock);
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.SUCCESS);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void performExpressCheckout_WhenCartIsValidAndPerformExpressCheckoutIsErrorDeliveryAddress_ShouldRedirectAddDeliveryAddress() throws
            CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_DELIVERY_ADDRESS);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + "/checkout/multi/delivery-address/add", result);
    }

    @Test
    public void performExpressCheckoutShouldRedirectChooseDeliveryMethodWhenCartIsValidAndPerformExpressCheckoutIsErrorDeliveryMode() throws
            CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_DELIVERY_MODE);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE, result);
    }

    @Test
    public void performExpressCheckout_WhenCartIsValidAndPerformExpressCheckoutIsCheapestDeliveryMode_ShouldRedirectChooseDeliveryMethod() throws
            CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_CHEAPEST_DELIVERY_MODE);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CHECKOUT_MULTI_DELIVERY_METHOD_CHOOSE, result);
    }

    @Test
    public void performExpressCheckout_WhenCartIsValidAndPerformExpressCheckoutIsErrorPaymentInfo_ShouldRedirectChoosePaymentMethod() throws
            CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_PAYMENT_INFO);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    public void performExpressCheckout_WhenCartIsValidAndPerformExpressCheckoutIsErrorNotAvailable_ShouldRedirectCartPage() throws
            CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        when(checkoutFacadeMock.performExpressCheckout()).thenReturn(AcceleratorCheckoutFacade.ExpressCheckoutResult.ERROR_NOT_AVAILABLE);

        final String result = testObj.performExpressCheckout(modelMock, redirectAttributesMock, responseMock);

        assertEquals(REDIRECT_PREFIX + CART_SUFFIX, result);
    }

    @Test
    public void getDDCIframeContent_shouldPopulate3DSecureJsonWebToken() throws CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);

        when(worldpayDDCFacadeMock.createJsonWebTokenForDDC()).thenReturn(THREEDSFLEX_JSON_WEB_TOKEN_VALUE);
        lenient().when(worldpayDDCFacadeMock.getEventOriginDomainForDDC()).thenReturn(THREEDSFLEX_EVENT_ORIGIN_DOMAIN_VALUE);
        when(worldpayAddonEndpointServiceMock.getDdcIframe3dSecureFlex()).thenReturn(THREDSFLEX_DDC_PAGE);

        final String result = testObj.getDDCIframeContent(modelMock);

        assertThat(result).isEqualTo(THREDSFLEX_DDC_PAGE);
    }

    @Test
    public void getChallengeIframeContent_ShouldReturnTheWorldpayChallengeIframe() {
        when(worldpayAddonEndpointServiceMock.getChallengeIframe3dSecureFlex()).thenReturn(THREE_DS_FLEX_IFRAME);

        final String result = testObj.getChallengeIframeContent(modelMock);

        assertThat(result).isEqualTo(THREE_DS_FLEX_IFRAME);
    }

    @Test
    public void back_ShouldReturnThePreviousPage() {
        when(checkoutStepMock.getTransitions()).thenReturn(transitions);
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        doCallRealMethod().when(checkoutStepMock).previousStep();
        doCallRealMethod().when(checkoutStepMock).go(PREVIOUS);

        final String result = testObj.back(redirectAttributesMock);

        assertThat(result).isEqualTo(PREVIOUS_PAGE);
    }

    @Test
    public void next_ShouldReturnTheNextPage() throws CMSItemNotFoundException {
        when(checkoutStepMock.getTransitions()).thenReturn(transitions);
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(SOME_FLOW_GROUP);
        when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);

        doCallRealMethod().when(checkoutStepMock).nextStep();
        doCallRealMethod().when(checkoutStepMock).go(NEXT);

        final String result = testObj.next(redirectAttributesMock);

        assertThat(result).isEqualTo(NEXT_PAGE);
    }
}
