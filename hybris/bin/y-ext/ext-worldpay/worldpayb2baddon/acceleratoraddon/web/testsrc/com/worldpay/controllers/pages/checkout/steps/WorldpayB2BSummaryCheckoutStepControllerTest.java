package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BDaysOfWeekData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
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
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.worldpay.controllers.pages.checkout.steps.WorldpayB2BSummaryCheckoutStepController.CMS_PAGE_MODEL;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayB2BSummaryCheckoutStepController.SAVED_CARD_SELECTED_ATTRIBUTE;
import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.REFUSED;
import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController.REDIRECT_PREFIX;
import static de.hybris.platform.commercefacades.product.ProductOption.BASIC;
import static de.hybris.platform.commercefacades.product.ProductOption.PRICE;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("java:S5961")
public class WorldpayB2BSummaryCheckoutStepControllerTest {

    private static final String SECURITY_CODE = "securityCode";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String PAGE_TITLE = "pageTitle";
    private static final String RESOLVED_CONTENT_PAGE_TITLE = "resolvedContentPageTitle";
    private static final String SOME_FLOW_GROUP = "someFlowGroup";
    private static final String SUMMARY = "summary";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String PRODUCT_CODE = "productCode";
    private static final String NOINDEX_NOFOLLOW = "noindex,nofollow";
    private static final String B2B_CSE_PAYMENT_FORM = "b2bCSEPaymentForm";
    private static final String REDIRECT_VIEW = "redirectView";
    private static final String CHECKOUT_SUMMARY_PAGE = "CheckoutSummaryPage";
    private static final String THREEDSECURE_FLEX_DDC_URL_VALUE = "threeDSecureDDCUrlValue";
    private static final String THREEDSECURE_JWT_FLEX_DDC_VALUE = "THREEDSECURE_JWT_FLEX_DDC_VALUE";
    private static final String THREDSFLEX_DDC_PAGE = "ddcIframePage";
    private static final String CHALLENGE_IFRAME_3D_SECURE = "challengeIframe3dSecure";
    private static final String BIN = "bin";
    private static final String BIN_VALUE = "78954";
    private static final String PREVIOUS = "redirect:previous";
    private static final String NEXT = "redirect:next";
    private static final String CURRENT = "redirect:current";
    private static final String CART_DATA = "cartData";
    private static final String ALL_ITEMS = "allItems";
    private static final String DELIVERY_ADDRESS = "deliveryAddress";
    private static final String DELIVERY_MODE = "deliveryMode";
    private static final String PAYMENT_INFO = "paymentInfo";
    private static final String REQUEST_SECURITY_CODE = "requestSecurityCode";
    private static final String CART_SUFFIX = "/cart";
    private static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";
    private static final String CART_CODE = "0001";
    private static final String REDIRECT_URL_ORDER_CONFIRMATION = REDIRECT_PREFIX + "/checkout/orderConfirmation/";
    private static final String THREEDSECURE_JWT_FLEX_DDC = "jwt3DSecureFlexDDC";
    private static final String THREEDSECURE_FLEX_DDC_URL = "threeDSecureDDCUrl";
    private static final String DAY_1 = "DAY1";
    private static final String DAY_2 = "DAY2";
    private static final String N_DAYS = "nDays";
    private static final String NTH_DAY_OF_MONTH = "nthDayOfMonth";
    private static final String NTH_WEEK = "nthWeek";
    private static final String DAYS_OF_WEEK = "daysOfWeek";
    private static final String FORM_N_DAYS = "14";
    private static final String META_ROBOTS = "metaRobots";
    private static final String PREVIOUS_STEP_URL = "previousStepUrl";
    private static final String NEXT_STEP_URL = "nextStepUrl";
    private static final String CURRENT_STEP_URL = "currentStepUrl";
    private static final String PROGRESS_BAR_ID = "progressBarId";
    private static final String BIRTHDAY_DATE = "birthdayDate";
    private static final String DEVICE_SESSION = "DEVICE_SESSION";
    private static final String DEVICE_SESSION_VALUE = "deviceSession";

    @Spy
    @InjectMocks
    private WorldpayB2BSummaryCheckoutStepController testObj;

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
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private B2BCSEPaymentForm formMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CartModificationData cartModificationDataMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CartData cartDataMock;
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
    @Mock
    private AbstractOrderData abstractOrderDataMock;
    @Mock
    private B2BDaysOfWeekData b2BDaysOfWeekDataMock;

    private Model modelStub = new ExtendedModelMap();

    private List<String> dayList = List.of(DAY_1, DAY_2);

