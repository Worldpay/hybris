package com.worldpay.controllers.pages.checkout.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.ERROR;
import static com.worldpay.enums.order.AuthorisedStatus.EXPIRED;
import static com.worldpay.enums.order.AuthorisedStatus.OPEN;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHOPNoReturnParamsStrategy;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.hop.WorldpayOrderCodeVerificationService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayHopResponseControllerTest {

    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ORDER_CODE = "orderCode";
    private static final String ORDER_CONF_PREFIX = "redirect:" + "/checkout/orderConfirmation/";
    private static final String COUNTRY_ISO_CODE = "GB";
    private static final String TITLE = "title";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LINE_1 = "line1";
    private static final String LINE_2 = "line2";
    private static final String TOWN = "town";
    private static final String POSTAL_CODE = "postalCode";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String ORDER_GUID = "orderGuid";
    private static final String ORDER_CONFIRMATION_PAGE = "redirect:/checkout/orderConfirmation/" + ORDER_GUID;
    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/delivery-address/add";
    private static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/delivery-method/choose";
    private static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/worldpay/choose-payment-method";
    private static final String CHOOSE_PAYMENT_REDIRECT_URL = REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=%s";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String BILLING_ADDRESS_FORM = "wpBillingAddressForm";

    private final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

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
    private Set<AuthorisedStatus> apmErrorResponseStatusesMock;
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
    @Mock
    private WorldpayHOPNoReturnParamsStrategy worldpayHOPNoReturnParamsStrategyMock;
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
    private WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacadeMock;
    @Mock
    private WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationServiceMock;

    @Test
    void doHandleHopResponse_ShouldPlaceOrderAndRedirectToConfirmation_WhenRedirectIsValidAndPaymentStatusIsAuthorised() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, AUTHORISED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AUTHORISED);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder();
        verify(redirectAttributesMock, never()).addFlashAttribute(eq(PAYMENT_STATUS_PARAMETER_NAME), any());
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToChoosePaymentMethod_WhenPaymentStatusIsRefused() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(REFUSED);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, REFUSED.name()), result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenRedirectValidationFails() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).hasValidCart();
        verify(redirectAuthoriseResultMock, never()).getPaymentStatus();
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToOrderConfirmationPage_WhenCartIsInvalidButOrderAlreadyExists() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, AUTHORISED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderConverterMock.convert(orderModelMock)).thenReturn(orderDataMock);

        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        verify(orderConverterMock).convert(orderModelMock);
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToCart_WhenCartIsInvalidAndOrderCannotBeFound() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, AUTHORISED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(null);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        verify(orderConverterMock, never()).convert(any());
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(REDIRECT_PREFIX + "/cart", result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToCart_WhenCartIsInvalidAndPaymentTransactionDoesNotReferenceAnOrder() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, AUTHORISED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        verify(orderConverterMock, never()).convert(any());
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(REDIRECT_PREFIX + "/cart", result);
    }

    @Test
    void doHandleHopResponse_ShouldPlaceOrderAndRedirectToConfirmation_WhenPaymentStatusIsMissingAndInquiryReturnsAuthorised() throws Exception {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);

        when(worldpayHOPNoReturnParamsStrategyMock.authoriseCart()).thenReturn(redirectAuthoriseResultFromInquiryMock);
        when(redirectAuthoriseResultFromInquiryMock.getPaymentStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHOPNoReturnParamsStrategyMock).authoriseCart();
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultFromInquiryMock);
        verify(checkoutFacadeMock).placeOrder();
        verify(redirectAttributesMock, never()).addFlashAttribute(eq(PAYMENT_STATUS_PARAMETER_NAME), any());
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenPaymentStatusIsMissingAndInquiryReturnsError() throws Exception {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);

        when(worldpayHOPNoReturnParamsStrategyMock.authoriseCart()).thenReturn(redirectAuthoriseResultFromInquiryMock);
        when(redirectAuthoriseResultFromInquiryMock.getPaymentStatus()).thenReturn(AuthorisedStatus.ERROR);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHOPNoReturnParamsStrategyMock).authoriseCart();
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToOrderConfirmationPage_WhenPaymentStatusIsMissingAndOrderAlreadyExists() throws InvalidCartException {
        mockHttpServletRequest.removeParameter(PAYMENT_STATUS_PARAMETER_NAME);

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);

        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderConverterMock.convert(orderModelMock)).thenReturn(orderDataMock);

        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayPaymentTransactionServiceMock).getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);
        verify(orderConverterMock).convert(orderModelMock);
        verify(worldpayHOPNoReturnParamsStrategyMock, never()).authoriseCart();
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldPlaceOrderAndRedirectToConfirmation_WhenApmPaymentStatusIsOpen() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);

        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(OPEN);
        when(apmErrorResponseStatusesMock.contains(OPEN)).thenReturn(false);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(redirectAuthoriseResultMock).setPending(true);
        verify(apmErrorResponseStatusesMock).contains(OPEN);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder();
        verify(redirectAttributesMock, never()).addFlashAttribute(eq(PAYMENT_STATUS_PARAMETER_NAME), any());
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenApmPaymentStatusIsError() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);

        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(ERROR);
        when(apmErrorResponseStatusesMock.contains(ERROR)).thenReturn(true);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(redirectAuthoriseResultMock).setPending(true);
        verify(apmErrorResponseStatusesMock).contains(ERROR);
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenRedirectValidationFails() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(redirectAuthoriseResultConverterMock, never()).convert(anyMap());
        verify(redirectAuthoriseResultMock, never()).setPending(true);
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenPlaceOrderThrowsInvalidCartException() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);

        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(OPEN);
        when(apmErrorResponseStatusesMock.contains(OPEN)).thenReturn(false);

        when(checkoutFacadeMock.placeOrder()).thenThrow(new InvalidCartException(EXCEPTION_MESSAGE));
        when(modelMock.asMap()).thenReturn(new HashMap<>());

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(redirectAuthoriseResultMock).setPending(true);
        verify(apmErrorResponseStatusesMock).contains(OPEN);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder();
        verify(modelMock).addAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), anyList());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandleBankTransferHopResponse_ShouldPlaceOrderAndRedirectToConfirmation_WhenEncryptedOrderIdIsValid() throws InvalidCartException {
        when(worldpayOrderCodeVerificationServiceMock.isValidEncryptedOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(true);
        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);

        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(false);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);

        final String result = testObj.doHandleBankTransferHopResponse(WORLDPAY_ORDER_CODE, mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayOrderCodeVerificationServiceMock).isValidEncryptedOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder();
        assertEquals(ORDER_CONF_PREFIX + ORDER_CODE, result);
    }

    @Test
    void doHandleBankTransferHopResponse_ShouldRedirectToChoosePaymentMethodWithRefusedStatus_WhenEncryptedOrderIdIsInvalid() throws InvalidCartException {
        when(worldpayOrderCodeVerificationServiceMock.isValidEncryptedOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(false);

        final String result = testObj.doHandleBankTransferHopResponse(WORLDPAY_ORDER_CODE, mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayOrderCodeVerificationServiceMock).isValidEncryptedOrderCode(WORLDPAY_ORDER_CODE);
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        verify(redirectAuthoriseResultConverterMock, never()).convert(anyMap());
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    void doHandleBankTransferHopFailure_ShouldRedirectToChoosePaymentMethodWithRefusedStatus() throws InvalidCartException {
        final String result = testObj.doHandleBankTransferHopFailure(mockHttpServletRequest, redirectAttributesMock);

        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(any());
        verify(checkoutFacadeMock, never()).placeOrder();
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    void doCancelPayment_ShouldRedirectToChoosePaymentMethod_WhenCheckoutStateIsComplete() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(checkoutFacadeMock.hasNoDeliveryAddress()).thenReturn(false);
        when(checkoutFacadeMock.hasNoDeliveryMode()).thenReturn(false);

        final String result = testObj.doCancelPayment();

        verify(checkoutFacadeMock).hasValidCart();
        verify(checkoutFacadeMock).hasNoDeliveryAddress();
        verify(checkoutFacadeMock).hasNoDeliveryMode();
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }

    @Test
    void doCancelPayment_ShouldRedirectToCart_WhenCartIsInvalid() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(false);

        final String result = testObj.doCancelPayment();

        verify(checkoutFacadeMock).hasValidCart();
        verify(checkoutFacadeMock, never()).hasNoDeliveryAddress();
        verify(checkoutFacadeMock, never()).hasNoDeliveryMode();
        assertEquals(REDIRECT_PREFIX + "/cart", result);
    }

    @Test
    void doCancelPayment_ShouldRedirectToAddDeliveryAddress_WhenDeliveryAddressIsMissing() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(checkoutFacadeMock.hasNoDeliveryAddress()).thenReturn(true);

        final String result = testObj.doCancelPayment();

        verify(checkoutFacadeMock).hasValidCart();
        verify(checkoutFacadeMock).hasNoDeliveryAddress();
        verify(checkoutFacadeMock, never()).hasNoDeliveryMode();
        assertEquals(REDIRECT_URL_ADD_DELIVERY_ADDRESS, result);
    }

    @Test
    void doCancelPayment_ShouldRedirectToChooseDeliveryMethod_WhenDeliveryModeIsMissing() {
        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(checkoutFacadeMock.hasNoDeliveryAddress()).thenReturn(false);
        when(checkoutFacadeMock.hasNoDeliveryMode()).thenReturn(true);

        final String result = testObj.doCancelPayment();

        verify(checkoutFacadeMock).hasValidCart();
        verify(checkoutFacadeMock).hasNoDeliveryAddress();
        verify(checkoutFacadeMock).hasNoDeliveryMode();
        assertEquals(REDIRECT_URL_CHOOSE_DELIVERY_METHOD, result);
    }

    @Test
    void doHostedOrderPageError_ShouldRedirectToChoosePaymentMethod_WhenPaymentStatusIsProvided() {
        final String result = testObj.doHostedOrderPageError(EXPIRED.name(), redirectAttributesMock);

        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, AuthorisedStatus.EXPIRED.name());
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + AuthorisedStatus.EXPIRED.name(), result);
    }

    @Test
    void doHostedOrderPageError_ShouldUseErrorFlashAttribute_WhenPaymentStatusIsNull() {
        final String result = testObj.doHostedOrderPageError(null, redirectAttributesMock);

        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + ERROR.name(), result);
    }

    @Test
    void getCountryAddressForm_ShouldPopulateBillingAddressFromDeliveryAddress_WhenUseDeliveryAddressIsTrue() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);

        when(deliveryAddressMock.getTitleCode()).thenReturn(TITLE);
        when(deliveryAddressMock.getFirstName()).thenReturn(FIRST_NAME);
        when(deliveryAddressMock.getLastName()).thenReturn(LAST_NAME);
        when(deliveryAddressMock.getLine1()).thenReturn(LINE_1);
        when(deliveryAddressMock.getLine2()).thenReturn(LINE_2);
        when(deliveryAddressMock.getTown()).thenReturn(TOWN);
        when(deliveryAddressMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(deliveryAddressMock.getPhone()).thenReturn(PHONE_NUMBER);

        final List<RegionData> regionDataInfos = singletonList(regionDataMock);
        when(i18NFacade.getRegionsForCountryIso(COUNTRY_ISO_CODE)).thenReturn(regionDataInfos);

        when(worldpayAddonEndpointServiceMock.getBillingAddressForm()).thenReturn(BILLING_ADDRESS_FORM);

        final String result = testObj.getCountryAddressForm(COUNTRY_ISO_CODE, true, modelMock);

        verify(modelMock).addAttribute(eq(BILLING_ADDRESS_FORM), paymentDetailsFormArgumentCaptor.capture());
        verify(modelMock).addAttribute("supportedCountries", checkoutFacadeMock.getCountries(CountryType.SHIPPING));
        verify(modelMock).addAttribute("regions", regionDataInfos);
        verify(modelMock).addAttribute("country", COUNTRY_ISO_CODE);
        assertEquals(BILLING_ADDRESS_FORM, result);

        final PaymentDetailsForm paymentDetailsForm = paymentDetailsFormArgumentCaptor.getValue();
        assertEquals(FIRST_NAME, paymentDetailsForm.getBillingAddress().getFirstName());
        assertEquals(LAST_NAME, paymentDetailsForm.getBillingAddress().getLastName());
        assertEquals(LINE_1, paymentDetailsForm.getBillingAddress().getLine1());
        assertEquals(LINE_2, paymentDetailsForm.getBillingAddress().getLine2());
        assertEquals(TOWN, paymentDetailsForm.getBillingAddress().getTownCity());
        assertEquals(POSTAL_CODE, paymentDetailsForm.getBillingAddress().getPostcode());
        assertEquals(COUNTRY_ISO_CODE, paymentDetailsForm.getBillingAddress().getCountryIso());
        assertEquals(PHONE_NUMBER, paymentDetailsForm.getBillingAddress().getPhone());
        assertEquals(TITLE, paymentDetailsForm.getBillingAddress().getTitleCode());
    }
}
