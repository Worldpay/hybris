package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.controllers.pages.checkout.steps.WorldpayHopResponseController.*;
import static com.worldpay.service.model.AuthorisedStatus.*;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayHopResponseControllerTest {

    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String STATUS_ERROR = "ERROR";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ORDER_CODE = "orderCode";
    private static final String ORDER_CONF_PREFIX = "redirect:" + "/checkout/orderConfirmation/";
    private static final String COUNTRY_ISO_CODE = "GB";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LINE_1 = "line1";
    private static final String LINE_2 = "line2";
    private static final String TOWN = "town";
    private static final String POSTAL_CODE = "postalCode";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String ORDER_GUID = "orderGuid";
    private static final String ORDER_CONFIRMATION_PAGE = "redirect:/checkout/orderConfirmation/" + ORDER_GUID;
    private static final String CHOOSE_PAYMENT_REDIRECT_URL = REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=%s";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String KLARNA_PAGE_REDIRECT = "klarnaContentPage";
    private static final String KLARNA_CONTENT_PAGE = "decodedContent";
    private static final String KLARNA_VIEW_DATA = "KLARNA_VIEW_DATA";

    @Spy
    @InjectMocks
    private WorldpayHopResponseController testObj;

    @Mock
    private Model modelMock;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock, redirectAuthoriseResultFromInquiryMock;
    @Mock
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverterMock;
    @Mock
    private Set<String> apmErrorResponseStatusesMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private AddressData deliveryAddressMock;
    @Mock
    private I18NFacade i18NFacade;
    @Mock
    private RegionData regionDataMock;
    @Captor
    private ArgumentCaptor<PaymentDetailsForm> paymentDetailsFormArgumentCaptor;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    private MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock(name = "orderConverter")
    private Converter<AbstractOrderModel, OrderData> orderConverterMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private KlarnaRedirectAuthoriseResult klarnaRedirectAuthoriseResponse;

    @Before
    public void setUp() {
        when(apmErrorResponseStatusesMock.contains(STATUS_ERROR)).thenReturn(true);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);
        when(redirectAuthoriseResultConverterMock.convert(anyMapOf(String.class, String.class))).thenReturn(redirectAuthoriseResultMock);
        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(orderConverterMock.convert(orderModelMock)).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayHostedOrderFacadeMock.validateRedirectResponse(anyMapOf(String.class, String.class))).thenReturn(true);
        when(worldpayAddonEndpointServiceMock.getHostedOrderPostPage()).thenReturn("hostedOrderPostPage");
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, AuthorisedStatus.ERROR.getCode());
    }

    @Test
    public void doHandleHopResponseREFUSEDShouldRedirectToChoosePaymentMethod() throws Exception {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(REFUSED.name());

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, REFUSED.name()), result);
    }

    @Test
    public void doHandleHopResponseAUTHORISEDShouldCompleteRedirectAndPlaceOrderWhenResponseIsValid() throws Exception {
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AUTHORISED.name());

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder();
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void doHandleHopResponseShouldNOTCompleteRedirectAndNOTPlaceOrderWhenResponseIsNotValid() throws Exception {
        when(worldpayHostedOrderFacadeMock.validateRedirectResponse(anyMapOf(String.class, String.class))).thenReturn(false);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
    }

    @Test
    public void doHandleHopResponseNotAUTHORISEDShouldNotCompleteRedirectAndNotPlaceOrderWhenResponseIsValid() throws Exception {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(REFUSED.name());

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, REFUSED.getCode()), result);
    }

    @Test
    public void doHandlePendingHopResponseOPENShouldCompleteRedirectForAPMAndPlaceOrderWhenResponseIsValid() throws Exception {
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(OPEN.getCode());

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder();
        verify(redirectAuthoriseResultMock).setPending(true);
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void doHandlePendingHopResponseNotOPENShouldNotCompleteRedirectForAPMAndNotPlaceOrderWhenResponseIsValid() throws Exception {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(ERROR.getCode());

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
    }

    @Test
    public void doHandlePendingHopResponseNotERRORShouldNOTCompleteRedirectForAPMAndNotPlaceOrderWhenResponseIsNotValid() throws Exception {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(ERROR.getCode());

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
    }

    @Test
    public void doHandlePendingHopResponseRaisesExceptionWhenPlacingOrderAndPaymentStatusSet() throws Exception {
        when(checkoutFacadeMock.placeOrder()).thenThrow(new InvalidCartException(EXCEPTION_MESSAGE));

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
    }

    @Test
    public void doHandleBankTransferHopResponseShouldCompleteRedirectAndPlaceOrder() throws Exception {
        when(redirectAuthoriseResultConverterMock.convert(anyMapOf(String.class, String.class))).thenReturn(redirectAuthoriseResultMock);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(false);

        final String result = testObj.doHandleBankTransferHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        assertEquals(ORDER_CONF_PREFIX + ORDER_CODE, result);
    }

    @Test
    public void doHandleBankTransferHopFailureShouldRedirectToPaymentPageWithErrorMessage() throws Exception {
        when(redirectAuthoriseResultConverterMock.convert(anyMapOf(String.class, String.class))).thenReturn(redirectAuthoriseResultMock);

        final String result = testObj.doHandleBankTransferHopFailure(mockHttpServletRequest, redirectAttributesMock);

        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, AuthorisedStatus.REFUSED.name());
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    public void shouldPopulateBillingAddressWithShippingAddress() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(deliveryAddressMock.getFirstName()).thenReturn(FIRST_NAME);
        when(deliveryAddressMock.getLastName()).thenReturn(LAST_NAME);
        when(deliveryAddressMock.getLine1()).thenReturn(LINE_1);
        when(deliveryAddressMock.getLine2()).thenReturn(LINE_2);
        when(deliveryAddressMock.getTown()).thenReturn(TOWN);
        when(deliveryAddressMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(deliveryAddressMock.getPhone()).thenReturn(PHONE_NUMBER);

        final List<RegionData> regionDataInfos = singletonList(regionDataMock);
        when(i18NFacade.getRegionsForCountryIso(COUNTRY_ISO_CODE)).thenReturn(regionDataInfos);

        testObj.getCountryAddressForm(COUNTRY_ISO_CODE, true, modelMock);

        verify(modelMock).addAttribute(eq(BILLING_ADDRESS_FORM), paymentDetailsFormArgumentCaptor.capture());
        verify(modelMock).addAttribute("supportedCountries", checkoutFacadeMock.getDeliveryCountries());
        verify(modelMock).addAttribute("regions", regionDataInfos);
        verify(modelMock).addAttribute("country", COUNTRY_ISO_CODE);

        final PaymentDetailsForm paymentDetailsForm = paymentDetailsFormArgumentCaptor.getValue();
        assertEquals(FIRST_NAME, paymentDetailsForm.getBillingAddress().getFirstName());
        assertEquals(LAST_NAME, paymentDetailsForm.getBillingAddress().getLastName());
        assertEquals(LINE_1, paymentDetailsForm.getBillingAddress().getLine1());
        assertEquals(LINE_2, paymentDetailsForm.getBillingAddress().getLine2());
        assertEquals(TOWN, paymentDetailsForm.getBillingAddress().getTownCity());
        assertEquals(POSTAL_CODE, paymentDetailsForm.getBillingAddress().getPostcode());
        assertEquals(COUNTRY_ISO_CODE, paymentDetailsForm.getBillingAddress().getCountryIso());
        assertEquals(PHONE_NUMBER, paymentDetailsForm.getBillingAddress().getPhone());
    }

    @Test
    public void doHostedOrderPageErrorShouldRedirectToChoosePaymentMethod() throws Exception {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(EXPIRED.getCode());

        final String result = testObj.doHostedOrderPageError(EXPIRED.getCode(), redirectAttributesMock);

        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, AuthorisedStatus.EXPIRED.name());
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + AuthorisedStatus.EXPIRED.name(), result);
    }

    @Test
    public void shouldReturnChoosePaymentMethodURL() {
        final String result = testObj.doCancelPayment();

        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    public void shouldReturnCartURLWhenInvalidCart() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);

        final String result = testObj.doCancelPayment();

        assertEquals(AbstractController.REDIRECT_PREFIX + "/cart", result);
    }

    @Test
    public void shouldReturnAddDeliveryAddressURLWhenHasNoDeliveryAddress() {
        when(checkoutFacadeMock.hasNoDeliveryAddress()).thenReturn(true);

        final String result = testObj.doCancelPayment();

        assertEquals(REDIRECT_URL_ADD_DELIVERY_ADDRESS, result);
    }

    @Test
    public void shouldReturnChooseDeliveryMethodURLWhenHasNoDeliveryMode() {
        when(checkoutFacadeMock.hasNoDeliveryMode()).thenReturn(true);

        final String result = testObj.doCancelPayment();

        assertEquals(REDIRECT_URL_CHOOSE_DELIVERY_METHOD, result);
    }

    @Test
    public void handleHopResponseShouldRedirectUserToOrderConfirmationPageWhenOrderHasAlreadyBeenPlaced() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void handleHopResponseShouldRedirectUserToEmptyCartPageWhenCartIsInvalidAndTransactionIsNotFound() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        assertEquals(AbstractController.REDIRECT_PREFIX + "/cart", result);
    }

    @Test
    public void shouldHandleKlarnaConfirmation() throws WorldpayException, InvalidCartException {
        when(worldpayPaymentCheckoutFacadeMock.checkKlarnaOrderStatus()).thenReturn(klarnaRedirectAuthoriseResponse);
        when(klarnaRedirectAuthoriseResponse.getDecodedHTMLContent()).thenReturn(KLARNA_CONTENT_PAGE);
        when(worldpayAddonEndpointServiceMock.getKlarnaResponsePage()).thenReturn(KLARNA_PAGE_REDIRECT);

        final String result = testObj.doHandleKlarnaConfirmation(modelMock, redirectAttributesMock);

        assertEquals(KLARNA_PAGE_REDIRECT, result);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(klarnaRedirectAuthoriseResponse);
        verify(modelMock).addAttribute(KLARNA_VIEW_DATA, KLARNA_CONTENT_PAGE);
        verify(checkoutFacadeMock).placeOrder();
    }

    @Test
    public void shouldNotPlaceOrderWhenKlarnaConfirmationIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayPaymentCheckoutFacadeMock.checkKlarnaOrderStatus()).thenThrow(new WorldpayException("Chan chan"));

        final String result = testObj.doHandleKlarnaConfirmation(modelMock, redirectAttributesMock);

        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
        verify(modelMock, never()).addAttribute(eq(KLARNA_VIEW_DATA), anyString());
        verify(checkoutFacadeMock, never()).placeOrder();
    }

    @Test
    public void shouldRedirectToErrorPageWhenPlaceOrderFails() throws WorldpayException, InvalidCartException {
        when(worldpayPaymentCheckoutFacadeMock.checkKlarnaOrderStatus()).thenReturn(klarnaRedirectAuthoriseResponse);
        when(checkoutFacadeMock.placeOrder()).thenThrow(new InvalidCartException("Chan chan"));

        final String result = testObj.doHandleKlarnaConfirmation(modelMock, redirectAttributesMock);

        verify(modelMock, never()).addAttribute(KLARNA_VIEW_DATA, KLARNA_PAGE_REDIRECT);
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
    }

    @Test
    public void shouldInquiryWorldpayWhenPaymentStatusIsNotPresentInTheResultURLParametersAndPlaceOrderIfInquiredPaymentStatusIsAuthorised() throws Exception {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(null);
        when(worldpayHostedOrderFacadeMock.inquiryPaymentStatus()).thenReturn(redirectAuthoriseResultFromInquiryMock);
        when(redirectAuthoriseResultFromInquiryMock.getPaymentStatus()).thenReturn(AUTHORISED.getCode());

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultFromInquiryMock);
        verify(checkoutFacadeMock).placeOrder();
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void shouldInquiryWorldpayWhenPaymentStatusIsNotPresentInTheResultURLParametersButNotPlaceOrderWhenPaymentStatusIsNotAuthorised() throws Exception {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(null);
        when(worldpayHostedOrderFacadeMock.inquiryPaymentStatus()).thenReturn(redirectAuthoriseResultFromInquiryMock);
        when(redirectAuthoriseResultFromInquiryMock.getPaymentStatus()).thenReturn(ERROR.getCode());

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultFromInquiryMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);
    }

    @Test
    public void handleHopResponseShouldRedirectUserToOrderConfirmationPageWhenOrderHasAlreadyBeenPlacedAndPaymentStatusIsNotInTheResultURLParameters() {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);
        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void shouldRedirectToErrorPageWhenInquiryCommunicationFails() throws Exception {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(null);
        when(worldpayHostedOrderFacadeMock.inquiryPaymentStatus()).thenThrow(new WorldpayException("There was an error"));

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.getCode()), result);

    }
}
