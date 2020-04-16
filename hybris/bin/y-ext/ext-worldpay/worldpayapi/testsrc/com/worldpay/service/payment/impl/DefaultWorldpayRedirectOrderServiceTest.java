package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.*;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static org.junit.Assert.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRedirectOrderServiceTest {

    private static final String REDIRECT_URL = "http://www.example.com";
    private static final double TOTAL_PRICE = 100D;
    private static final double PAYMENT_AMOUNT = 100d;
    private static final String GBP = "GBP";
    private static final String LANGUAGE_ISO_CODE = "en";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String FULL_SUCCESS_URL = "fullSuccessUrl";
    private static final String FULL_PENDING_URL = "fullPendingUrl";
    private static final String FULL_FAILURE_URL = "fullFailureUrl";
    private static final String FULL_CANCEL_URL = "fullCancelUrl";
    private static final String FULL_ERROR_URL = "fullErrorUrl";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";


    private static final String KEY_MAC = "mac";
    private static final String KEY_MAC2 = "mac2";
    private static final String ORDER_KEY = "orderKey";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CANCEL_URL = "cancelURL";
    private static final String KEY_SUCCESS_URL = "successURL";
    private static final String KEY_PENDING_URL = "pendingURL";
    private static final String KEY_FAILURE_URL = "failureURL";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String KEY_PAYMENT_AMOUNT = "paymentAmount";
    private static final String KEY_PAYMENT_CURRENCY = "paymentCurrency";
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String LANGUAGE_SESSION_ATTRIBUTE_KEY = "language";

    @Spy
    @InjectMocks
    private DefaultWorldpayRedirectOrderService testObj;

    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private RedirectAuthoriseServiceRequest redirectAuthoriseServiceRequestMock;
    @Mock
    private RedirectAuthoriseServiceResponse redirectAuthoriseServiceResponseMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private RedirectReference redirectReferenceMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private AddressModel potentialPickupAddressModelMock, cartPaymentAddressModelMock, clonedAddressMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayURIService worldpayURIServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private Shopper shopperMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategyMock;
    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private BigDecimal bigDecimalMock;
    @Mock
    private AddressService addressServiceMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private WorldpayRequestFactory worldpayRequestFactoryMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private LanguageModel currentSessionLanguageMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock)).thenReturn(redirectAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenReturn(redirectAuthoriseServiceResponseMock);
        when(redirectAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectAuthoriseServiceRequestMock.getOrder().getBillingAddress().getCountryCode()).thenReturn(COUNTRY_CODE);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(redirectReferenceMock.getValue()).thenReturn(REDIRECT_URL);
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(FULL_PENDING_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FULL_FAILURE_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(FULL_CANCEL_URL);
        when(worldpayUrlServiceMock.getFullErrorURL()).thenReturn(FULL_ERROR_URL);
        when(sessionServiceMock.getAttribute(LANGUAGE_SESSION_ATTRIBUTE_KEY)).thenReturn(currentSessionLanguageMock);
        when(currentSessionLanguageMock.getIsocode()).thenReturn(LANGUAGE_ISO_CODE);

        when(cartModelMock.getTotalPrice()).thenReturn(TOTAL_PRICE);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(currencyModelMock.getIsocode()).thenReturn(GBP);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(cartPaymentAddressModelMock);
        when(modelServiceMock.create(PaymentInfoModel.class)).thenReturn(paymentInfoModelMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(eq(WORLDPAY_ORDER_CODE), eq(WORLDPAY_ORDER_CODE), any(Amount.class))).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.createShopper(CUSTOMER_EMAIL, null, null)).thenReturn(shopperMock);

        when(worldpayPaymentInfoServiceMock.createPaymentInfo(cartModelMock)).thenReturn(paymentInfoModelMock);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(customerModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(potentialPickupAddressModelMock);
        when(addressServiceMock.cloneAddressForOwner(cartPaymentAddressModelMock, paymentInfoModelMock)).thenReturn(clonedAddressMock);
        doReturn(commerceCheckoutParameterMock).when(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, bigDecimalMock);
    }

    @Test
    public void redirectAuthorise_ShouldReturnPaymentDataCorrectlySet_WhenItIsCall() throws WorldpayException {
        final PaymentData result = testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        verify(sessionServiceMock).setAttribute(WORLDPAY_MERCHANT_CODE, MERCHANT_CODE);
        verify(worldpayURIServiceMock).extractUrlParamsToMap(eq(REDIRECT_URL), anyMapOf(String.class, String.class));
        assertEquals(REDIRECT_URL, result.getPostUrl());
        assertEquals(COUNTRY_CODE, result.getParameters().get(KEY_COUNTRY));
        assertEquals(LANGUAGE_ISO_CODE, result.getParameters().get(LANGUAGE_SESSION_ATTRIBUTE_KEY));
        assertEquals(FULL_SUCCESS_URL, result.getParameters().get(KEY_SUCCESS_URL));
        assertEquals(FULL_PENDING_URL, result.getParameters().get(KEY_PENDING_URL));
        assertEquals(FULL_FAILURE_URL, result.getParameters().get(KEY_FAILURE_URL));
        assertEquals(FULL_CANCEL_URL, result.getParameters().get(KEY_CANCEL_URL));
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenWorldpayServiceGatewayRiseIt() throws WorldpayException {
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenThrow(new WorldpayException(("Response Error")));

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenWorldpayServiceGatewayResponseIsNull() throws WorldpayException {
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenReturn(null);

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenRedirectReferenceFromRedirectResponseIsNull() throws WorldpayException {
        when(redirectAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(null);
        when(redirectAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenRedirectReferenceValueIsNull() throws WorldpayException {
        when(redirectReferenceMock.getValue()).thenReturn(null);

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToTrue() {
        when(redirectAuthoriseResultMock.getSaveCard()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, bigDecimalMock);
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToFalse() {
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, bigDecimalMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseWithNoPendingPaymentTransactionEntryAndSetSavedPaymentInfoToTrue() {
        when(redirectAuthoriseResultMock.getSaveCard()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED, false);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeConfirmedRedirectAuthorise(bigDecimalMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, bigDecimalMock);
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseWithNoPendingPaymentTransactionEntryAndSetSavedPaymentInfoToFalse() {
        setUpRedirectAuthoriseResultMock(AUTHORISED, false);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeConfirmedRedirectAuthorise(bigDecimalMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, bigDecimalMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCheckResponseIsValidWhenNotUsingMacValidation() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(false);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithOrderKeyOnly());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsValidWhenRedirectResponseStatusIsOPEN() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithOrderKeyOnly());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsValidWhenUsingMacValidationAndRedirectResultStatusIsAUTHORISED() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        doReturn(true).when(testObj).validateResponse(merchantInfoMock, ORDER_KEY, KEY_MAC, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponse());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenUsingMacValidationAndRedirectResultStatusIsAUTHORISED() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);

        doReturn(false).when(testObj).validateResponse(merchantInfoMock, ORDER_KEY, KEY_MAC, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponse());

        assertFalse(result);
    }

    @Test
    public void testCheckResponseIsValidWhenUsingMacValidationWithMac2ParameterAndRedirectResultStatusIsAUTHORISED() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);

        doReturn(true).when(testObj).validateResponse(merchantInfoMock, ORDER_KEY, KEY_MAC2, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponseWithMac2());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenUsingMacValidationWithMac2ParameterAndRedirectResultStatusIsAUTHORISED() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);

        doReturn(false).when(testObj).validateResponse(merchantInfoMock, ORDER_KEY, KEY_MAC2, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponseWithMac2());

        assertFalse(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenNoOrderKeyPresent() {
        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithNoOrderKey());

        assertFalse(result);
    }


    private Map<String, String> createFullWorldpayResponse() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(PAYMENT_STATUS, AUTHORISED.name());
        worldpayResponse.put(ORDER_KEY, ORDER_KEY);
        worldpayResponse.put(KEY_MAC, KEY_MAC);
        worldpayResponse.put(KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT));
        worldpayResponse.put(KEY_PAYMENT_CURRENCY, GBP);
        return worldpayResponse;
    }

    private Map<String, String> createFullWorldpayResponseWithMac2() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(PAYMENT_STATUS, AUTHORISED.name());
        worldpayResponse.put(ORDER_KEY, ORDER_KEY);
        worldpayResponse.put(KEY_MAC2, KEY_MAC2);
        worldpayResponse.put(KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT));
        worldpayResponse.put(KEY_PAYMENT_CURRENCY, GBP);
        return worldpayResponse;
    }

    private Map<String, String> createWorldpayResponseWithNoOrderKey() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(PAYMENT_STATUS, AUTHORISED.name());
        worldpayResponse.put(KEY_MAC, KEY_MAC);
        worldpayResponse.put(KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT));
        worldpayResponse.put(KEY_PAYMENT_CURRENCY, GBP);
        return worldpayResponse;
    }

    private Map<String, String> createWorldpayResponseWithOrderKeyOnly() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(ORDER_KEY, ORDER_KEY);
        return worldpayResponse;
    }

    private void setUpRedirectAuthoriseResultMock(final AuthorisedStatus paymentStatus, final boolean pending) {
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(paymentStatus);
        when(redirectAuthoriseResultMock.getOrderKey()).thenReturn(ORDER_KEY);
        when(redirectAuthoriseResultMock.getPending()).thenReturn(pending);
        when(redirectAuthoriseResultMock.getPaymentAmount()).thenReturn(bigDecimalMock);
    }
}
