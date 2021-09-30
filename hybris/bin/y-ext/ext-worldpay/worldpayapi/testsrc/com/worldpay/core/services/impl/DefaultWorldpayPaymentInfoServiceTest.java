package com.worldpay.core.services.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.model.ApplePayPaymentInfoModel;
import com.worldpay.model.GooglePayPaymentInfoModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.payment.Card;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.TokenReply;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.UATP;
import static de.hybris.platform.core.enums.CreditCardType.VISA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentInfoServiceTest {

    private static final int TIMEOUT_IN_MINUTES = 19;
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String CARD_EXPIRY_MONTH = "cardExpiryMonth";
    private static final String CARD_EXPIRY_YEAR = "cardExpiryYear";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String CARD_DETAILS_NUMBER = "cardDetailsNumber";
    private static final String CARD_DETAILS_EXPIRY_MONTH = "cardDetailsExpiryMonth";
    private static final String CARD_DETAILS_EXPIRY_YEAR = "cardDetailsExpiryYear";
    private static final String CARD_DETAILS_HOLDER_NAME = "cardDetailsHolderName";
    private static final Date CREATION_TIME = Date.from(Instant.now());
    private static final String ORDER_CODE = "orderCode";
    private static final Double TOTAL_PRICE = Double.valueOf("100.0");
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String APM_CODE = "apmCode";
    private static final String NEW = "NEW";
    private static final String MATCH = "MATCH";
    private static final String CONFLICT = "CONFLICT";
    private static final LocalDate DATE_TIME = LocalDate.of(2020, 2, 15);
    private static final String CC_PAYMENT_INFO_MODEL_CODE = "ccPaymentInfoModelCode";
    private static final String ANOTHER_SUBSCRIPTION_ID = "anotherPaymentId";
    private static final String WORLDPAY_CREDIT_CARD_MAPPINGS = "worldpay.creditCard.mappings.";
    private static final String MERCHANT_ID = "merchant19";
    private static final String PAYMENT_INFO_CODE = "00001009_ac2b99f2-3f15-494a-b33d-73166da4716d";
    private static final String PAYMENT_TYPE = "paymentType";
    private static final String PROTOCOL_VERSION = "protocolVersion";
    private static final String SIGNATURE = "signature";
    private static final String SIGNED_MESSAGE = "signedMessage";
    private static final String TRANSACTION_ID = "4ff0a4c2833b41144591ca2230eb44a5dab1373433b829125cc9099a46c53908";
    private static final String VERSION = "EC_v1";
    private static final String TRANSACTION_IDENTIFIER = "transactionIdentifier";
    private static final String BIN = "bin";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    @Spy
    @InjectMocks
    private DefaultWorldpayPaymentInfoService testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private EnumerationService enumerationServiceMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private AddressService addressServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;

    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentInfoModel paymentTransactionPaymentInfoModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private PaymentInfoModel orderPaymentInfoModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private TokenReply tokenReplyMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Card cardMock;
    @Mock
    private com.worldpay.data.Date dateMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private UserModel userModelMock;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock
    private CreditCardPaymentInfoModel savedCreditCardPaymentInfoMock;
    @Mock
    private WorldpayAPMPaymentInfoModel savedAPMPaypalPaymentInfoMock, savedAPMGooglePaymentInfoMock;
    @Mock
    private CreateTokenResponse createTokenResponseMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private UpdateTokenServiceRequest updateTokenServiceRequestMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CardDetails cardDetailsMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ApplePayAdditionalAuthInfo applePayAdditionalAuthInfoMock;
    @Mock
    private ApplePayPaymentInfoModel applePayPaymentInfoModelMock;
    @Mock
    private GooglePayAdditionalAuthInfo googlePayAdditionAuthInfoMock;
    @Mock
    private GooglePayPaymentInfoModel googlePayPaymentInfoModelMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoModelMock;
    @Mock
    private AddressModel paymentAddressModelMock, clonedAddressMock;

    private Boolean saveCard = Boolean.FALSE;

    @Captor
    private ArgumentCaptor<CommerceCheckoutParameter> commerceCheckoutParameterArgumentCaptor;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(cartModelMock.getCode()).thenReturn(ORDER_CODE);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(cartModelMock.getTotalPrice()).thenReturn(TOTAL_PRICE);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(cartModelMock.getUser()).thenReturn(userModelMock);
        when(orderModelMock.getUser()).thenReturn(userModelMock);
        when(orderModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(paymentInfoMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(savedCreditCardPaymentInfoMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayAPMPaymentInfoModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(modelServiceMock.create(PaymentInfoModel.class)).thenReturn(orderPaymentInfoModelMock);
        when(modelServiceMock.create(CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentTransactionPaymentInfoModelMock);
        when(paymentTransactionModelMock.getOrder().getPaymentInfo()).thenReturn(orderPaymentInfoModelMock);
        when(cardMock.getCardNumber()).thenReturn(CARD_NUMBER);
        when(cardMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME);
        when(cardMock.getExpiryDate().getMonth()).thenReturn(CARD_EXPIRY_MONTH);
        when(cardMock.getExpiryDate().getYear()).thenReturn(CARD_EXPIRY_YEAR);
        when(cardDetailsMock.getCardNumber()).thenReturn(CARD_DETAILS_NUMBER);
        when(cardDetailsMock.getCardHolderName()).thenReturn(CARD_DETAILS_HOLDER_NAME);
        when(cardDetailsMock.getExpiryDate().getMonth()).thenReturn(CARD_DETAILS_EXPIRY_MONTH);
        when(cardDetailsMock.getExpiryDate().getYear()).thenReturn(CARD_DETAILS_EXPIRY_YEAR);
        when(enumerationServiceMock.getEnumerationValue(CreditCardType.class.getSimpleName(), VISA.getCode())).thenReturn(VISA);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(NEW);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(paymentTransactionModelMock.getOrder().getPaymentInfo()).thenReturn(orderPaymentInfoModelMock);
        when(paymentReplyMock.getCardDetails()).thenReturn(cardMock);
        when(tokenReplyMock.getPaymentInstrument()).thenReturn(cardMock);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(tokenReplyMock.getTokenDetails().getTokenEventReference()).thenReturn(TOKEN_REFERENCE);
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(PaymentType.VISA.getMethodCode());
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + paymentReplyMock.getPaymentMethodCode())).thenReturn(VISA.getCode());
        doReturn(DATE_TIME).when(testObj).getDateTime(dateMock);
        when(tokenReplyMock.getTokenDetails().getPaymentTokenExpiry()).thenReturn(dateMock);
        when(tokenReplyMock.getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, WorldpayAPMPaymentInfoModel.class)).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(worldpayAPMPaymentInfoModelMock.getPaymentType())).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(paymentTransactionModelMock.getCreationtime()).thenReturn(CREATION_TIME);
        when(worldpayAPMConfigurationModelMock.getAutoCancelPendingTimeoutInMinutes()).thenReturn(TIMEOUT_IN_MINUTES);
        when(userModelMock.getPaymentInfos()).thenReturn(Arrays.asList(paymentInfoMock, savedCreditCardPaymentInfoMock, savedAPMPaypalPaymentInfoMock, savedAPMGooglePaymentInfoMock));
        when(savedAPMPaypalPaymentInfoMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(savedAPMGooglePaymentInfoMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(savedCreditCardPaymentInfoMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);
        when(applePayAdditionalAuthInfoMock.getVersion()).thenReturn(VERSION);
        when(applePayAdditionalAuthInfoMock.getHeader().getTransactionId()).thenReturn(TRANSACTION_ID);
        when(modelServiceMock.clone(any(PaymentTransactionModel.class), eq(CreditCardPaymentInfoModel.class))).thenReturn(creditCardPaymentInfoModelMock);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoModelMock);
        when(merchantInfoModelMock.getMerchantCode()).thenReturn(MERCHANT_ID);
        when(cartModelMock.getPaymentTransactions()).thenReturn(List.of(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getCode()).thenReturn(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void SavePaymentType_ShouldSetPaymentMethodToPaymentInfoInTransactionAndOrder() {
        testObj.savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());

        verify(paymentTransactionPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(orderPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(modelServiceMock).saveAll(orderPaymentInfoModelMock, paymentTransactionPaymentInfoModelMock);
    }

    @Test
    public void createPaymentInfo_WhenCartPassedToCreatePaymentInfoIsNull_ShouldThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("CartModel cannot be null");

        testObj.createPaymentInfo(null);
    }

    @Test
    public void savePaymentType_ShouldSavePaymentMethodToPaymentInfoInTransactionAndOrder() {
        testObj.savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());

        verify(paymentTransactionPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(orderPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(modelServiceMock).saveAll(orderPaymentInfoModelMock, paymentTransactionPaymentInfoModelMock);
    }

    @Test
    public void createPaymentInfo_ShouldCreatePaymentInfo() {
        testObj.createPaymentInfo(cartModelMock);

        verify(modelServiceMock).create(PaymentInfoModel.class);
        verify(orderPaymentInfoModelMock).setSaved(false);
        verify(orderPaymentInfoModelMock).setCode(startsWith(ORDER_CODE + "_"));
        verify(orderPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(orderPaymentInfoModelMock).setUser(userModelMock);
    }

    @Test
    public void setPaymentInfoModel_WhenEventIsMatch_ShouldUseExistingCreditCardForTokenInformationAndSaveItToPaymentTransactionAndOrder()
        throws WorldpayConfigurationException {
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verifyCardNotCreated();
    }

    @Test
    public void setPaymentInfoModel_WhenEventIsConflict_ShouldUseExistingCreditCardForTokenInformationAndSaveItToPaymentTransactionAndOrder() throws WorldpayConfigurationException {
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(CONFLICT);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verifyCardNotCreated();
    }

    @Test
    public void setPaymentInfoModel_ShouldCreateCreditCardPaymentInfoModelWithoutTokenAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(creditCardPaymentInfoModelMock).setOriginal(null);
        verify(creditCardPaymentInfoModelMock).setDuplicate(false);
        verify(creditCardPaymentInfoModelMock).setNumber(CARD_NUMBER);
        verify(creditCardPaymentInfoModelMock).setValidToMonth(CARD_EXPIRY_MONTH);
        verify(creditCardPaymentInfoModelMock).setValidToYear(CARD_EXPIRY_YEAR);
        verify(creditCardPaymentInfoModelMock).setCcOwner(CARD_HOLDER_NAME);
        verify(creditCardPaymentInfoModelMock).setType(VISA);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);

        final InOrder inOrder = inOrder(testObj, modelServiceMock, orderModelMock, paymentTransactionModelMock);

        inOrder.verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        inOrder.verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        inOrder.verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        inOrder.verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
        inOrder.verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
    }

    @Test
    public void setPaymentInfoModel_WhenPaymentTransactionHasACreditCardPaymentInfoThatHasASubscriptionId_ShouldNotCreateCreditCardPaymentInfoModel() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);

        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);
        when(paymentTransactionModelMock.getInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(modelServiceMock, never()).clone(any(CreditCardPaymentInfoModel.class));

        final InOrder inOrder = inOrder(modelServiceMock, orderModelMock, paymentTransactionModelMock);
        inOrder.verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        inOrder.verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        inOrder.verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_WhenPaymentTransactionHasACreditCardPaymentInfoThatDoesNotHaveSubscriptionId_ShouldNotCreateCreditCardPaymentInfoModel() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);

        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);
        when(paymentTransactionModelMock.getInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(null);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, WorldpayAPMPaymentInfoModel.class)).thenReturn(worldpayAPMPaymentInfoModelMock);


        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        final InOrder inOrder = inOrder(modelServiceMock, orderModelMock, paymentTransactionModelMock);

        inOrder.verify(modelServiceMock).clone(any(CreditCardPaymentInfoModel.class), eq(CreditCardPaymentInfoModel.class));
        inOrder.verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        inOrder.verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        inOrder.verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
        inOrder.verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
    }

    @Test
    public void setPaymentInfoModel_ShouldCreateCreditCardPaymentInfoModelWithTokenAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(modelServiceMock).clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class);
        verify(creditCardPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        verify(creditCardPaymentInfoModelMock).setSaved(true);
        verify(creditCardPaymentInfoModelMock).setEventReference(TOKEN_REFERENCE);
        verify(creditCardPaymentInfoModelMock).setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        verify(creditCardPaymentInfoModelMock).setNumber(CARD_NUMBER);
        verify(creditCardPaymentInfoModelMock).setValidToMonth(CARD_EXPIRY_MONTH);
        verify(creditCardPaymentInfoModelMock).setValidToYear(CARD_EXPIRY_YEAR);
        verify(creditCardPaymentInfoModelMock).setCcOwner(CARD_HOLDER_NAME);
        verify(creditCardPaymentInfoModelMock).setType(VISA);
        verify(creditCardPaymentInfoModelMock).setExpiryDate(java.util.Date.from(DATE_TIME.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_ShouldUseExistingCreditCardForTokenInformationAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getOrder().getUser()).thenReturn(userModelMock);
        when(savedCreditCardPaymentInfoMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);
        when(savedCreditCardPaymentInfoMock.isSaved()).thenReturn(true);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(savedCreditCardPaymentInfoMock);
        verify(paymentTransactionModelMock).setInfo(savedCreditCardPaymentInfoMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_IfNotFoundCreditCardForTokenInformation_ShouldCreateItAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        doReturn(DATE_TIME).when(testObj).getDateTime(dateMock);

        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getOrder().getUser()).thenReturn(userModelMock);
        when(savedCreditCardPaymentInfoMock.getSubscriptionId()).thenReturn(ANOTHER_SUBSCRIPTION_ID);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_ShouldCreateAPMPaymentInfoModelAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(worldpayAPMPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(false);
        verify(worldpayAPMPaymentInfoModelMock).setTimeoutDate(DateUtils.addMinutes(CREATION_TIME, TIMEOUT_IN_MINUTES));
        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(worldpayAPMPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(worldpayAPMPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_WhenPaymentTypeIsPaypalAndPAMPaymentInfoNotFound_ShouldCreatePaypalTokenizedPaymentInfoAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(PaymentType.PAYPAL.getMethodCode());

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(worldpayAPMPaymentInfoModelMock).setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        verify(worldpayAPMPaymentInfoModelMock).setEventReference(TOKEN_REFERENCE);
        verify(worldpayAPMPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(true);
        verify(worldpayAPMPaymentInfoModelMock).setBillingAddress(paymentAddressModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setExpiryDate(java.util.Date.from(DATE_TIME.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.PAYPAL.getMethodCode());
        verify(orderModelMock).setPaymentInfo(worldpayAPMPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(worldpayAPMPaymentInfoModelMock);
        verify(modelServiceMock).save(worldpayAPMPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_WhenPaymentTypeIsPaypalAndPAMPaymentInfoIsFound_ShouldUseExistingPaypalTokenizedPaymentInfoAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(PaymentType.PAYPAL.getMethodCode());
        when(savedAPMPaypalPaymentInfoMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);
        when(savedAPMPaypalPaymentInfoMock.isSaved()).thenReturn(Boolean.TRUE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.PAYPAL.getMethodCode());
        verify(orderModelMock).setPaymentInfo(savedAPMPaypalPaymentInfoMock);
        verify(paymentTransactionModelMock).setInfo(savedAPMPaypalPaymentInfoMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void setPaymentInfoModel_ShouldCreateAPMPaymentInfoModelAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotificationWithNullTimeout() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(worldpayAPMConfigurationModelMock.getCode()).thenReturn(APM_CODE);
        when(worldpayAPMConfigurationModelMock.getAutoCancelPendingTimeoutInMinutes()).thenReturn(null);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(worldpayAPMPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(false);
        verify(worldpayAPMPaymentInfoModelMock).setTimeoutDate(null);
        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(worldpayAPMPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(worldpayAPMPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void createCreditCardPaymentInfo_WhenCartPassedToCreateCreditCardPaymentInfoIsNull_ShouldThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("CartModel cannot be null");

        testObj.createCreditCardPaymentInfo(null, tokenReplyMock, false, MERCHANT_ID);
    }

    @Test
    public void createCreditCardPaymentInfo_ShouldCreateAndPopulateCreditCardPaymentInfoForCreateTokenResponse() {
        doReturn(CC_PAYMENT_INFO_MODEL_CODE).when(testObj).generateCcPaymentInfoCode(cartModelMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(NEW);
        when(tokenReplyMock.getPaymentInstrument().getPaymentType()).thenReturn(PaymentType.VISA.getMethodCode());
        when(tokenReplyMock.getPaymentInstrument().getBin()).thenReturn(BIN);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        doReturn(clonedAddressMock).when(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);

        testObj.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_ID);

        verify(creditCardPaymentInfoModelMock).setCode(CC_PAYMENT_INFO_MODEL_CODE);
        verify(creditCardPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(creditCardPaymentInfoModelMock).setUser(userModelMock);
        verify(creditCardPaymentInfoModelMock).setSaved(false);
        verify(creditCardPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.VISA);
        verify(creditCardPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(creditCardPaymentInfoModelMock).setMerchantId(MERCHANT_ID);
        verify(creditCardPaymentInfoModelMock).setBin(BIN);
        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);
    }

    @Test
    public void createCreditCardPaymentInfo_WhenPaymentTypeIsNull_ShouldSetCreditCardTypeToCard() {
        doReturn(CC_PAYMENT_INFO_MODEL_CODE).when(testObj).generateCcPaymentInfoCode(cartModelMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(NEW);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        doReturn(clonedAddressMock).when(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);

        testObj.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_ID);

        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.CARD);
        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);
    }

    @Test
    public void setCreditCardType_WhenCardCannotBeMatchedToAnExistingOne_ShouldSetPaymentTypeAsCard() {
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(UATP.getMethodCode());
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + UATP.getMethodCode())).thenReturn("");

        testObj.setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);

        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.CARD);
    }

    @Test
    public void setCreditCardType_WhenPaymentReplyCardCannotBeMapped_ShouldSetPaymentTypeFromTokenReplyInformation() {
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(UATP.getMethodCode());
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + UATP.getMethodCode())).thenReturn("");

        testObj.setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);

        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.CARD);
    }

    @Test
    public void createCreditCardPaymentInfo_ShouldUseExistingCreditCardForTokenInformationAndSaveItToOrderForCreateTokenResponse() {
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(savedCreditCardPaymentInfoMock.isSaved()).thenReturn(false);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);

        testObj.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, true, MERCHANT_ID);

        verify(cartModelMock).setPaymentInfo(savedCreditCardPaymentInfoMock);
        verify(savedCreditCardPaymentInfoMock).setSaved(true);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void createCreditCardPaymentInfo_IfAlreadySaved_ShouldNotUpdateExistingCard() {
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(savedCreditCardPaymentInfoMock.isSaved()).thenReturn(true);

        testObj.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_ID);

        verify(savedCreditCardPaymentInfoMock, never()).setSaved(anyBoolean());
    }

    @Test
    public void updateAndAttachPaymentInfoModel_WhenIsCalled_ShouldUpdateAndAttachPaymentInfoToOrderAndTransaction() {
        doReturn(CC_PAYMENT_INFO_MODEL_CODE).when(testObj).generateCcPaymentInfoCode(cartModelMock);

        testObj.updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);

        verify(creditCardPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(cartModelMock, paymentTransactionModelMock);
    }

    @Test
    public void createWorldpayApmPaymentInfo_ShouldCreateWorldpayApmPaymentInfo() throws WorldpayConfigurationException {
        testObj.createWorldpayApmPaymentInfo(paymentTransactionModelMock);

        verify(worldpayAPMPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(false);
        verify(worldpayAPMPaymentInfoModelMock).setMerchantId(MERCHANT_ID);
        verify(worldpayAPMPaymentInfoModelMock).setTimeoutDate(DateUtils.addMinutes(CREATION_TIME, TIMEOUT_IN_MINUTES));
    }

    @Test
    public void setCreditCardType_ShouldSetCreditCardTypeOnPaymentInfo() {

        testObj.setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);

        verify(creditCardPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.VISA);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
    }

    @Test
    public void updateCreditCardPaymentInfo_WhenSavedFlagItWasAlreadySetAsTrue_ShouldUpdateCreditCardPaymentInfoAndNotModifyTheSavedFlag() {
        when(updateTokenServiceRequestMock.getUpdateTokenRequest().getCardDetails()).thenReturn(cardDetailsMock);
        when(updateTokenServiceRequestMock.getUpdateTokenRequest().getPaymentTokenId()).thenReturn(PAYMENT_TOKEN_ID);
        when(savedCreditCardPaymentInfoMock.isSaved()).thenReturn(Boolean.TRUE);

        testObj.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock, saveCard);

        verify(savedCreditCardPaymentInfoMock).setValidToMonth(CARD_DETAILS_EXPIRY_MONTH);
        verify(savedCreditCardPaymentInfoMock).setValidToYear(CARD_DETAILS_EXPIRY_YEAR);
        verify(savedCreditCardPaymentInfoMock).setCcOwner(CARD_DETAILS_HOLDER_NAME);
        verify(savedCreditCardPaymentInfoMock, never()).setSaved(any(Boolean.class));
        verify(modelServiceMock).save(savedCreditCardPaymentInfoMock);
        verify(modelServiceMock, never()).save(paymentInfoMock);
    }

    @Test
    public void updateCreditCardPaymentInfo_WhenSavedFlagItWasAlreadySetAsTrue_ShouldUpdateCreditCardPaymentInfoAndModifyTheSavedFlag() {
        when(updateTokenServiceRequestMock.getUpdateTokenRequest().getCardDetails()).thenReturn(cardDetailsMock);
        when(updateTokenServiceRequestMock.getUpdateTokenRequest().getPaymentTokenId()).thenReturn(PAYMENT_TOKEN_ID);

        testObj.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock, Boolean.TRUE);

        verify(savedCreditCardPaymentInfoMock).setValidToMonth(CARD_DETAILS_EXPIRY_MONTH);
        verify(savedCreditCardPaymentInfoMock).setValidToYear(CARD_DETAILS_EXPIRY_YEAR);
        verify(savedCreditCardPaymentInfoMock).setCcOwner(CARD_DETAILS_HOLDER_NAME);
        verify(savedCreditCardPaymentInfoMock).setSaved(Boolean.TRUE);
        verify(modelServiceMock).save(savedCreditCardPaymentInfoMock);
        verify(modelServiceMock, never()).save(paymentInfoMock);
    }

    @Test
    public void removePaymentInfo_WhenCreatingNewOneFromNotification_WhenCreditCardPaymentInfoIsCreated_ShouldDeleteInitialPaymentInfo() {
        final PaymentInfoModel paymentInfo = createPaymentInfo();

        final CreditCardPaymentInfoModel ccPaymentInfo = new CreditCardPaymentInfoModel();
        ccPaymentInfo.setPaymentType(PAYMENT_TYPE);
        ccPaymentInfo.setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        ccPaymentInfo.setCode(PAYMENT_INFO_CODE);
        ccPaymentInfo.setNumber("444433******1111");

        when(userModelMock.getPaymentInfos()).thenReturn(ImmutableList.of(paymentInfo, ccPaymentInfo));

        testObj.removePaymentInfoWhenCreatingNewOneFromNotification(orderModelMock);

        verify(modelServiceMock).remove(paymentInfo);
        verify(modelServiceMock, never()).remove(ccPaymentInfo);
    }

    @Test
    public void removePaymentInfo_WhenCreatingNewOneFromNotification_WhenWorldpayPaymentInfoIsCreated_ShouldDeleteInitialPaymentInfo() {
        final PaymentInfoModel paymentInfo = createPaymentInfo();

        final WorldpayAPMPaymentInfoModel apmPaymentInfo = new WorldpayAPMPaymentInfoModel();
        apmPaymentInfo.setPaymentType(PAYMENT_TYPE);
        apmPaymentInfo.setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        apmPaymentInfo.setCode(PAYMENT_INFO_CODE);

        when(userModelMock.getPaymentInfos()).thenReturn(ImmutableList.of(paymentInfo, apmPaymentInfo));

        testObj.removePaymentInfoWhenCreatingNewOneFromNotification(orderModelMock);

        verify(modelServiceMock).remove(paymentInfo);
        verify(modelServiceMock, never()).remove(apmPaymentInfo);
    }

    @Test
    public void createPaymentInfoGooglePay_ShouldCreatePaymentInfoWithGooglePayInformationWhenNonExistingTokenisedAPMIsFound() {
        when(modelServiceMock.create(GooglePayPaymentInfoModel.class)).thenReturn(googlePayPaymentInfoModelMock);
        when(googlePayAdditionAuthInfoMock.getSignedMessage()).thenReturn(SIGNED_MESSAGE);
        when(googlePayAdditionAuthInfoMock.getSignature()).thenReturn(SIGNATURE);
        when(googlePayAdditionAuthInfoMock.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode())).thenReturn(worldpayAPMConfigurationModelMock);

        testObj.createPaymentInfoGooglePay(cartModelMock, googlePayAdditionAuthInfoMock, null, cardMock);

        verify(googlePayPaymentInfoModelMock).setSignedMessage(SIGNED_MESSAGE);
        verify(googlePayPaymentInfoModelMock).setSignature(SIGNATURE);
        verify(googlePayPaymentInfoModelMock).setProtocolVersion(PROTOCOL_VERSION);
        verify(googlePayPaymentInfoModelMock).setUser(cartModelMock.getUser());
        verify(googlePayPaymentInfoModelMock).setSaved(false);
        verify(googlePayPaymentInfoModelMock).setCode(startsWith(ORDER_CODE));
        verify(googlePayPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(cartModelMock).setPaymentInfo(googlePayPaymentInfoModelMock);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void PaymentInfoGooglePay_ShouldNotCreatePaymentInfoWithGooglePayInformationWhenExistingTokenisedAPMInfoExists() {
        when(googlePayAdditionAuthInfoMock.getSignedMessage()).thenReturn(SIGNED_MESSAGE);
        when(googlePayAdditionAuthInfoMock.getSignature()).thenReturn(SIGNATURE);
        when(googlePayAdditionAuthInfoMock.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode())).thenReturn(worldpayAPMConfigurationModelMock);
        when(savedAPMGooglePaymentInfoMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);
        when(savedAPMGooglePaymentInfoMock.isSaved()).thenReturn(true);

        final PaymentInfoModel result = testObj.createPaymentInfoGooglePay(cartModelMock, googlePayAdditionAuthInfoMock, PAYMENT_TOKEN_ID, cardMock);

        assertThat(result).isEqualTo(savedAPMGooglePaymentInfoMock);
        verify(modelServiceMock, times(0)).create(GooglePayPaymentInfoModel.class);
        verify(cartModelMock).setPaymentInfo(savedAPMGooglePaymentInfoMock);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void createPaymentInfoGooglePay_ShouldCreatePaymentInfoWithGooglePayInformationAndAddSubscriptionId__WhenTokenizationIsRequiredAndFoundMatchingTokenisedAPM() {
        when(modelServiceMock.create(GooglePayPaymentInfoModel.class)).thenReturn(googlePayPaymentInfoModelMock);
        when(googlePayAdditionAuthInfoMock.getSignedMessage()).thenReturn(SIGNED_MESSAGE);
        when(googlePayAdditionAuthInfoMock.getSignature()).thenReturn(SIGNATURE);
        when(googlePayAdditionAuthInfoMock.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode())).thenReturn(worldpayAPMConfigurationModelMock);
        when(googlePayAdditionAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);

        testObj.createPaymentInfoGooglePay(cartModelMock, googlePayAdditionAuthInfoMock, PAYMENT_TOKEN_ID, cardMock);

        final InOrder inOrder = inOrder(googlePayPaymentInfoModelMock, cartModelMock, modelServiceMock);
        inOrder.verify(googlePayPaymentInfoModelMock).setUser(cartModelMock.getUser());
        inOrder.verify(googlePayPaymentInfoModelMock).setCode(startsWith(ORDER_CODE));
        inOrder.verify(googlePayPaymentInfoModelMock).setProtocolVersion(PROTOCOL_VERSION);
        inOrder.verify(googlePayPaymentInfoModelMock).setSignature(SIGNATURE);
        inOrder.verify(googlePayPaymentInfoModelMock).setSignedMessage(SIGNED_MESSAGE);
        inOrder.verify(googlePayPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        inOrder.verify(googlePayPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        inOrder.verify(googlePayPaymentInfoModelMock).setObfuscatedCardNumber(CARD_NUMBER);
        inOrder.verify(googlePayPaymentInfoModelMock).setExpiryMonth(CARD_EXPIRY_MONTH);
        inOrder.verify(googlePayPaymentInfoModelMock).setExpiryYear(CARD_EXPIRY_YEAR);
        inOrder.verify(googlePayPaymentInfoModelMock).setSaved(true);
        inOrder.verify(cartModelMock).setPaymentInfo(googlePayPaymentInfoModelMock);
        inOrder.verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void createPaymentInfoGooglePay_ShouldCreatePaymentInfoWithGooglePayInformationWithoutCardDetails__WhenCardDetailsfromResponseIsNull() {
        when(modelServiceMock.create(GooglePayPaymentInfoModel.class)).thenReturn(googlePayPaymentInfoModelMock);
        when(googlePayAdditionAuthInfoMock.getSignedMessage()).thenReturn(SIGNED_MESSAGE);
        when(googlePayAdditionAuthInfoMock.getSignature()).thenReturn(SIGNATURE);
        when(googlePayAdditionAuthInfoMock.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode())).thenReturn(worldpayAPMConfigurationModelMock);
        when(googlePayAdditionAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);

        testObj.createPaymentInfoGooglePay(cartModelMock, googlePayAdditionAuthInfoMock, PAYMENT_TOKEN_ID, null);

        final InOrder inOrder = inOrder(googlePayPaymentInfoModelMock, cartModelMock, modelServiceMock);
        inOrder.verify(googlePayPaymentInfoModelMock).setUser(cartModelMock.getUser());
        inOrder.verify(googlePayPaymentInfoModelMock).setCode(startsWith(ORDER_CODE));
        inOrder.verify(googlePayPaymentInfoModelMock).setProtocolVersion(PROTOCOL_VERSION);
        inOrder.verify(googlePayPaymentInfoModelMock).setSignature(SIGNATURE);
        inOrder.verify(googlePayPaymentInfoModelMock).setSignedMessage(SIGNED_MESSAGE);
        inOrder.verify(googlePayPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        inOrder.verify(googlePayPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        inOrder.verify(googlePayPaymentInfoModelMock).setSaved(true);
        inOrder.verify(cartModelMock).setPaymentInfo(googlePayPaymentInfoModelMock);
        inOrder.verify(modelServiceMock).save(cartModelMock);

        verify(googlePayPaymentInfoModelMock, never()).setObfuscatedCardNumber(anyString());
        verify(googlePayPaymentInfoModelMock, never()).setExpiryYear(anyString());
        verify(googlePayPaymentInfoModelMock, never()).setExpiryMonth(anyString());
    }

    @Test
    public void createPaymentInfoGooglePay_ShouldCreatePaymentInfoWithGooglePayInformationWithoutAddingSubscriptionId_WhenFoundMatchingTokenisedAPM() {
        when(modelServiceMock.create(GooglePayPaymentInfoModel.class)).thenReturn(googlePayPaymentInfoModelMock);
        when(googlePayAdditionAuthInfoMock.getSignedMessage()).thenReturn(SIGNED_MESSAGE);
        when(googlePayAdditionAuthInfoMock.getSignature()).thenReturn(SIGNATURE);
        when(googlePayAdditionAuthInfoMock.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PaymentType.PAYWITHGOOGLESSL.getMethodCode())).thenReturn(worldpayAPMConfigurationModelMock);
        when(googlePayAdditionAuthInfoMock.getSaveCard()).thenReturn(Boolean.FALSE);

        testObj.createPaymentInfoGooglePay(cartModelMock, googlePayAdditionAuthInfoMock, PAYMENT_TOKEN_ID, cardMock);

        final InOrder inOrder = inOrder(googlePayPaymentInfoModelMock, cartModelMock, modelServiceMock);
        inOrder.verify(googlePayPaymentInfoModelMock).setUser(cartModelMock.getUser());
        inOrder.verify(googlePayPaymentInfoModelMock).setCode(startsWith(ORDER_CODE));
        inOrder.verify(googlePayPaymentInfoModelMock).setProtocolVersion(PROTOCOL_VERSION);
        inOrder.verify(googlePayPaymentInfoModelMock).setSignature(SIGNATURE);
        inOrder.verify(googlePayPaymentInfoModelMock).setSignedMessage(SIGNED_MESSAGE);
        inOrder.verify(googlePayPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        inOrder.verify(googlePayPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        inOrder.verify(googlePayPaymentInfoModelMock).setObfuscatedCardNumber(CARD_NUMBER);
        inOrder.verify(googlePayPaymentInfoModelMock).setSaved(false);
        inOrder.verify(cartModelMock).setPaymentInfo(googlePayPaymentInfoModelMock);
        inOrder.verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void createPaymentInfoApplePay_ShouldCreateApplePayPaymentInfoAndSetItAsPaymentInfoOnTheCart() {
        when(modelServiceMock.create(ApplePayPaymentInfoModel.class)).thenReturn(applePayPaymentInfoModelMock);

        final ApplePayPaymentInfoModel result = (ApplePayPaymentInfoModel) testObj.createPaymentInfoApplePay(cartModelMock, applePayAdditionalAuthInfoMock);

        assertThat(result).isEqualTo(applePayPaymentInfoModelMock);
        verify(applePayPaymentInfoModelMock).setUser(cartModelMock.getUser());
        verify(applePayPaymentInfoModelMock).setSaved(false);
        verify(applePayPaymentInfoModelMock).setCode(startsWith(ORDER_CODE));
        verify(applePayPaymentInfoModelMock).setTransactionId(TRANSACTION_ID);
        verify(applePayPaymentInfoModelMock).setVersion(VERSION);
        verify(cartModelMock).setPaymentInfo(applePayPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(cartModelMock, applePayPaymentInfoModelMock);
    }

    @Test
    public void setTransactionIdentifierPaymentInfo_WhenItIsCalled_ShouldSetTransactionIdentifierOnPaymentInfo() {
        testObj.setTransactionIdentifierOnPaymentInfo(paymentInfoMock, TRANSACTION_IDENTIFIER);

        verify(paymentInfoMock).setTransactionIdentifier(TRANSACTION_IDENTIFIER);
    }

    @Test
    public void createPaymentInfoModelOnCart_WhenThereIsPaymentTransactionAndPaymentMethodIsGoingToBeSaved_ShouldSavePaymentInfoWithSavedAttributeSetToTrueModelOnPaymentTransactionAndCart_WhenThereIsPaymentTransactionAndPaymentMethodIsGoingToBeSaved() {
        testObj.createPaymentInfoModelOnCart(cartModelMock, true);

        verifiesOn_CreatePaymentInfoModelOnCart(true);
    }

    @Test
    public void createPaymentInfoModelOnCart_WhenThereIsPaymentTransactionAndPaymentMethodIsNotGoingToBeSaved_ShouldSavePaymentInfoWithSavedAttributeSetToFalseModelOnPaymentTransactionAndCart() {
        testObj.createPaymentInfoModelOnCart(cartModelMock, true);

        verifiesOn_CreatePaymentInfoModelOnCart(false);
    }

    @Test
    public void createPaymentInfoModelOnCart_WhenThereIsNoPaymentTransactionAndPaymentMethodIsGoingToBeSaved_ShouldSavePaymentInfoWithSavedAttributeSetToTrueModelOnCartOnly() {
        when(cartModelMock.getPaymentTransactions()).thenReturn(Collections.emptyList());

        testObj.createPaymentInfoModelOnCart(cartModelMock, true);

        verify(modelServiceMock).create(PaymentInfoModel.class);
        verify(orderPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(orderPaymentInfoModelMock).setUser(userModelMock);
        verify(orderPaymentInfoModelMock).setSaved(true);
        verify(orderPaymentInfoModelMock).setCode(anyString());
        verify(paymentTransactionModelMock, never()).setInfo(orderPaymentInfoModelMock);
        verify(cartModelMock).setPaymentInfo(orderPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderPaymentInfoModelMock, cartModelMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cloneAndSetBillingAddressFromCart_WhenPaymentAddressIsNull_ShouldThrowException() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);

        testObj.cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoMock);
    }

    @Test
    public void cloneAndSetBillingAddressFromCart_WhenPaymentAddressIsNotNull_ShouldCloneAndSetToPaymentInfo() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(addressServiceMock.cloneAddressForOwner(paymentAddressModelMock, paymentInfoMock)).thenReturn(clonedAddressMock);

        testObj.cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoMock);

        verify(clonedAddressMock).setBillingAddress(true);
        verify(clonedAddressMock).setShippingAddress(false);
        verify(clonedAddressMock).setOwner(paymentInfoMock);
        verify(paymentInfoMock).setBillingAddress(clonedAddressMock);
    }

    private void verifyCardNotCreated() {
        final InOrder inOrder = inOrder(testObj, orderModelMock, paymentTransactionModelMock, modelServiceMock);
        inOrder.verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        inOrder.verify(orderModelMock).setPaymentInfo(savedCreditCardPaymentInfoMock);
        inOrder.verify(paymentTransactionModelMock).setInfo(savedCreditCardPaymentInfoMock);
        inOrder.verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
        inOrder.verify(modelServiceMock).save(savedCreditCardPaymentInfoMock);
    }

    private void verifiesOn_CreatePaymentInfoModelOnCart(final boolean isSaved) {
        verify(modelServiceMock).create(PaymentInfoModel.class);
        verify(orderPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(orderPaymentInfoModelMock).setUser(userModelMock);
        verify(orderPaymentInfoModelMock).setSaved(isSaved);
        verify(orderPaymentInfoModelMock).setCode(anyString());
        verify(paymentTransactionModelMock).setInfo(orderPaymentInfoModelMock);
        verify(cartModelMock).setPaymentInfo(orderPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderPaymentInfoModelMock, cartModelMock);
    }

    @Test
    public void setPaymentInfoOnCart_WhenCreditCardInfoIsPresent_ShouldSetPaymentInfoOnCart() {
        testObj.setPaymentInfoOnCart(cartModelMock, creditCardPaymentInfoModelMock);

        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterArgumentCaptor.capture());

        final CommerceCheckoutParameter checkoutParameter = commerceCheckoutParameterArgumentCaptor.getValue();
        assertThat(checkoutParameter.getCart()).isEqualTo(cartModelMock);
        assertThat(checkoutParameter.getPaymentInfo()).isEqualTo(creditCardPaymentInfoModelMock);
    }

    @Test
    public void setPaymentInfoOnCart_WhenCreditCardInfoIsNull_ShouldDoNothing() {
        testObj.setPaymentInfoOnCart(cartModelMock, null);

        verifyZeroInteractions(commerceCheckoutServiceMock);
    }

    private PaymentInfoModel createPaymentInfo() {
        final PaymentInfoModel paymentInfo = new PaymentInfoModel();
        paymentInfo.setPaymentType(PAYMENT_TYPE);
        paymentInfo.setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        paymentInfo.setCode(PAYMENT_INFO_CODE);
        return paymentInfo;
    }
}
