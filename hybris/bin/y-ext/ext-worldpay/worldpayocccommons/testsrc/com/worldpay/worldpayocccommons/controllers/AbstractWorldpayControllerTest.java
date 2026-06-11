package com.worldpay.worldpayocccommons.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.Browser;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.BrowserInfoWsDTO;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.payment.AchDirectDebitPaymentWsDTO;
import com.worldpay.enums.AchDirectDebitAccountType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.worldpayocccommons.exceptions.NoCheckoutCartException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.validators.CompositeValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractWorldpayControllerTest {

    private static final String SESSION_ID = "1508416";
    private static final String ISSUER_URL = "issuerURL";
    private static final String RETURN_CODE = "returnCode";
    private static final String PA_REQUEST = "paRequest";
    private static final String TIME_ZONE = "timeZone";
    private static final String LANGUAGE = "language";
    private static final int SCREEN_HEIGHT = 1080;
    private static final int SCREEN_WIDTH = 1200;
    private static final int COLOR_DEPTH = 24;
    private static final String CVC = "cvc";
    private static final String CART_ID = "cartId";
    private static final String THREED_SECURE_FLOW = "3D-Secure-Flow";
    private static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    private static final String ORDER_CODE = "order123";
    private static final String DIFFERENT_CODE = "differentCode";
    private static final String PAYMENT_ID = "paymentId";
    private static final String CHALLENGE_WINDOWS_SIZE = "600";
    private static final String DF_REFERENCE_ID = "dfid";
    private static final String CHALLENGE_PREFERENCE = "CHALLENGE";
    private static final String CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE = "checkout.multi.worldpay.declined.message.";
    private static final String CHECKOUT_MULTI_WORLDPAY_DECLINED_MESSAGE_DEFAULT = "checkout.multi.worldpay.declined.message.default";


    @Spy
    @InjectMocks
    private AbstractWorldpayController testObj;

    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private Authentication authenticationMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private Validator paymentDetailsDTOValidatorMock;
    @Mock
    private ConfigurablePopulator<HttpServletRequest, PaymentDetailsWsDTO, PaymentDetailsWsDTOOption> httpRequestPaymentDetailsWsDTOPopulatorMock;
    @Mock
    private Populator<AddressWsDTO, AddressData> worldpayAdressWsDTOAddressDataPopulatorMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacadeMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private CompositeValidator worldpayPlaceOrderCartValidatorMock;
    @Mock
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverterMock;
    @Mock
    protected CartFacade cartFacadeCommercewebservicesMock;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private BrowserInfoWsDTO browserInfoWsDTOMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private Browser browserMock;
    @Mock
    private PaymentDetailsWsDTO paymentDetailsWsDTOMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoDataMock;
    @Mock
    private AddressWsDTO addressWsDTOMock;
    @Mock
    private AddressData addressDataMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private AchDirectDebitPaymentWsDTO achDirectDebitPaymentWsDTOMock;
    @Mock
    protected MessageSource messageSourceMock;
    @Mock
    protected I18NService i18nServiceMock;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private static final Map<String, String> threeDSFlexData = Map.of("key", "value");

    @Before
    public void setUp() throws WorldpayException {
        lenient().when(dataMapperMock.map(addressWsDTOMock, AddressData.class, DEFAULT_LEVEL)).thenReturn(addressDataMock);

        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(paymentDetailsWsDTOMock.getBillingAddress()).thenReturn(addressWsDTOMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);
        lenient().when(dataMapperMock.map(ccPaymentInfoDataMock, PaymentDetailsWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL)).thenReturn(paymentDetailsWsDTOMock);
        when(worldpayDirectResponseFacadeMock.retrieveAttributesForFlex3dSecure(directResponseDataMock)).thenReturn(threeDSFlexData);
    }

    @Test
    public void shouldReturnGetSessionIdFromRequestWhenAuthenticationIsNull() {
        final String result = testObj.getSessionId(requestMock);

        assertEquals(SESSION_ID, result);
    }

    @Test
    public void shouldReturnGetSessionIdFromRequestWhenDetailsAreNull() {
        SecurityContextHolder.getContext().setAuthentication(authenticationMock);

        final String result = testObj.getSessionId(requestMock);

        assertEquals(SESSION_ID, result);
    }

    @Test
    public void shouldReturnGetSessionIdFromAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(authenticationMock);

        final String result = testObj.getSessionId(requestMock);

        assertEquals(SESSION_ID, result);
    }

    @Test
    public void handleDirectResponseShouldReturnPlaceOrderResponseWsDTO() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);

        final DirectResponseData directResponseData = new DirectResponseData();
        directResponseData.setIssuerURL(ISSUER_URL);
        directResponseData.setPaRequest(PA_REQUEST);
        directResponseData.setTransactionStatus(TransactionStatus.AUTHENTICATION_REQUIRED);
        directResponseData.setReturnCode(RETURN_CODE);


        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseData, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertEquals(TransactionStatus.AUTHENTICATION_REQUIRED, result.getTransactionStatus());
        assertEquals(RETURN_CODE, result.getReturnCode());
    }

    @Test
    public void handleDirectResponseShouldReturnMessageWhenStatusIsRefused() {
        when(i18nServiceMock.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(messageSourceMock.getMessage(eq(CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE + RETURN_CODE), any(), eq(Locale.ENGLISH))).thenReturn("Refused decline message");

        final DirectResponseData directResponseData = new DirectResponseData();
        directResponseData.setReturnCode(RETURN_CODE);
        directResponseData.setTransactionStatus(TransactionStatus.REFUSED);

        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseData, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals("Refused decline message", result.getReturnMessage());
    }

    @Test
    public void handleDirectResponseShouldReturnMessageWhenStatusIsCancelled() {
        when(i18nServiceMock.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(messageSourceMock.getMessage(eq(CHECKOUT_MULTI_WORLDPAY_DECLINED_MESSAGE_DEFAULT), any(), eq(Locale.ENGLISH))).thenReturn("Default decline message");

        final DirectResponseData directResponseData = new DirectResponseData();
        directResponseData.setTransactionStatus(TransactionStatus.CANCELLED);
        directResponseData.setReturnCode(RETURN_CODE);

        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseData, FieldSetLevelHelper.DEFAULT_LEVEL);

        assertEquals(TransactionStatus.CANCELLED, result.getTransactionStatus());
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals("Default decline message", result.getReturnMessage());
    }

    @Test
    public void createWorldpayAdditionalInfo_shouldReturnWorldpayAdditionalInfoData() {
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(browserInfoWsDTOMock.getJavaEnabled()).thenReturn(Boolean.TRUE);
        when(browserInfoWsDTOMock.getJavascriptEnabled()).thenReturn(Boolean.TRUE);
        when(browserInfoWsDTOMock.getScreenHeight()).thenReturn(SCREEN_HEIGHT);
        when(browserInfoWsDTOMock.getScreenWidth()).thenReturn(SCREEN_WIDTH);
        when(browserInfoWsDTOMock.getColorDepth()).thenReturn(COLOR_DEPTH);
        when(browserInfoWsDTOMock.getTimeZone()).thenReturn(TIME_ZONE);
        when(browserInfoWsDTOMock.getLanguage()).thenReturn(LANGUAGE);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfo(requestMock, CVC, browserInfoWsDTOMock);

        verify(result).setSessionId(SESSION_ID);
        verify(result).setSecurityCode(CVC);
        verify(result).setJavaEnabled(Boolean.TRUE);
        verify(result).setJavascriptEnabled(Boolean.TRUE);
        verify(result).setTimeZone(TIME_ZONE);
        verify(result).setScreenHeight(SCREEN_HEIGHT);
        verify(result).setScreenWidth(SCREEN_WIDTH);
        verify(result).setLanguage(LANGUAGE);
        verify(result).setColorDepth(COLOR_DEPTH);
    }

    @Test(expected = NoCheckoutCartException.class)
    public void addPaymentDetails_WhenHasNotCheckoutCart_ShouldThrowNoCheckoutCartException() throws WorldpayException, NoCheckoutCartException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.FALSE);

        testObj.addPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void addPaymentDetails_WhenTokenizeThrowsWorldpayException_ShouldThrowWorldpayException() throws WorldpayException, NoCheckoutCartException {
        doThrow(WorldpayException.class).when(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.TRUE);

        testObj.addPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = NoCheckoutCartException.class)
    public void addPaymentDetailsWithRequestBody_WhenHasNotCheckoutCart_ShouldThrowNoCheckoutCartException() throws WorldpayException, NoCheckoutCartException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(Boolean.FALSE);

        testObj.addPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test(expected = WorldpayException.class)
    public void addPaymentDetailsWithRequestBody_WhenTokenizeThrowsWorldpayException_ShouldThrowWorldpayException() throws WorldpayException, NoCheckoutCartException {
        doNothing().when(testObj).validatePayment(paymentDetailsWsDTOMock);
        doThrow(WorldpayException.class).when(worldpayDirectOrderFacadeMock).tokenize(any(CSEAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock));

        testObj.addPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void addPaymentDetailsWithRequestBody_WhenPaymentDetailsAreValidCheckoutCartExistsAndTokenizeDoesNotThrowAnException_ShouldPopulatePaymentDetailsFromRequestAndAddThem() throws WorldpayException, NoCheckoutCartException {

        final PaymentDetailsWsDTO result = testObj.addPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, FieldSetLevelHelper.DEFAULT_LEVEL);

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

        testObj.addPaymentDetailsInternal(requestMock, paymentDetailsWsDTOMock, CART_ID);
    }


    @Test
    public void handleDirectResponse_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIs3DSecureLegacyFlow_ShouldPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(Boolean.TRUE);

        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseDataMock, responseMock, CART_ID);

        verify(responseMock).addHeader(eq(THREED_SECURE_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.TRUE.toString());
        verify(responseMock).addHeader(eq(THREED_SECURE_FLEX_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.FALSE.toString());
        assertThat(result.getThreeDSecureInfo()).isNotNull();
        assertThat(result.isThreeDSecureNeeded()).isTrue();
    }

    @Test
    public void handleDirectResponse_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIsNot3DSecureLegacyFlow_ShouldNotPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(Boolean.FALSE);

        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseDataMock, responseMock, CART_ID);

        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLOW), anyString());
        verify(responseMock, never()).addHeader(eq(THREED_SECURE_FLEX_FLOW), anyString());
        assertThat(result.getThreeDSecureInfo()).isNull();
        assertThat(result.isThreeDSecureNeeded()).isFalse();
    }

    @Test
    public void handleDirectResponse_WhenHasCheckoutCartPaymentDetailsAreValidAndExecuteFirstPaymentAuthorisation3DSecureDoesNotThrowAnExceptionAndIs3DSecureFlexFlow_ShouldPopulateThreeDSecureInfo() throws InvalidCartException, NoCheckoutCartException, WorldpayException {
        when(worldpayDirectResponseFacadeMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(Boolean.TRUE);

        final PlaceOrderResponseWsDTO result = testObj.handleDirectResponse(directResponseDataMock, responseMock, CART_ID);

        verify(responseMock).addHeader(eq(THREED_SECURE_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.TRUE.toString());
        verify(responseMock).addHeader(eq(THREED_SECURE_FLEX_FLOW), stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(Boolean.TRUE.toString());
        assertThat(result.getThreeDSecureInfo()).isNotNull();
        assertThat(result.getThreeDSecureInfo().getThreeDSFlexData()).isEqualTo(threeDSFlexData);
        assertThat(result.isThreeDSecureNeeded()).isTrue();
    }

    @Test
    public void createCseAdditionalAuthInfo_ShouldMapFields() {
        PaymentDetailsWsDTO dto = new PaymentDetailsWsDTO();
        dto.setChallengeWindowSize("01");
        dto.setDfReferenceId(DF_REFERENCE_ID);
        dto.setCseToken("token");
        dto.setSaved(Boolean.TRUE);
        dto.setAccountHolderName("holder");
        dto.setExpiryYear("2025");
        dto.setExpiryMonth("12");

        final CSEAdditionalAuthInfo result = testObj.createCseAdditionalAuthInfo(dto);

        assertThat(result.getEncryptedData()).isEqualTo("token");
        assertThat(result.getSaveCard()).isTrue();
        assertThat(result.getCardHolderName()).isEqualTo("holder");
        assertThat(result.getExpiryYear()).isEqualTo("2025");
        assertThat(result.getExpiryMonth()).isEqualTo("12");
        assertThat(result.getAdditional3DS2().getChallengeWindowSize()).isEqualTo("01");
        assertThat(result.getAdditional3DS2().getDfReferenceId()).isEqualTo(DF_REFERENCE_ID);
    }

    @Test
    public void createCSESubscriptionAdditionalAuthInfo_ShouldSetChallengePreference() {
        WorldpayMerchantConfigData configData = mock(WorldpayMerchantConfigData.class);
        when(configData.getThreeDSFlexChallengePreference()).thenReturn(CHALLENGE_PREFERENCE);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(configData);

        final CSEAdditionalAuthInfo result = testObj.createCSESubscriptionAdditionalAuthInfo(CHALLENGE_WINDOWS_SIZE, DF_REFERENCE_ID);

        assertThat(result.getAdditional3DS2().getChallengeWindowSize()).isEqualTo(CHALLENGE_WINDOWS_SIZE);
        assertThat(result.getAdditional3DS2().getDfReferenceId()).isEqualTo(DF_REFERENCE_ID);
        assertThat(result.getAdditional3DS2().getChallengePreference()).isEqualTo(CHALLENGE_PREFERENCE);
    }

    @Test
    public void createWorldpayAdditionalInfo_ShouldSetSessionId() {
        final WorldpayAdditionalInfoData infoData = new WorldpayAdditionalInfoData();
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(infoData);
        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfo(requestMock);

        assertThat(result.getSessionId()).isEqualTo(SESSION_ID);
    }

    @Test
    public void createWorldpayAdditionalInfo_WithCvcAndBrowserInfo_ShouldSetFields() {
        final WorldpayAdditionalInfoData infoData = new WorldpayAdditionalInfoData();
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(infoData);
        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);
        when(browserInfoWsDTOMock.getJavaEnabled()).thenReturn(true);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfo(requestMock, "123", browserInfoWsDTOMock);

        assertThat(result.getSessionId()).isEqualTo(SESSION_ID);
        assertThat(result.getSecurityCode()).isEqualTo("123");
        assertThat(result.getJavaEnabled()).isTrue();
    }

    @Test
    public void setBrowserInfo_ShouldSetAllFields() {
        final WorldpayAdditionalInfoData infoData = new WorldpayAdditionalInfoData();
        final BrowserInfoWsDTO browser = new BrowserInfoWsDTO();
        browser.setJavaEnabled(true);
        browser.setJavascriptEnabled(true);
        browser.setLanguage("en");
        browser.setTimeZone("GMT");
        browser.setColorDepth(32);
        browser.setScreenHeight(800);
        browser.setScreenWidth(600);

        testObj.setBrowserInfo(browser, infoData);

        assertThat(infoData.getJavaEnabled()).isTrue();
        assertThat(infoData.getJavascriptEnabled()).isTrue();
        assertThat(infoData.getLanguage()).isEqualTo("en");
        assertThat(infoData.getTimeZone()).isEqualTo("GMT");
        assertThat(infoData.getColorDepth()).isEqualTo(32);
        assertThat(infoData.getScreenHeight()).isEqualTo(800);
        assertThat(infoData.getScreenWidth()).isEqualTo(600);
    }

    @Test
    public void createWorldpayAdditionalInfo_WithCseAdditionalAuthInfoAndBrowserInfo_ShouldSetFields() {
        final WorldpayAdditionalInfoData infoData = new WorldpayAdditionalInfoData();
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(infoData);
        doReturn(SESSION_ID).when(testObj).getSessionId(requestMock);
        when(requestMock.getHeader(anyString())).thenReturn("UA");
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        Additional3DS2Info add3ds2 = new Additional3DS2Info();
        when(cseAdditionalAuthInfoMock.getAdditional3DS2()).thenReturn(add3ds2);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfo(requestMock, cseAdditionalAuthInfoMock, CART_ID, browserInfoWsDTOMock);

        assertThat(result.getSessionId()).isEqualTo(SESSION_ID);
        assertThat(result.getUserAgentHeader()).isEqualTo("UA");
        assertThat(result.getTransactionIdentifier()).isEqualTo(CART_ID);
        assertThat(result.getAdditional3DS2()).isSameAs(add3ds2);
    }

    @Test
    public void getRequestParameterMap_ShouldReturnAllParams() {
        when(requestMock.getParameterNames()).thenReturn(Collections.enumeration(List.of("a", "b")));
        when(requestMock.getParameter("a")).thenReturn("1");
        when(requestMock.getParameter("b")).thenReturn("2");

        Map<String, String> result = testObj.getRequestParameterMap(requestMock);

        assertThat(result).containsEntry("a", "1").containsEntry("b", "2");
    }

    @Test
    public void createCseAdditionalAuthInfo_WithSavedCard_ShouldSetFields() {
        final CSEAdditionalAuthInfo result = testObj.createCseAdditionalAuthInfo(CHALLENGE_WINDOWS_SIZE, DF_REFERENCE_ID, true);

        assertThat(result.getAdditional3DS2().getChallengeWindowSize()).isEqualTo(CHALLENGE_WINDOWS_SIZE);
        assertThat(result.getAdditional3DS2().getDfReferenceId()).isEqualTo(DF_REFERENCE_ID);
        assertThat(result.getSaveCard()).isTrue();
    }

    @Test
    public void createACHDirectDebitAdditionalAuthInfo_ShouldMapFields() {
        when(achDirectDebitPaymentWsDTOMock.getAccountNumber()).thenReturn("acc");
        when(achDirectDebitPaymentWsDTOMock.getAccountType()).thenReturn("checking");
        when(achDirectDebitPaymentWsDTOMock.getCompanyName()).thenReturn("comp");
        when(achDirectDebitPaymentWsDTOMock.getRoutingNumber()).thenReturn("rout");
        when(achDirectDebitPaymentWsDTOMock.getCheckNumber()).thenReturn("chk");
        when(achDirectDebitPaymentWsDTOMock.getCustomIdentifier()).thenReturn("cid");
        when(worldpayPaymentCheckoutFacadeMock.hasBillingDetails()).thenReturn(true);

        final ACHDirectDebitAdditionalAuthInfo result = testObj.createACHDirectDebitAdditionalAuthInfo(achDirectDebitPaymentWsDTOMock);

        assertThat(result.getAccountNumber()).isEqualTo("acc");
        assertThat(result.getAccountType()).isEqualTo(AchDirectDebitAccountType.CHECKING);
        assertThat(result.getCompanyName()).isEqualTo("comp");
        assertThat(result.getRoutingNumber()).isEqualTo("rout");
        assertThat(result.getCheckNumber()).isEqualTo("chk");
        assertThat(result.getCustomIdentifier()).isEqualTo("cid");
        assertThat(result.getUsingShippingAsBilling()).isFalse();
        assertThat(result.getSaveCard()).isFalse();
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
    }

    @Test
    public void createWorldpayAdditionalInfo_WithCvcCseAdditionalAuthInfoAndSavedCard_ShouldSetFields() {
        final WorldpayAdditionalInfoData infoData = new WorldpayAdditionalInfoData();
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(infoData);

        final Additional3DS2Info add3ds2 = new Additional3DS2Info();
        when(cseAdditionalAuthInfoMock.getAdditional3DS2()).thenReturn(add3ds2);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfo(requestMock, CVC, cseAdditionalAuthInfoMock, CART_ID, true);

        assertThat(result.getSecurityCode()).isEqualTo(CVC);
        assertThat(result.getTransactionIdentifier()).isEqualTo(CART_ID);
        assertThat(result.getAdditional3DS2()).isSameAs(add3ds2);
    }

    @Test
    public void extractAuthoriseResultFromRequest_ShouldDelegateToConverter() {
        final Map<String, String> map = Map.of("foo", "bar");
        RedirectAuthoriseResult resultMock = mock(RedirectAuthoriseResult.class);
        when(redirectAuthoriseResultConverterMock.convert(map)).thenReturn(resultMock);

        final RedirectAuthoriseResult result = testObj.extractAuthoriseResultFromRequest(map);

        assertThat(result).isSameAs(resultMock);
    }

    @Test
    public void getPaymentInfo_ShouldReturnPaymentInfo_WhenFound() {
        when(userFacadeMock.getCCPaymentInfoForCode(PAYMENT_ID)).thenReturn(ccPaymentInfoDataMock);

        final CCPaymentInfoData result = testObj.getPaymentInfo(PAYMENT_ID);

        assertSame(ccPaymentInfoDataMock, result);
    }

    @Test
    public void getPaymentInfo_ShouldThrowRequestParameterException_WhenNotFound() {
        when(userFacadeMock.getCCPaymentInfoForCode(PAYMENT_ID)).thenReturn(null);

        assertThatThrownBy(() -> testObj.getPaymentInfo(PAYMENT_ID))
                .isInstanceOf(RequestParameterException.class)
                .hasMessageContaining(PAYMENT_ID);
    }

    @Test
    public void getPaymentInfo_ShouldThrowRequestParameterException_WhenPKException() {
        when(userFacadeMock.getCCPaymentInfoForCode(PAYMENT_ID)).thenThrow(new PK.PKException("pk error"));

        assertThatThrownBy(() -> testObj.getPaymentInfo(PAYMENT_ID))
                .isInstanceOf(RequestParameterException.class)
                .hasMessageContaining(PAYMENT_ID)
                .hasCauseInstanceOf(PK.PKException.class);
    }

    @Test
    public void validateCartForPlaceOrder_ShouldThrowNoCheckoutCartException_WhenNoCart() {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(false);

        assertThatThrownBy(() -> testObj.validateCartForPlaceOrder())
                .isInstanceOf(NoCheckoutCartException.class)
                .hasMessageContaining("no checkout cart");
    }

    @Test
    public void validateCartForPlaceOrder_ShouldThrowWebserviceValidationException_WhenValidationFails() {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);

        // Simulate validation error
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.reject("error");
            return null;
        }).when(worldpayPlaceOrderCartValidatorMock).validate(eq(cartDataMock), any(Errors.class));

        assertThatThrownBy(() -> testObj.validateCartForPlaceOrder())
                .isInstanceOf(WebserviceValidationException.class);
    }

    @Test
    public void validateCartForPlaceOrder_ShouldThrowWebserviceValidationException_WhenValidationCartFails() throws CommerceCartModificationException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);

        doNothing().when(worldpayPlaceOrderCartValidatorMock).validate(eq(cartDataMock), any(Errors.class));
        doReturn(List.of(cartDataMock)).when(cartFacadeCommercewebservicesMock).validateCartData();

        assertThatThrownBy(() -> testObj.validateCartForPlaceOrder())
                .isInstanceOf(WebserviceValidationException.class);
    }

    @Test
    public void validateCartForPlaceOrder_ShouldPass_WhenNoValidationErrors() throws InvalidCartException, NoCheckoutCartException, CommerceCartModificationException {
        when(checkoutFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);

        doReturn(null).when(cartFacadeCommercewebservicesMock).validateCartData();
        doNothing().when(worldpayPlaceOrderCartValidatorMock).validate(eq(cartDataMock), any(Errors.class));

        testObj.validateCartForPlaceOrder();
    }

    @Test
    public void validateCartForPlaceOrder_WithMatchingOrderCode_ShouldPass() throws Exception {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getWorldpayOrderCode()).thenReturn(ORDER_CODE);
        doNothing().when(testObj).validateCartForPlaceOrder();

        testObj.validateCartForPlaceOrder(ORDER_CODE);

        verify(testObj).validateCartForPlaceOrder();
        verify(cartDataMock).getWorldpayOrderCode();
    }

    @Test(expected = InvalidCartException.class)
    public void validateCartForPlaceOrder_WithNonMatchingOrderCode_ShouldThrowInvalidCartException() throws Exception {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getWorldpayOrderCode()).thenReturn(DIFFERENT_CODE);
        doNothing().when(testObj).validateCartForPlaceOrder();

        testObj.validateCartForPlaceOrder(ORDER_CODE);
    }

    @Test(expected = NoCheckoutCartException.class)
    public void validateCartForPlaceOrder_WhenNoCheckoutCart_ShouldThrowNoCheckoutCartException() throws Exception {
        doThrow(new NoCheckoutCartException("no cart")).when(testObj).validateCartForPlaceOrder();

        testObj.validateCartForPlaceOrder(ORDER_CODE);
    }

}
