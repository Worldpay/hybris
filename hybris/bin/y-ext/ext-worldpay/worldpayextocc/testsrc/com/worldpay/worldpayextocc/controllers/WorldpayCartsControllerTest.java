package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.payment.PaymentDataWsDTO;
import com.worldpay.dto.payment.PaymentRequestData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.worldpayextocc.exceptions.NoCheckoutCartException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.FULL_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCartsControllerTest {

    private static final String SESSION_ID = "sessionID";
    private static final String PAYMENT_METHOD_NOT_SUPPORTED = "paymentMethodNotSupported";
    private static final String PAYMENT_DETAILS = "paymentDetails";
    private static final String CART_ID = "cartId";
    private static final String THREED_SECURE_FLOW = "3D-Secure-Flow";
    private static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    private static final String RETURN_CODE = "returnCode";
    private static final String PAYMENT_METHOD = "paymentMethod";

    @Spy
    @InjectMocks
    private WorldpayCartsController testObj;

    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacadeMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private Validator paymentDetailsDTOValidatorMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private ConfigurablePopulator<HttpServletRequest, PaymentDetailsWsDTO, PaymentDetailsWsDTOOption> httpRequestPaymentDetailsWsDTOPopulatorMock;
    @Mock
    private Populator<AddressWsDTO, AddressData> worldpayAdressWsDTOAddressDataPopulatorMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private APMAvailabilityFacade apmAvailabilityFacadeMock;
    @Mock
    private WorldpayBankConfigurationFacade worldpayBankConfigurationFacadeMock;

    @Mock
    private AddressData addressDataMock;
    @Mock
    private AddressWsDTO addressWsDTOMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoDataMock;
    @Mock
    private PaymentDetailsWsDTO paymentDetailsWsDTOMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private OrderWsDTO orderWsDTOMock;
    @Mock
    private PaymentData paymentDataMock;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    private static final Map<String, String> threeDSFlexData = Map.of("key", "value");

    @Before
    public void setUp() throws WorldpayException, InvalidCartException {
        lenient().when(dataMapperMock.map(addressWsDTOMock, AddressData.class, DEFAULT_LEVEL)).thenReturn(addressDataMock);
        when(apmAvailabilityFacadeMock.isAvailable(or(eq(PaymentType.IDEAL.getMethodCode()), eq(PaymentType.VISA.getMethodCode())))).thenReturn(Boolean.TRUE);
        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);
        lenient().when(dataMapperMock.map(ccPaymentInfoDataMock, PaymentDetailsWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL)).thenReturn(paymentDetailsWsDTOMock);
        when(worldpayDirectOrderFacadeMock.executeFirstPaymentAuthorisation3DSecure(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock))).thenReturn(directResponseDataMock);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHORISED);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL)).thenReturn(orderWsDTOMock);
        when(worldpayDirectResponseFacadeMock.retrieveAttributesForFlex3dSecure(directResponseDataMock)).thenReturn(threeDSFlexData);
        when(paymentDetailsWsDTOMock.getBillingAddress()).thenReturn(addressWsDTOMock);

        doAnswer(invocationOnMock -> {
            final PaymentDetailsWsDTO paymentDetails = invocationOnMock.getArgument(1);
            paymentDetails.setBillingAddress(addressWsDTOMock);

            return null;
        }).when(httpRequestPaymentDetailsWsDTOPopulatorMock).populate(eq(requestMock), any(PaymentDetailsWsDTO.class), any());
    }

    @Test(expected = NoCheckoutCartException.class)
    public void addPaymentDetails_WhenHasNotCheckoutCart_ShouldThrowNoCheckoutCartException() throws WorldpayException, NoCheckoutCartException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.FALSE);

        testObj.addPaymentDetails(requestMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WebserviceValidationException.class)
    public void addPaymentDetails_WhenPaymentDetailsAreNotValid_ShouldThrowWebserviceValidationException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WebserviceValidationException.class).when(testObj).validate(any(PaymentDetailsWsDTO.class), eq(PAYMENT_DETAILS), eq(paymentDetailsDTOValidatorMock));

        testObj.addPaymentDetails(requestMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void addPaymentDetails_WhenTokenizeThrowsWorldpayException_ShouldThrowWorldpayException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WorldpayException.class).when(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        testObj.addPaymentDetails(requestMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void addPaymentDetails_WhenPaymentDetailsAreValidCheckoutCartExistsAndTokenizeDoesNotThrowAnException_ShouldPopulatePaymentDetailsFromRequestAndAddThem() throws WorldpayException, NoCheckoutCartException {
        final PaymentDetailsWsDTO result = testObj.addPaymentDetails(requestMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(httpRequestPaymentDetailsWsDTOPopulatorMock).populate(eq(requestMock), any(PaymentDetailsWsDTO.class), anyCollectionOf(PaymentDetailsWsDTOOption.class));
        verify(checkoutFacadeMock).hasCheckoutCart();
        verify(paymentDetailsDTOValidatorMock).validate(any(PaymentDetailsWsDTO.class), any(BeanPropertyBindingResult.class));
        verify(worldpayAdditionalInfoFacadeMock).createWorldpayAdditionalInfoData(requestMock);
        verify(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));
        verify(checkoutFacadeMock).getCheckoutCart();
        verify(dataMapperMock).map(ccPaymentInfoDataMock, PaymentDetailsWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertThat(result).isEqualTo(paymentDetailsWsDTOMock);
    }

    @Test(expected = NoCheckoutCartException.class)
    public void addPaymentDetailsWithRequestBody_WhenHasNotCheckoutCart_ShouldThrowNoCheckoutCartException() throws WorldpayException, NoCheckoutCartException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.FALSE);

        testObj.addPaymentDetails(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WebserviceValidationException.class)
    public void addPaymentDetailsWithRequestBody_WhenPaymentDetailsAreNotValid_ShouldThrowWebserviceValidationException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WebserviceValidationException.class).when(testObj).validate(paymentDetailsWsDTOMock, PAYMENT_DETAILS, paymentDetailsDTOValidatorMock);

        testObj.addPaymentDetails(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void addPaymentDetailsWithRequestBody_WhenTokenizeThrowsWorldpayException_ShouldThrowWorldpayException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WorldpayException.class).when(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        testObj.addPaymentDetails(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void addPaymentDetailsWithRequestBody_WhenPaymentDetailsAreValidCheckoutCartExistsAndTokenizeDoesNotThrowAnException_ShouldPopulatePaymentDetailsFromRequestAndAddThem() throws WorldpayException, NoCheckoutCartException {
        final PaymentDetailsWsDTO result = testObj.addPaymentDetails(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(checkoutFacadeMock).hasCheckoutCart();
        verify(paymentDetailsDTOValidatorMock).validate(any(PaymentDetailsWsDTO.class), any(BeanPropertyBindingResult.class));
        verify(worldpayAdditionalInfoFacadeMock).createWorldpayAdditionalInfoData(requestMock);
        verify(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));
        verify(checkoutFacadeMock).getCheckoutCart();
        verify(dataMapperMock).map(ccPaymentInfoDataMock, PaymentDetailsWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertThat(result).isEqualTo(paymentDetailsWsDTOMock);
    }

    @Test(expected = NoCheckoutCartException.class)
    public void addPaymentDetailsAndPlaceOrder_WhenHasNotCheckoutCart_ShouldThrowNoCheckoutCartException() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.FALSE);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test(expected = WebserviceValidationException.class)
    public void addPaymentDetailsAndPlaceOrder__WhenPaymentDetailsAreNotValid_ShouldThrowWebserviceValidationException() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        doThrow(WebserviceValidationException.class).when(testObj).validate(paymentDetailsWsDTOMock, PAYMENT_DETAILS, paymentDetailsDTOValidatorMock);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test(expected = WorldpayException.class)
    public void addPaymentDetailsAndPlaceOrder_WhenExecuteFirstPaymentAuthorisation3DSecureThrowsWorldpayException_ShouldThrowWorldpayException() throws WorldpayException, NoCheckoutCartException, InvalidCartException {
        doThrow(WorldpayException.class).when(worldpayDirectOrderFacadeMock).executeFirstPaymentAuthorisation3DSecure(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test(expected = InvalidCartException.class)
    public void addPaymentDetailsAndPlaceOrder_WhenExecuteFirstPaymentAuthorisation3DSecureThrowsInvalidCartException_ShouldThrowInvalidCartException() throws WorldpayException, NoCheckoutCartException, InvalidCartException {
        doThrow(InvalidCartException.class).when(worldpayDirectOrderFacadeMock).executeFirstPaymentAuthorisation3DSecure(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndPaymentIsAuthorised_ShouldPopulateOrderInResponse() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isAuthorised(directResponseDataMock)).thenReturn(Boolean.TRUE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        assertThat(result.getTransactionStatus()).isEqualTo(TransactionStatus.AUTHORISED);
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndPaymentIsNotAuthorised_ShouldNotPopulateOrderInResponse() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isAuthorised(directResponseDataMock)).thenReturn(Boolean.FALSE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        assertThat(result.getOrder()).isNull();
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIs3DSecureLegacyFlow_ShouldPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(Boolean.TRUE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock).addHeader(eq(THREED_SECURE_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.TRUE.toString());
        verify(responseMock).addHeader(eq(THREED_SECURE_FLEX_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.FALSE.toString());
        assertThat(result.getThreeDSecureInfo()).isNotNull();
        assertThat(result.isThreeDSecureNeeded()).isTrue();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIsNot3DSecureLegacyFlow_ShouldNotPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(Boolean.FALSE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLOW), anyString());
        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLEX_FLOW), anyString());
        assertThat(result.getThreeDSecureInfo()).isNull();
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIs3DSecureFlexFlow_ShouldPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(Boolean.TRUE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock).addHeader(eq(THREED_SECURE_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.TRUE.toString());
        verify(responseMock).addHeader(eq(THREED_SECURE_FLEX_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.TRUE.toString());
        assertThat(result.getThreeDSecureInfo()).isNotNull();
        assertThat(result.getThreeDSecureInfo().getThreeDSFlexData()).isEqualTo(threeDSFlexData);
        assertThat(result.isThreeDSecureNeeded()).isTrue();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIsNot3DSecureFlexFlow_ShouldNotPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(Boolean.FALSE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLOW), anyString());
        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLEX_FLOW), anyString());
        assertThat(result.getThreeDSecureInfo()).isNull();
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIsCancelled_ShouldPopulateStatusBadRequest() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isCancelled(directResponseDataMock)).thenReturn(Boolean.TRUE);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock).setStatus(integerArgumentCaptor.capture());
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIsNotCancelled_ShouldNotPopulateStatusBadRequest() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isCancelled(directResponseDataMock)).thenReturn(Boolean.FALSE);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnException_ShouldAlwaysPopulateTransactionStatusAndReturnCode() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        assertThat(result.getTransactionStatus()).isEqualTo(TransactionStatus.AUTHORISED);
        assertThat(result.getReturnCode()).isEqualTo(RETURN_CODE);
    }

    @Test
    public void isPaymentMethodAvailable_WhenApmAvailabilityFacadeIsAvailableReturnTrue_ShouldReturnTrue() {
        when(apmAvailabilityFacadeMock.isAvailable(PAYMENT_METHOD)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.isPaymentMethodAvailable(PAYMENT_METHOD);
        verify(apmAvailabilityFacadeMock).isAvailable(PAYMENT_METHOD);

        assertThat(result).isTrue();
    }

    @Test
    public void isPaymentMethodAvailable_WhenApmAvailabilityFacadeIsAvailableReturnTrue_ShouldReturnFalse() {
        when(apmAvailabilityFacadeMock.isAvailable(PAYMENT_METHOD)).thenReturn(Boolean.FALSE);

        final boolean result = testObj.isPaymentMethodAvailable(PAYMENT_METHOD);
        verify(apmAvailabilityFacadeMock).isAvailable(PAYMENT_METHOD);

        assertThat(result).isFalse();
    }

    @Test
    public void addBillingAddressToCart_ShouldPopulateFieldsWithDataMapperAndWorldpayAddressPopulatorAddingAddressWithUserFacadeAndSetBillingDetails() {
        testObj.addBillingAddressToCart(addressWsDTOMock, DEFAULT_LEVEL);

        final InOrder inOrder = Mockito.inOrder(dataMapperMock, worldpayAdressWsDTOAddressDataPopulatorMock, userFacadeMock, worldpayPaymentCheckoutFacadeMock);
        inOrder.verify(dataMapperMock).map(addressWsDTOMock, AddressData.class, DEFAULT_LEVEL);
        inOrder.verify(worldpayAdressWsDTOAddressDataPopulatorMock).populate(addressWsDTOMock, addressDataMock);
        inOrder.verify(userFacadeMock).addAddress(addressDataMock);
        inOrder.verify(worldpayPaymentCheckoutFacadeMock).setBillingDetails(addressDataMock);
    }

    @Test
    public void getRedirectAuthorise_WhenPaymentMethodIsNotAvailable_ShouldThrowAnIllegalArgumentException() {
        final PaymentRequestData paymentRequestData = new PaymentRequestData();
        paymentRequestData.setPaymentMethod(PAYMENT_METHOD_NOT_SUPPORTED);

        when(apmAvailabilityFacadeMock.isAvailable(PAYMENT_METHOD_NOT_SUPPORTED)).thenReturn(Boolean.FALSE);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> testObj.getRedirectAuthorise(paymentRequestData, requestMock, FieldSetLevelHelper.FULL_LEVEL));

        assertThat(exception.getMessage()).isEqualTo("Payment method [" + PAYMENT_METHOD_NOT_SUPPORTED + "] is not supported");
    }

    @Test
    public void getRedirectAuthorise_WhenIsBankTransfer_ShouldCallAuthoriseBankTransferRedirect() throws WorldpayException {
        final PaymentRequestData paymentRequestData = new PaymentRequestData();
        paymentRequestData.setPaymentMethod(PaymentType.IDEAL.getMethodCode());

        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayBankConfigurationFacadeMock.isBankTransferApm(PaymentType.IDEAL.getMethodCode())).thenReturn(Boolean.TRUE);

        testObj.getRedirectAuthorise(paymentRequestData, requestMock, FieldSetLevelHelper.FULL_LEVEL);

        verify(worldpayDirectOrderFacadeMock).authoriseBankTransferRedirect(any(BankTransferAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));
        verify(dataMapperMock).map(any(), eq(PaymentDataWsDTO.class), eq(FULL_LEVEL));
    }

    @Test
    public void getRedirectAuthorise_WhenIsNotBankTransfer_ShouldCallRedirectAuthorise() throws WorldpayException {
        final PaymentRequestData paymentRequestData = new PaymentRequestData();
        paymentRequestData.setPaymentMethod(PaymentType.VISA.getMethodCode());

        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(worldpayBankConfigurationFacadeMock.isBankTransferApm(PaymentType.VISA.getMethodCode())).thenReturn(Boolean.FALSE);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(any(AdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock))).thenReturn(paymentDataMock);

        testObj.getRedirectAuthorise(paymentRequestData, requestMock, FieldSetLevelHelper.FULL_LEVEL);

        verify(worldpayHostedOrderFacadeMock).redirectAuthorise(any(AdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));
        verify(dataMapperMock).map(any(), eq(PaymentDataWsDTO.class), eq(FULL_LEVEL));
    }

    @Test
    public void createCartDeliveryAndBillingAddress_ShouldSetAddressesOnCart() {
        testObj.createCartDeliveryAndBillingAddress(addressWsDTOMock, DEFAULT_LEVEL);

        final InOrder inOrder = Mockito.inOrder(dataMapperMock, worldpayAdressWsDTOAddressDataPopulatorMock, userFacadeMock, worldpayPaymentCheckoutFacadeMock);
        inOrder.verify(dataMapperMock).map(addressWsDTOMock, AddressData.class, DEFAULT_LEVEL);
        inOrder.verify(worldpayAdressWsDTOAddressDataPopulatorMock).populate(addressWsDTOMock, addressDataMock);
        inOrder.verify(userFacadeMock).addAddress(addressDataMock);
        inOrder.verify(worldpayPaymentCheckoutFacadeMock).setShippingAndBillingDetails(addressDataMock);
    }

    @Test
    public void isPaymentMethodAvailable_ShouldCallAvailabilityFacade() {
        testObj.isPaymentMethodAvailable(PAYMENT_METHOD);

        verify(apmAvailabilityFacadeMock).isAvailable(PAYMENT_METHOD);
    }
}
