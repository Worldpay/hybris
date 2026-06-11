package com.worldpay.worldpayextb2bocc.controllers;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.order.WorldpayAPMPaymentInfoWsDTO;
import com.worldpay.dto.payment.PaymentDataWsDTO;
import com.worldpay.dto.payment.PaymentRequestData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayB2BDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.worldpayocccommons.exceptions.NoCheckoutCartException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.FULL_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayB2BCartsControllerTest {

    private static final String PAYMENT_METHOD_NOT_SUPPORTED = "paymentMethodNotSupported";
    private static final String CART_ID = "cartId";
    private static final String THREED_SECURE_FLOW = "3D-Secure-Flow";
    private static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    private static final String RETURN_CODE = "returnCode";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String APM_CODE = "apmCode";
    private static final String APM_NAME = "apmName";
    private static final String ERROR = "error";

    @Spy
    @InjectMocks
    private WorldpayB2BCartsController testObj;

    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private WorldpayB2BDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacadeMock;
    @Mock
    private UserFacade userFacadeMock;
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
    @Mock(name = "checkoutFacade")
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator worldpayB2BAcceleratorCheckoutFacadeDecoratorMock;
    @Mock
    private CartLoaderStrategy cartLoaderStrategyMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    protected MessageSource messageSourceMock;
    @Mock
    protected I18NService i18nServiceMock;

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
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private
    CartData cartDataMock;
    @Mock
    private CartWsDTO cartWsDTOMock;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Before
    public void setUp() throws WorldpayException, InvalidCartException {
        lenient().when(dataMapperMock.map(addressWsDTOMock, AddressData.class, DEFAULT_LEVEL)).thenReturn(addressDataMock);
        when(apmAvailabilityFacadeMock.isAvailable(or(eq(PaymentType.IDEAL.getMethodCode()), eq(PaymentType.VISA.getMethodCode())))).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        doReturn(cseAdditionalAuthInfoMock).when(testObj).callSuperCreateCSESubscriptionAdditionalAuthInfo(paymentDetailsWsDTOMock);
        doReturn(worldpayAdditionalInfoDataMock).when(testObj).callSuperCreateWorldpayAdditionalInfo(requestMock, paymentDetailsWsDTOMock, CART_ID, cseAdditionalAuthInfoMock);
        lenient().when(dataMapperMock.map(ccPaymentInfoDataMock, PaymentDetailsWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL)).thenReturn(paymentDetailsWsDTOMock);
        when(worldpayDirectOrderFacadeMock.executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHORISED);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL)).thenReturn(orderWsDTOMock);
        doNothing().when(cartLoaderStrategyMock).loadCart(anyString());
        doNothing().when(testObj).validateCart();

        doAnswer(invocationOnMock -> {
            final PaymentDetailsWsDTO paymentDetails = invocationOnMock.getArgument(1);
            paymentDetails.setBillingAddress(addressWsDTOMock);

            return null;
        }).when(httpRequestPaymentDetailsWsDTOPopulatorMock).populate(eq(requestMock), any(PaymentDetailsWsDTO.class), any());
        when(i18nServiceMock.getCurrentLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test(expected = WebserviceValidationException.class)
    public void addPaymentDetails_WhenPaymentDetailsAreNotValid_ShouldThrowWebserviceValidationException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WebserviceValidationException.class).when(testObj).callSuperAddPaymentDetailsInternal(eq(requestMock), any(PaymentDetailsWsDTO.class), eq(FieldSetLevelHelper.DEFAULT_LEVEL));

        testObj.addPaymentDetails(requestMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void addPaymentDetails_WhenPaymentDetailsAreValidCheckoutCartExistsAndTokenizeDoesNotThrowAnException_ShouldPopulatePaymentDetailsFromRequestAndAddThem() throws WorldpayException, NoCheckoutCartException {
        doReturn(paymentDetailsWsDTOMock).when(testObj).callSuperAddPaymentDetailsInternal(eq(requestMock), any(PaymentDetailsWsDTO.class), eq(FieldSetLevelHelper.DEFAULT_LEVEL));

        final PaymentDetailsWsDTO result = testObj.addPaymentDetails(requestMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        verify(httpRequestPaymentDetailsWsDTOPopulatorMock).populate(eq(requestMock), any(PaymentDetailsWsDTO.class), anyCollection());

        assertThat(result).isEqualTo(paymentDetailsWsDTOMock);
    }

    @Test(expected = WebserviceValidationException.class)
    public void addPaymentDetailsWithRequestBody_WhenPaymentDetailsAreNotValid_ShouldThrowWebserviceValidationException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WebserviceValidationException.class).when(testObj).callSuperAddPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);

        testObj.addPaymentDetails(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }


    @Test(expected = WebserviceValidationException.class)
    public void addPaymentDetailsAndPlaceOrder_WhenCartIsNotValid_ShouldThrowWebserviceValidationException() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        doThrow(WebserviceValidationException.class).when(testObj).validateCart();

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test(expected = WorldpayException.class)
    public void addPaymentDetailsAndPlaceOrder_WhenExecuteFirstPaymentAuthorisation3DSecureThrowsWorldpayException_ShouldThrowWorldpayException() throws WorldpayException, NoCheckoutCartException, InvalidCartException {
        doThrow(WorldpayException.class).when(worldpayDirectOrderFacadeMock).executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test(expected = InvalidCartException.class)
    public void addPaymentDetailsAndPlaceOrder_WhenExecuteFirstPaymentAuthorisation3DSecureThrowsInvalidCartException_ShouldThrowInvalidCartException() throws WorldpayException, NoCheckoutCartException, InvalidCartException {
        doThrow(InvalidCartException.class).when(worldpayDirectOrderFacadeMock).executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValid_ShouldPopulateOrderInResponse() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isAuthorised(directResponseDataMock)).thenReturn(Boolean.TRUE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        assertThat(result.getTransactionStatus()).isEqualTo(TransactionStatus.AUTHORISED);
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValid_ShouldNotPopulateOrderInResponse() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isAuthorised(directResponseDataMock)).thenReturn(Boolean.FALSE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        assertThat(result.getOrder()).isNull();
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValid_ShouldNotPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(Boolean.FALSE);

        final PlaceOrderResponseWsDTO result = testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLOW), anyString());
        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLEX_FLOW), anyString());
        assertThat(result.getThreeDSecureInfo()).isNull();
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValid_ShouldPopulateStatusBadRequest() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.isCancelled(directResponseDataMock)).thenReturn(Boolean.TRUE);

        testObj.addPaymentDetailsAndPlaceOrder(requestMock, responseMock, paymentDetailsWsDTOMock, CART_ID);

        verify(responseMock).setStatus(integerArgumentCaptor.capture());
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void addPaymentDetailsAndPlaceOrder_WhenHasCheckoutCartPaymentDetailsAreValid_ShouldNotPopulateStatusBadRequest() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
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

    @Test
    public void createAPMPaymentInfo_ShouldCreateAndMapCartData() {
        final WorldpayAPMPaymentInfoWsDTO apmPaymentInfoWsDto = new WorldpayAPMPaymentInfoWsDTO();
        apmPaymentInfoWsDto.setApmCode(APM_CODE);
        apmPaymentInfoWsDto.setApmName(APM_NAME);

        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayPaymentInfoServiceMock.createAPMPaymentInfo(cartModelMock, APM_CODE, APM_NAME)).thenReturn(paymentInfoModelMock);
        when(cartFacadeMock.getCurrentCart()).thenReturn(cartDataMock);
        when(dataMapperMock.map(cartDataMock, CartWsDTO.class, FULL_LEVEL)).thenReturn(cartWsDTOMock);

        final CartWsDTO result = testObj.createAPMPaymentInfo(apmPaymentInfoWsDto, FULL_LEVEL);

        verify(worldpayPaymentInfoServiceMock).createAPMPaymentInfo(cartModelMock, APM_CODE, APM_NAME);
        verify(cartDataMock).setApmCode(APM_CODE);
        verify(cartDataMock).setApmName(APM_NAME);
        assertThat(result).isEqualTo(cartWsDTOMock);
    }

    @Test(expected = RuntimeException.class)
    public void createAPMPaymentInfo_WhenServiceThrows_ShouldPropagateException() {
        final WorldpayAPMPaymentInfoWsDTO apmPaymentInfoWsDto = new WorldpayAPMPaymentInfoWsDTO();
        apmPaymentInfoWsDto.setApmCode(APM_CODE);
        apmPaymentInfoWsDto.setApmName(APM_NAME);

        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        doThrow(new RuntimeException(ERROR)).when(worldpayPaymentInfoServiceMock)
                .createAPMPaymentInfo(cartModelMock, APM_CODE, APM_NAME);

        testObj.createAPMPaymentInfo(apmPaymentInfoWsDto, FULL_LEVEL);
    }
}