    private static final Date BIRTHDAY_DATE_VALUE = new GregorianCalendar(1990, Calendar.MAY, 17).getTime();


    @Before
    public void setUp() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        doNothing().when(testObj).setupAddPaymentPage(modelStub);
        doReturn(checkoutFacadeMock).when(testObj).getB2BCheckoutFacade();
        doReturn(checkoutFacadeMock).when(testObj).getCheckoutFacade();
        doReturn(checkoutStepMock).when(testObj).getCheckoutStep();
        doReturn(dayList).when(testObj).getNumberRange(anyInt(), anyInt());
        when(cmsPageServiceMock.getPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL, pagePreviewCriteriaMock)).thenReturn(contentPageModelMock);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(Boolean.FALSE);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(checkoutFacadeMock.getDaysOfWeekForReplenishmentCheckoutSummary()).thenReturn(List.of(b2BDaysOfWeekDataMock));
        when(formMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaMock);
        when(pageTitleResolverMock.resolveContentPageTitle(PAGE_TITLE)).thenReturn(RESOLVED_CONTENT_PAGE_TITLE);
        when(contentPageModelMock.getTitle()).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(emptyList());
        when(formMock.isTermsCheck()).thenReturn(true);
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(true);
        when(cartDataMock.isCalculated()).thenReturn(true);
        lenient().when(checkoutFlowGroupMapMock.get(SOME_FLOW_GROUP)).thenReturn(checkoutGroupMock);
        lenient().when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        lenient().when(checkoutStepMapMock.get(SUMMARY)).thenReturn(checkoutStepMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        lenient().when(paymentInfoMock.getBin()).thenReturn(BIN_VALUE);
        when(checkoutStepMock.previousStep()).thenReturn(PREVIOUS);
        when(checkoutStepMock.nextStep()).thenReturn(NEXT);
        when(checkoutStepMock.currentStep()).thenReturn(CURRENT);
        when(orderEntryDataMock.getProduct()).thenReturn(productDataMock);
        when(cartDataMock.getEntries()).thenReturn(singletonList(orderEntryDataMock));
        when(productDataMock.getCode()).thenReturn(PRODUCT_CODE);
        lenient().when(productFacadeMock.getProductForCodeAndOptions(PRODUCT_CODE, Arrays.asList(BASIC, PRICE))).thenReturn(productDataMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        lenient().when(sessionServiceMock.getAttribute(WebConstants.CART_RESTORATION)).thenReturn(null);
        when(sessionServiceMock.getAttribute(SAVED_CARD_SELECTED_ATTRIBUTE)).thenReturn(Boolean.TRUE);
        lenient().when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
        when(worldpayDirectOrderFacadeMock.authoriseRecurringPayment(worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHORISED);
        when(testObj.getCheckoutFacade()).thenReturn(checkoutFacadeMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void enterStep__IfPaymentInfoIsNotNullAndFSIsNotEnabled_ShouldReturnCheckoutSummaryPage() throws CMSItemNotFoundException {
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartDataMock.getDeliveryMode()).thenReturn(deliveryModeDataMock);
        when(paymentInfoMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        final String result = testObj.enterStep(modelStub, redirectAttributesMock, responseMock);

        assertThat(modelStub.asMap()).containsEntry(CART_DATA, cartDataMock);
        final List<OrderEntryData> orderEntryDatas = (List<OrderEntryData>) modelStub.asMap().get(ALL_ITEMS);
        assertThat(orderEntryDatas).hasSize(1);
        assertThat(orderEntryDatas.get(0)).isEqualTo(orderEntryDataMock);
        assertThat(modelStub.asMap()).containsEntry(DELIVERY_ADDRESS, deliveryAddressMock);
        assertThat(modelStub.asMap()).containsEntry(DELIVERY_MODE, deliveryModeDataMock);
        assertThat(modelStub.asMap()).containsEntry(PAYMENT_INFO, paymentInfoMock);
        assertThat(modelStub.asMap()).containsEntry(N_DAYS, dayList);
        assertThat(modelStub.asMap()).containsEntry(NTH_DAY_OF_MONTH, dayList);
        assertThat(modelStub.asMap()).containsEntry(NTH_WEEK, dayList);
        assertThat(modelStub.asMap()).containsEntry(DAYS_OF_WEEK, List.of(b2BDaysOfWeekDataMock));
        assertThat(modelStub.asMap()).containsEntry(REQUEST_SECURITY_CODE, Boolean.TRUE);

        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).getSecurityCode()).isNull();
        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).isTermsCheck()).isFalse();
        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).getnDays()).isEqualTo(FORM_N_DAYS);
        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).getReplenishmentRecurrence())
            .isEqualTo(B2BReplenishmentRecurrenceEnum.MONTHLY);
        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).getnDaysOfWeek())
            .isEqualTo(List.of(DayOfWeek.MONDAY));

        assertThat(modelStub.asMap()).containsEntry(CMS_PAGE_MODEL, contentPageModelMock);
        assertThat(modelStub.asMap()).containsEntry(BREADCRUMBS_KEY, EMPTY_LIST);
        assertThat(modelStub.asMap()).containsEntry(META_ROBOTS, NOINDEX_NOFOLLOW);

        assertThat(modelStub.asMap()).containsEntry(PREVIOUS_STEP_URL, StringUtils.remove(checkoutStepMock.previousStep(), REDIRECT_PREFIX));
        assertThat(modelStub.asMap()).containsEntry(NEXT_STEP_URL, StringUtils.remove(checkoutStepMock.nextStep(), REDIRECT_PREFIX));
        assertThat(modelStub.asMap()).containsEntry(CURRENT_STEP_URL, StringUtils.remove(checkoutStepMock.currentStep(), REDIRECT_PREFIX));
        assertThat(modelStub.asMap()).containsEntry(PROGRESS_BAR_ID, checkoutStepMock.getProgressBarId());

        assertThat(modelStub.asMap()).containsEntry(SUBSCRIPTION_ID, SUBSCRIPTION_ID);
        assertThat(modelStub.asMap()).containsEntry(BIN, paymentInfoMock);
        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void enterStep__IfPaymentInfoIsNull_ShouldNotThrowException() throws CMSItemNotFoundException {
        when(cartDataMock.getPaymentInfo()).thenReturn(null);

        final String result = testObj.enterStep(modelStub, redirectAttributesMock, responseMock);

        assertThat(modelStub.asMap()).containsEntry(PAYMENT_INFO, null);
        assertThat(modelStub.asMap()).containsEntry(SUBSCRIPTION_ID, null);
        assertThat(modelStub.asMap()).doesNotContainKey(BIN);
        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void enterStep__IfFSIsEnabled_ShouldAddAddFormAttributes() throws CMSItemNotFoundException {
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(Boolean.TRUE);

        modelStub.addAttribute(BIRTHDAY_DATE, BIRTHDAY_DATE_VALUE);
        modelStub.addAttribute(DEVICE_SESSION, DEVICE_SESSION_VALUE);

        final String result = testObj.enterStep(modelStub, redirectAttributesMock, responseMock);

        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).getDateOfBirth()).isEqualTo(BIRTHDAY_DATE_VALUE);
        assertThat(((B2BCSEPaymentForm) modelStub.asMap().get(B2B_CSE_PAYMENT_FORM)).getDeviceSession()).isEqualTo(DEVICE_SESSION_VALUE);
        assertThat(result).isEqualTo(CHECKOUT_SUMMARY_PAGE);
    }

    @Test
    public void placeOrder_WhenCartIsInvalid_ShouldRedirectToCartPage() throws CMSItemNotFoundException, CommerceCartModificationException, InvalidCartException {
        when(cartFacadeMock.validateCartData()).thenReturn(singletonList(cartModificationDataMock));

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertThat(result).isEqualTo(REDIRECT_PREFIX + CART_SUFFIX);
    }

    @Test
    public void placeOrder_WhenSecurityCodeIsBlankAndSubscriptionIdIsNull_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        when(cartDataMock.getPaymentInfo().getSubscriptionId()).thenReturn(null);
        when(formMock.getSecurityCode()).thenReturn(StringUtils.EMPTY);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenNoDeliveryAddress_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        when(checkoutFlowFacadeMock.hasNoDeliveryAddress()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenNoDeliveryMode_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        when(checkoutFlowFacadeMock.hasNoDeliveryMode()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenTermsAreNotChecked_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        // By default, the mock returns false, but we explicitly mock the answer here for the sake of clarity
        when(formMock.isTermsCheck()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenCartDoesNotContainTaxValues_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        when(checkoutFacadeMock.containsTaxValues()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenCartIsNotCalculated_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        when(cartDataMock.isCalculated()).thenReturn(false);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_WhenNoPaymentInfo_ShouldStayOnSummaryPage() throws CMSItemNotFoundException, InvalidCartException {
        when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertEquals(CHECKOUT_SUMMARY_PAGE, result);
    }

    @Test
    public void placeOrder_OnWorldpayException_ShouldRedirectsToSummaryPage() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayDirectOrderFacadeMock).authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(result).isEqualTo(CHECKOUT_SUMMARY_PAGE);
    }

    @Test
    public void placeOrder_OnInvalidCartException_ShouldThrowException() throws WorldpayException, InvalidCartException {
        doThrow(new InvalidCartException(EXCEPTION_MESSAGE)).when(worldpayDirectOrderFacadeMock).authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        assertThatThrownBy(() -> testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock)).isInstanceOf(InvalidCartException.class);
    }

    @Test
    public void placeOrder_WhenTransactionStatusIsNotAuthorised_ShouldHandleRedirectResponse() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(worldpayDirectOrderFacadeMock.authoriseRecurringPayment(worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(REFUSED);
        doReturn(REDIRECT_VIEW).when(testObj).handleDirectResponse(modelStub, directResponseDataMock, responseMock);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        verify(worldpayAdditionalInfoDataMock).setSecurityCode(SECURITY_CODE);
        assertThat(result).isEqualTo(REDIRECT_VIEW);
    }

    @Test
    public void placeOrder_OnEntityValidationException_ShouldRedirectsToSummaryPage() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        doThrow(new EntityValidationException(EXCEPTION_MESSAGE)).when(checkoutFacadeMock).placeOrder(any(PlaceOrderData.class));

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        verify(formMock).setTermsCheck(Boolean.FALSE);
        assertThat(result).isEqualTo(CHECKOUT_SUMMARY_PAGE);
    }

    @Test
    public void placeOrder_OnException_ShouldRedirectsToSummaryPage() throws InvalidCartException, CMSItemNotFoundException, WorldpayException {
        doThrow(new NullPointerException(EXCEPTION_MESSAGE)).when(checkoutFacadeMock).placeOrder(any(PlaceOrderData.class));

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertThat(result).isEqualTo(CHECKOUT_SUMMARY_PAGE);
    }

    @Test
    public void placeOrder_WhenTransactionStatusIsAuthorised_ShouldRedirectToOrderConfirmationPage() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(cartDataMock.getWorldpayAPMPaymentInfo().getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(checkoutFacadeMock.placeOrder(any(PlaceOrderData.class))).thenReturn(abstractOrderDataMock);
        when(abstractOrderDataMock.getCode()).thenReturn(CART_CODE);

        final String result = testObj.placeOrder(formMock, modelStub, httpServletRequestMock, redirectAttributesMock, responseMock);

        assertThat(result).isEqualTo(REDIRECT_URL_ORDER_CONFIRMATION + CART_CODE);
    }

    @Test
    public void getDDCIframeContent_shouldPopulate3DSecureJsonWebToken() {
        lenient().when(worldpayAddonEndpointServiceMock.getCheckoutSummaryPage()).thenReturn(CHECKOUT_SUMMARY_PAGE);
        when(worldpayMerchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getDdcUrl()).thenReturn(THREEDSECURE_FLEX_DDC_URL_VALUE);
        when(worldpayDDCFacadeMock.createJsonWebTokenForDDC()).thenReturn(THREEDSECURE_JWT_FLEX_DDC_VALUE);
        when(worldpayAddonEndpointServiceMock.getDdcIframe3dSecureFlex()).thenReturn(THREDSFLEX_DDC_PAGE);

        final String result = testObj.getDDCIframeContent(modelStub);

        assertThat(modelStub.asMap()).containsEntry(THREEDSECURE_FLEX_DDC_URL, THREEDSECURE_FLEX_DDC_URL_VALUE);
        assertThat(modelStub.asMap()).containsEntry(THREEDSECURE_JWT_FLEX_DDC, THREEDSECURE_JWT_FLEX_DDC_VALUE);
        assertThat(result).isEqualTo(THREDSFLEX_DDC_PAGE);
    }

    @Test
    public void getChallengeIframeContent_ShouldReturnTheWorldpayChallengeIframe() {
        when(worldpayAddonEndpointServiceMock.getChallengeIframe3dSecureFlex()).thenReturn(CHALLENGE_IFRAME_3D_SECURE);

        final String result = testObj.getChallengeIframeContent(modelStub);

        assertThat(result).isEqualTo(CHALLENGE_IFRAME_3D_SECURE);
    }

    @Test
    public void back_ShouldReturnThePreviousPage() {
        final String result = testObj.back(redirectAttributesMock);

        assertThat(result).isEqualTo(PREVIOUS);
    }

    @Test
    public void next_ShouldReturnTheNextPage() {
        final String result = testObj.next(redirectAttributesMock);

        assertThat(result).isEqualTo(NEXT);
    }
}
