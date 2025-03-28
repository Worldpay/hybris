package com.worldpay.service.response.transform.impl;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.JournalReply;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.WebformRefundReply;
import com.worldpay.data.token.DeleteTokenReply;
import com.worldpay.data.token.TokenReply;
import com.worldpay.data.token.UpdateTokenReply;
import com.worldpay.enums.DebitCreditIndicator;
import com.worldpay.factories.CardBrandFactory;
import com.worldpay.internal.model.Error;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultServiceResponseTransformerHelperTest {

    public static final String REASON_CODE_1 = "Card Unfamiliarity";
    public static final String REASON_CODE_2 = "High Risk Email";
    private static final String RESPONSE_CODE = "responseCode";
    private static final String ACTION_CODE = "action code";
    private static final String TRANSACTION_ID = "transaction id";
    private static final String SCHEMA_NAME = "schema name";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String LAST_EVENT = "AUTHORISED";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String ISO8583_RETURN_CODE_CODE = "12";
    private static final String ISO8583_RETURN_CODE_DESCRIPTION = "iso8583ReturnCode-description";
    private static final String BALANCE_ACCOUNT_TYPE = "balanceAccountType";
    private static final String CVC_RESULT_CODE_DESCRIPTION = "cvcResultCodeDescription";
    private static final String AMOUNT_VALUE = "amountValue";
    private static final String AMOUNT_CURRENCY_CODE = "amountCurrencyCode";
    private static final String AMOUNT_EXPONENT = "amountExponent";
    private static final String AMOUNT_DEBIT_CREDIT_INDICATOR = "debit";
    private static final String BALANCE_AMOUNT_VALUE = "balanceAmountValue";
    private static final String BALANCE_AMOUNT_CURRENCY_CODE = "balanceAmountCurrencyCode";
    private static final String BALANCE_AMOUNT_EXPONENT = "balanceAmountExponent";
    private static final String BALANCE_AMOUNT_DEBIT_CREDIT_INDICATOR = "debit";
    private static final String AAV_ADDRESS_RESULT_CODE_DESCRIPTION = "aavAddressResultCodeDescription";
    private static final String AAV_CARDHOLDER_NAME_RESULT_CODE_DESCRIPTION = "aavCardholderNameResultCodeDescription";
    private static final String AAV_EMAIL_RESULT_CODE_DESCRIPTION = "aavEmailResultCodeDescription";
    private static final String AAV_POSTCODE_RESULT_CODE_DESCRIPTION = "aavPostcodeResultCodeDescription";
    private static final String AAV_TELEPHONE_RESULT_CODE_DESCRIPTION = "aavTelephoneResultCodeDescription";
    private static final String AUTHENTICATED_SHOPPER_ID = "authShopper";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String CARD_SUB_BRAND = "cardSubBrand";
    private static final String ISSUER_COUNTRY_CODE = "issuerCode";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String CITY = "city";
    private static final String CVC = "123";
    private static final String EXPIRY_YEAR = "2015";
    private static final String EXPIRY_MONTH = "02";
    private static final String DAY_OF_MONTH = "5";
    private static final String HOUR = "09";
    private static final String MINUTE = "35";
    private static final String SECOND = "17";
    private static final String REPORTING_EXPIRY_YEAR = "2015";
    private static final String REPORTING_EXPIRY_MONTH = "02";
    private static final String REPORTING_DAY_OF_MONTH = "5";
    private static final String REPORTING_HOUR = "09";
    private static final String REPORTING_MINUTE = "35";
    private static final String REPORTING_SECOND = "17";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String PAYMENT_TOKEN_ID = "tokenId";
    private static final String REPORTING_TOKEN = "reportingToken";
    private static final String TOKEN_EVENT = "tokenEvent";
    private static final String COUNTRY_CODE = "UK";
    private static final String LAST_NAME = "lastName";
    private static final String POSTAL_CODE = "postalCode";
    private static final String STATE = "state";
    private static final String TELEPHONE_NUMBER = "telephoneNumber";
    private static final String STREET = "street";
    private static final String HOUSE_NAME = "houseName";
    private static final String HOUSE_NUMBER = "houseNumber";
    private static final String HOUSE_NUMBER_EXTENSION = "houseNumberExtension";
    private static final String ADDRESS_1 = "address1";
    private static final String ADDRESS_2 = "address2";
    private static final String ADDRESS_3 = "address3";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REFUND_REFERENCE = "refundReference";
    private static final String REFUND_ID = "RefundId";
    private static final String WEBFORM_URL = "WebformURL";
    private static final String WEBFORM_ID = "WebformId";
    private static final String WEBFORM_STATUS = "WebformStatus";
    private static final String PAYMENT_ID = "PaymentId";
    private static final String REASON = "Reason";
    private static final String AUTHORISATION_ID_BY = "AuthorisationIdBy";
    private static final String AUTHORISATION_ID = "authorisationId";
    private static final String RISK_SCORE_VALUE = "riskScoreValue";
    private static final String BIN = "bin";
    private static final String PERSONAL_ACCOUNT = "personal";
    private static final String BUSSINESS_ACCOUNT = "BUSSINESS ACCOUNT";
    private static final String BATCH_1 = "batch 1";
    private static final String BATCH_2 = "batch 2";
    private static final String TOKEN_ID = "token_id";
    private static final String FRAUD_SIGHT_ID = "188a9ae6-21c4-4fd9-87cd-8df4c719aaf1";
    private static final String SCORE = "0.5";
    private static final String LOW_RISK_LEVEL = "low-risk";

    @InjectMocks
    private DefaultServiceResponseTransformerHelper testObj;

    @Mock
    private Converter<com.worldpay.internal.model.Amount, com.worldpay.data.Amount> internalAmountReverseConverterMock;
    @Mock
    private Converter<com.worldpay.internal.model.Date, com.worldpay.data.Date> internalDateReverseConverterMock;
    @Mock
    private Converter<ExemptionResponse, ExemptionResponseInfo> exemptionResponseReverseConverterMock;
    @Mock
    private CardBrandFactory cardBrandFactoryMock;

    private Amount amount;
    private CVCResultCode cvcResultCode;
    private Balance balance;
    private Amount balanceAmount;
    private ISO8583ReturnCode iso8583ReturnCode;
    private RiskScore riskScore;
    private CardHolderName cardHolderName;
    private AAVAddressResultCode aavAddressResultCode;
    private AAVCardholderNameResultCode aavCardholderNameResultCode;
    private AAVEmailResultCode aavEmailResultCode;
    private AAVPostcodeResultCode aavPostcodeResultCode;
    private AAVTelephoneResultCode aavTelephoneResultCode;
    private AuthorisationId authorisationId;
    private SchemeResponse schemeResponse;
    private FraudSight fraudSightResponse;
    private CardBrand cardBrand;

    @Mock
    private UpdateTokenReceived updateTokenReceivedMock;
    @Mock
    private Journal journalMock;
    @Mock
    private BookingDate bookingDateMock;
    @Mock
    private AccountTx accountTx1Mock, accountTx2Mock;
    @Mock
    private DeleteTokenReceived deletedTokenReceivedMock;
    @Mock
    private ReasonCodes reasonCodesMock;
    @Mock
    private Amount internalAmountMock;
    @Mock
    private com.worldpay.data.Amount amountMock, balanceAmountMock;
    @Mock
    private Date internalDateMock;
    @Mock
    private com.worldpay.data.Date dateMock, reportingDateMock;
    @Mock
    private ExemptionResponse exemptionResponseMock;

    @Before
    public void setUp() {
        testObj = new DefaultServiceResponseTransformerHelper(internalAmountReverseConverterMock, internalDateReverseConverterMock,
            exemptionResponseReverseConverterMock, cardBrandFactoryMock);

        cardHolderName = new CardHolderName();
        cardHolderName.setvalue(CARD_HOLDER_NAME);
        cardBrand = new CardBrand();

        when(amountMock.getValue()).thenReturn(AMOUNT_VALUE);
        when(amountMock.getCurrencyCode()).thenReturn(AMOUNT_CURRENCY_CODE);
        when(amountMock.getDebitCreditIndicator()).thenReturn(DebitCreditIndicator.DEBIT);
        when(amountMock.getExponent()).thenReturn(AMOUNT_EXPONENT);
        when(balanceAmountMock.getValue()).thenReturn(BALANCE_AMOUNT_VALUE);
        when(balanceAmountMock.getCurrencyCode()).thenReturn(BALANCE_AMOUNT_CURRENCY_CODE);
        when(balanceAmountMock.getDebitCreditIndicator()).thenReturn(DebitCreditIndicator.DEBIT);
        when(balanceAmountMock.getExponent()).thenReturn(BALANCE_AMOUNT_EXPONENT);
        when(dateMock.getYear()).thenReturn(EXPIRY_YEAR);
        when(dateMock.getMonth()).thenReturn(EXPIRY_MONTH);
        when(dateMock.getDayOfMonth()).thenReturn(DAY_OF_MONTH);
        when(dateMock.getHour()).thenReturn(HOUR);
        when(dateMock.getMinute()).thenReturn(MINUTE);
        when(dateMock.getSecond()).thenReturn(SECOND);
        when(journalMock.getJournalType()).thenReturn(AUTHORISED.name());
        when(journalMock.getBookingDate()).thenReturn(bookingDateMock);
        when(bookingDateMock.getDate()).thenReturn(createDate());
        when(journalMock.getAccountTx()).thenReturn(List.of(accountTx1Mock, accountTx2Mock));
        when(accountTx1Mock.getAccountType()).thenReturn(PERSONAL_ACCOUNT);
        when(accountTx1Mock.getAmount()).thenReturn(amount);
        when(accountTx1Mock.getBatchId()).thenReturn(BATCH_1);
        when(accountTx2Mock.getAccountType()).thenReturn(BUSSINESS_ACCOUNT);
        when(accountTx2Mock.getBatchId()).thenReturn(BATCH_2);
        when(deletedTokenReceivedMock.getPaymentTokenID()).thenReturn(TOKEN_ID);
        lenient().when(internalAmountReverseConverterMock.convert(internalAmountMock)).thenReturn(amountMock);
        lenient().when(internalAmountReverseConverterMock.convert(internalAmountMock)).thenReturn(balanceAmountMock);
        lenient().when(internalDateReverseConverterMock.convert(internalDateMock)).thenReturn(dateMock);
        lenient().when(internalDateReverseConverterMock.convert(internalDateMock)).thenReturn(reportingDateMock);
    }

    @Test
    public void testBuildPaymentReply() {
        final Payment payment = new Payment();
        final PaymentMethodDetail paymentMethodDetail = new PaymentMethodDetail();
        paymentMethodDetail.setCard(createCard());
        createAmount();
        createCvcResultCode();
        createBalance();
        createIso8583ReturnCode();
        createRiskScore();
        createFraudSight();
        createAavAddressResultCode();
        createAavCardholderNameResultCode();
        createAavEmailResultCode();
        createAavPostcodeResultCode();
        createAavTelephoneResultCode();
        createAuthorisationId();
        createScheme();

        payment.setPaymentMethod(PAYMENT_METHOD);
        payment.setPaymentMethodDetail(paymentMethodDetail);
        payment.setAmount(amount);
        payment.getBalance().add(balance);
        payment.setLastEvent(LAST_EVENT);
        payment.setCVCResultCode(cvcResultCode);
        payment.getBalance().add(balance);
        payment.setISO8583ReturnCode(iso8583ReturnCode);
        payment.setRiskScore(riskScore);
        payment.setFraudSight(fraudSightResponse);
        payment.setCardHolderName(cardHolderName);
        payment.setAAVAddressResultCode(aavAddressResultCode);
        payment.setAAVCardholderNameResultCode(aavCardholderNameResultCode);
        payment.setAAVEmailResultCode(aavEmailResultCode);
        payment.setAAVPostcodeResultCode(aavPostcodeResultCode);
        payment.setAAVTelephoneResultCode(aavTelephoneResultCode);
        payment.setRefundReference(REFUND_REFERENCE);
        payment.setAuthorisationId(authorisationId);
        payment.setSchemeResponse(schemeResponse);

        when(internalDateReverseConverterMock.convert(payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate())).thenReturn(dateMock);
        when(internalAmountReverseConverterMock.convert(payment.getBalance().get(0).getAmount())).thenReturn(balanceAmountMock);
        when(internalAmountReverseConverterMock.convert(payment.getAmount())).thenReturn(amountMock);

        final PaymentReply paymentReply = testObj.buildPaymentReply(payment);

        assertEquals(PAYMENT_METHOD, paymentReply.getPaymentMethodCode());
        assertEquals(EXPIRY_MONTH, paymentReply.getCardDetails().getExpiryDate().getMonth());
        assertEquals(EXPIRY_YEAR, paymentReply.getCardDetails().getExpiryDate().getYear());
        assertEquals(CARD_HOLDER_NAME, paymentReply.getCardDetails().getCardHolderName());
        assertEquals(CARD_NUMBER, paymentReply.getCardDetails().getCardNumber());
        assertEquals(PaymentType.VISA.getMethodCode(), paymentReply.getCardDetails().getPaymentType());
        assertEquals(AUTHORISED, paymentReply.getAuthStatus());
        assertEquals(CVC_RESULT_CODE_DESCRIPTION, paymentReply.getCvcResultDescription());
        assertEquals(ISO8583_RETURN_CODE_CODE, paymentReply.getReturnCode());
        assertThat(paymentReply.getRiskScore().getValue()).isEqualTo(RISK_SCORE_VALUE);
        assertEquals(AAV_ADDRESS_RESULT_CODE_DESCRIPTION, paymentReply.getAavAddressResultCode());
        assertEquals(AAV_CARDHOLDER_NAME_RESULT_CODE_DESCRIPTION, paymentReply.getAavCardholderNameResultCode());
        assertEquals(AAV_EMAIL_RESULT_CODE_DESCRIPTION, paymentReply.getAavEmailResultCode());
        assertEquals(AAV_POSTCODE_RESULT_CODE_DESCRIPTION, paymentReply.getAavPostcodeResultCode());
        assertEquals(AAV_TELEPHONE_RESULT_CODE_DESCRIPTION, paymentReply.getAavTelephoneResultCode());

        assertEquals(BALANCE_AMOUNT_VALUE, paymentReply.getBalanceAmount().getValue());
        assertEquals(BALANCE_AMOUNT_CURRENCY_CODE, paymentReply.getBalanceAmount().getCurrencyCode());
        assertEquals(BALANCE_AMOUNT_EXPONENT, paymentReply.getBalanceAmount().getExponent());
        assertEquals(BALANCE_AMOUNT_DEBIT_CREDIT_INDICATOR, paymentReply.getBalanceAmount().getDebitCreditIndicator().getCode());

        assertEquals(AMOUNT_VALUE, paymentReply.getAmount().getValue());
        assertEquals(AMOUNT_CURRENCY_CODE, paymentReply.getAmount().getCurrencyCode());
        assertEquals(AMOUNT_EXPONENT, paymentReply.getAmount().getExponent());
        assertEquals(AMOUNT_DEBIT_CREDIT_INDICATOR, paymentReply.getAmount().getDebitCreditIndicator().getCode());
        assertEquals(AUTHORISATION_ID, paymentReply.getAuthorisationId());
        assertEquals(AUTHORISATION_ID_BY, paymentReply.getAuthorisedBy());
        assertEquals(REFUND_REFERENCE, paymentReply.getRefundReference());
        assertEquals(ACTION_CODE, paymentReply.getSchemeResponse().getActionCode());
        assertEquals(RESPONSE_CODE, paymentReply.getSchemeResponse().getResponseCode());
        assertEquals(SCHEMA_NAME, paymentReply.getSchemeResponse().getSchemeName());
        assertEquals(TRANSACTION_ID, paymentReply.getSchemeResponse().getTransactionIdentifier());

        assertThat(paymentReply.getFraudSight().getId()).isEqualTo(FRAUD_SIGHT_ID);
        assertThat(paymentReply.getFraudSight().getScore()).isEqualTo(0.5);
        assertThat(paymentReply.getFraudSight().getMessage()).isEqualTo(LOW_RISK_LEVEL);
        assertThat(paymentReply.getFraudSight().getReasonCodes()).hasSize(2);
        assertThat(paymentReply.getFraudSight().getReasonCodes().get(0)).isEqualTo(REASON_CODE_1);
        assertThat(paymentReply.getFraudSight().getReasonCodes().get(1)).isEqualTo(REASON_CODE_2);
    }

    private void createFraudSight() {
        fraudSightResponse = new FraudSight();
        fraudSightResponse.setId(FRAUD_SIGHT_ID);
        fraudSightResponse.setScore(SCORE);
        fraudSightResponse.setMessage(LOW_RISK_LEVEL);
        final ReasonCode reasonCode1 = new ReasonCode();
        reasonCode1.setvalue(REASON_CODE_1);
        final ReasonCode reasonCode2 = new ReasonCode();
        reasonCode2.setvalue(REASON_CODE_2);
        when(reasonCodesMock.getReasonCode()).thenReturn(Arrays.asList(reasonCode1, reasonCode2));
        fraudSightResponse.setReasonCodes(reasonCodesMock);
    }

    private void createScheme() {
        schemeResponse = new SchemeResponse();
        ResponseCode responseCode = new ResponseCode();
        responseCode.setvalue(RESPONSE_CODE);
        ActionCode actionCode = new ActionCode();
        actionCode.setvalue(ACTION_CODE);
        schemeResponse.setResponseCode(responseCode);
        schemeResponse.setActionCode(actionCode);
        schemeResponse.setTransactionIdentifier(TRANSACTION_ID);
        schemeResponse.setSchemeName(SCHEMA_NAME);
    }

    @Test
    public void buildPaymentReply_WhenCardHolderNameIsNull_ShouldSetItAsNullInPaymentReply() {
        cardHolderName = null;
        final Payment payment = new Payment();
        final PaymentMethodDetail paymentMethodDetail = new PaymentMethodDetail();
        paymentMethodDetail.setCard(createCard());
        createAmount();
        createCvcResultCode();
        createBalance();
        createIso8583ReturnCode();
        createRiskScore();
        createAavAddressResultCode();
        createAavCardholderNameResultCode();
        createAavEmailResultCode();
        createAavPostcodeResultCode();
        createAavTelephoneResultCode();
        createAuthorisationId();

        payment.setPaymentMethod(PAYMENT_METHOD);
        payment.setPaymentMethodDetail(paymentMethodDetail);
        payment.setAmount(amount);
        payment.getBalance().add(balance);
        payment.setLastEvent(LAST_EVENT);
        payment.setCVCResultCode(cvcResultCode);
        payment.getBalance().add(balance);
        payment.setISO8583ReturnCode(iso8583ReturnCode);
        payment.setRiskScore(riskScore);
        payment.setCardHolderName(cardHolderName);
        payment.setAAVAddressResultCode(aavAddressResultCode);
        payment.setAAVCardholderNameResultCode(aavCardholderNameResultCode);
        payment.setAAVEmailResultCode(aavEmailResultCode);
        payment.setAAVPostcodeResultCode(aavPostcodeResultCode);
        payment.setAAVTelephoneResultCode(aavTelephoneResultCode);
        payment.setRefundReference(REFUND_REFERENCE);
        payment.setAuthorisationId(authorisationId);

        final PaymentReply paymentReply = testObj.buildPaymentReply(payment);

        assertNull(paymentReply.getCardDetails().getCardHolderName());
    }

    @Test
    public void buildPaymentReplyWithoutRiskScoreShouldNotSetRiskScoreOnPaymentReply() {
        final Payment payment = new Payment();
        final PaymentMethodDetail paymentMethodDetail = new PaymentMethodDetail();
        paymentMethodDetail.setCard(createCard());
        createAmount();
        createCvcResultCode();
        createBalance();
        createIso8583ReturnCode();
        createAavAddressResultCode();
        createAavCardholderNameResultCode();
        createAavEmailResultCode();
        createAavPostcodeResultCode();
        createAavTelephoneResultCode();
        createAuthorisationId();

        payment.setPaymentMethod(PAYMENT_METHOD);
        payment.setPaymentMethodDetail(paymentMethodDetail);
        payment.setAmount(amount);
        payment.getBalance().add(balance);
        payment.setLastEvent(LAST_EVENT);
        payment.setCVCResultCode(cvcResultCode);
        payment.getBalance().add(balance);
        payment.setISO8583ReturnCode(iso8583ReturnCode);
        payment.setRiskScore(riskScore);
        payment.setCardHolderName(cardHolderName);
        payment.setAAVAddressResultCode(aavAddressResultCode);
        payment.setAAVCardholderNameResultCode(aavCardholderNameResultCode);
        payment.setAAVEmailResultCode(aavEmailResultCode);
        payment.setAAVPostcodeResultCode(aavPostcodeResultCode);
        payment.setAAVTelephoneResultCode(aavTelephoneResultCode);
        payment.setRefundReference(REFUND_REFERENCE);
        payment.setAuthorisationId(authorisationId);

        when(internalDateReverseConverterMock.convert(payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate())).thenReturn(dateMock);
        when(internalAmountReverseConverterMock.convert(payment.getBalance().get(0).getAmount())).thenReturn(balanceAmountMock);
        when(internalAmountReverseConverterMock.convert(payment.getAmount())).thenReturn(amountMock);

        final PaymentReply paymentReply = testObj.buildPaymentReply(payment);

        assertEquals(PAYMENT_METHOD, paymentReply.getPaymentMethodCode());
        assertEquals(EXPIRY_MONTH, paymentReply.getCardDetails().getExpiryDate().getMonth());
        assertEquals(EXPIRY_YEAR, paymentReply.getCardDetails().getExpiryDate().getYear());
        assertEquals(CARD_HOLDER_NAME, paymentReply.getCardDetails().getCardHolderName());
        assertEquals(CARD_NUMBER, paymentReply.getCardDetails().getCardNumber());
        assertEquals(PaymentType.VISA.getMethodCode(), paymentReply.getCardDetails().getPaymentType());
        assertEquals(AUTHORISED, paymentReply.getAuthStatus());
        assertEquals(CVC_RESULT_CODE_DESCRIPTION, paymentReply.getCvcResultDescription());
        assertEquals(ISO8583_RETURN_CODE_CODE, paymentReply.getReturnCode());
        assertThat(paymentReply.getRiskScore()).isNull();
        assertEquals(AAV_ADDRESS_RESULT_CODE_DESCRIPTION, paymentReply.getAavAddressResultCode());
        assertEquals(AAV_CARDHOLDER_NAME_RESULT_CODE_DESCRIPTION, paymentReply.getAavCardholderNameResultCode());
        assertEquals(AAV_EMAIL_RESULT_CODE_DESCRIPTION, paymentReply.getAavEmailResultCode());
        assertEquals(AAV_POSTCODE_RESULT_CODE_DESCRIPTION, paymentReply.getAavPostcodeResultCode());
        assertEquals(AAV_TELEPHONE_RESULT_CODE_DESCRIPTION, paymentReply.getAavTelephoneResultCode());

        assertEquals(BALANCE_AMOUNT_VALUE, paymentReply.getBalanceAmount().getValue());
        assertEquals(BALANCE_AMOUNT_CURRENCY_CODE, paymentReply.getBalanceAmount().getCurrencyCode());
        assertEquals(BALANCE_AMOUNT_EXPONENT, paymentReply.getBalanceAmount().getExponent());
        assertEquals(BALANCE_AMOUNT_DEBIT_CREDIT_INDICATOR, paymentReply.getBalanceAmount().getDebitCreditIndicator().getCode());

        assertEquals(AMOUNT_VALUE, paymentReply.getAmount().getValue());
        assertEquals(AMOUNT_CURRENCY_CODE, paymentReply.getAmount().getCurrencyCode());
        assertEquals(AMOUNT_EXPONENT, paymentReply.getAmount().getExponent());
        assertEquals(AMOUNT_DEBIT_CREDIT_INDICATOR, paymentReply.getAmount().getDebitCreditIndicator().getCode());
        assertEquals(AUTHORISATION_ID, paymentReply.getAuthorisationId());
        assertEquals(AUTHORISATION_ID_BY, paymentReply.getAuthorisedBy());
        assertEquals(REFUND_REFERENCE, paymentReply.getRefundReference());
    }

    private Card createCard() {
        Card intCard = new Card();
        intCard.setType(PaymentType.VISA.getMethodCode());
        intCard.setNumber(CARD_NUMBER);
        intCard.setExpiryDate(createExpiryDate());
        return intCard;
    }

    @Test
    public void shouldBuildTokenReply() {
        final Token token = new Token();
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setvalue(AUTHENTICATED_SHOPPER_ID);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(TOKEN_REASON);
        tokenElements.add(tokenReason);

        final TokenDetails tokenDetail = new TokenDetails();
        tokenDetail.setTokenReason(tokenReason);
        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        tokenExpiry.setDate(createDate());
        tokenDetail.setPaymentTokenExpiry(tokenExpiry);
        final PaymentTokenID paymentTokenID = new PaymentTokenID();
        paymentTokenID.setvalue(PAYMENT_TOKEN_ID);
        tokenDetail.setPaymentTokenID(paymentTokenID);
        tokenDetail.setReportingTokenID(REPORTING_TOKEN);
        tokenDetail.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetail.setTokenEvent(TOKEN_EVENT);
        final ReportingTokenExpiry reportingTokenExpiry = new ReportingTokenExpiry();
        reportingTokenExpiry.setDate(createReportingTokenExpiryDate());
        tokenDetail.setReportingTokenExpiry(reportingTokenExpiry);
        tokenElements.add(tokenDetail);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("VISA", null));
        tokenElements.add(paymentInstrument);

        final Date date = ((CardDetails) paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().get(0)).getExpiryDate().getDate();
        when(internalDateReverseConverterMock.convert(date)).thenReturn(dateMock);

        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_MESSAGE);
        tokenElements.add(error);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertEquals(AUTHENTICATED_SHOPPER_ID, tokenReply.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, tokenReply.getTokenEventReference());

        assertEquals(TOKEN_REASON, tokenReply.getTokenReason());

        assertEquals(CARD_NUMBER, tokenReply.getPaymentInstrument().getCardNumber());
        assertEquals(cardHolderName.getvalue(), tokenReply.getPaymentInstrument().getCardHolderName());
        assertEquals(CVC, tokenReply.getPaymentInstrument().getCvc());
        assertEquals(CITY, tokenReply.getPaymentInstrument().getCardAddress().getCity());
        assertEquals(COUNTRY_CODE, tokenReply.getPaymentInstrument().getCardAddress().getCountryCode());
        assertEquals(POSTAL_CODE, tokenReply.getPaymentInstrument().getCardAddress().getPostalCode());
        assertEquals(LAST_NAME, tokenReply.getPaymentInstrument().getCardAddress().getLastName());
        assertEquals(CARD_HOLDER_NAME, tokenReply.getPaymentInstrument().getCardAddress().getFirstName());
        assertEquals(STATE, tokenReply.getPaymentInstrument().getCardAddress().getState());
        assertEquals(TELEPHONE_NUMBER, tokenReply.getPaymentInstrument().getCardAddress().getTelephoneNumber());
        assertEquals(STREET, tokenReply.getPaymentInstrument().getCardAddress().getStreet());
        assertEquals(HOUSE_NAME, tokenReply.getPaymentInstrument().getCardAddress().getHouseName());
        assertEquals(HOUSE_NUMBER, tokenReply.getPaymentInstrument().getCardAddress().getHouseNumber());
        assertEquals(HOUSE_NUMBER_EXTENSION, tokenReply.getPaymentInstrument().getCardAddress().getHouseNumberExtension());
        assertEquals(ADDRESS_1, tokenReply.getPaymentInstrument().getCardAddress().getAddress1());
        assertEquals(ADDRESS_2, tokenReply.getPaymentInstrument().getCardAddress().getAddress2());
        assertEquals(ADDRESS_3, tokenReply.getPaymentInstrument().getCardAddress().getAddress3());

        assertEquals(BIN, tokenReply.getPaymentInstrument().getBin());

        assertEquals(EXPIRY_YEAR, tokenReply.getPaymentInstrument().getExpiryDate().getYear());
        assertEquals(EXPIRY_MONTH, tokenReply.getPaymentInstrument().getExpiryDate().getMonth());
        assertEquals(DAY_OF_MONTH, tokenReply.getPaymentInstrument().getExpiryDate().getDayOfMonth());
        assertEquals(HOUR, tokenReply.getPaymentInstrument().getExpiryDate().getHour());
        assertEquals(MINUTE, tokenReply.getPaymentInstrument().getExpiryDate().getMinute());
        assertEquals(SECOND, tokenReply.getPaymentInstrument().getExpiryDate().getSecond());

        assertEquals(ERROR_CODE, tokenReply.getError().getCode());
        assertEquals(ERROR_MESSAGE, tokenReply.getError().getMessage());
    }

    @Test
    public void shouldBuildTokenReplyWithCartebleueVisa() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.CARTE_BLEUE.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("VISA", "CARTEBLEUE"));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARTE_BLEUE.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithEFTPOSVisa() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.EFTPOS_AU.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("VISA", "EFTPOS_AU"));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.EFTPOS_AU.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithEFTPOSMastercard() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.EFTPOS_AU.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("ECMC", "EFTPOS_AU"));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.EFTPOS_AU.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithCarteBancaireMasterCard() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.CARTE_BANCAIRE.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("ECMC", "CB"));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARTE_BANCAIRE.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithVisa() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.VISA.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("VISA", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.VISA.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithAirplus() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.AIRPLUS.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("AIRPLUS", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.AIRPLUS.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithAMEX() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.AMERICAN_EXPRESS.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("AMEX", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.AMERICAN_EXPRESS.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithDankort() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.DANKORT.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("DANKORT", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.DANKORT.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithDiners() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.DINERS.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("DINERS", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.DINERS.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithDiscover() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.DISCOVER.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("DISCOVER", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.DISCOVER.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithJCB() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.JCB.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("JCB", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.JCB.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithMaestro() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.MAESTRO.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("MAESTRO", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.MAESTRO.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithUATP() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.UATP.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("UATP", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.UATP.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithUnknownBrand() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("MONZO", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARD_SSL.getMethodCode());
    }

    @Test
    public void shouldBuildTokenReplyWithoutCardHolderNameInPaymentInstrument() {
        final Token token = new Token();
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setvalue(AUTHENTICATED_SHOPPER_ID);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(TOKEN_REASON);
        tokenElements.add(tokenReason);

        final TokenDetails tokenDetail = new TokenDetails();
        tokenDetail.setTokenReason(tokenReason);
        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        tokenExpiry.setDate(createDate());
        tokenDetail.setPaymentTokenExpiry(tokenExpiry);
        final PaymentTokenID paymentTokenID = new PaymentTokenID();
        paymentTokenID.setvalue(PAYMENT_TOKEN_ID);
        tokenDetail.setPaymentTokenID(paymentTokenID);
        tokenDetail.setReportingTokenID(REPORTING_TOKEN);
        tokenDetail.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetail.setTokenEvent(TOKEN_EVENT);
        final ReportingTokenExpiry reportingTokenExpiry = new ReportingTokenExpiry();
        reportingTokenExpiry.setDate(createReportingTokenExpiryDate());
        tokenDetail.setReportingTokenExpiry(reportingTokenExpiry);
        tokenElements.add(tokenDetail);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetailsWithoutCardHolderName());
        tokenElements.add(paymentInstrument);

        final Date date = ((CardDetails) paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().get(0)).getExpiryDate().getDate();
        when(internalDateReverseConverterMock.convert(date)).thenReturn(dateMock);

        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_MESSAGE);
        tokenElements.add(error);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertEquals(AUTHENTICATED_SHOPPER_ID, tokenReply.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, tokenReply.getTokenEventReference());

        assertEquals(TOKEN_REASON, tokenReply.getTokenReason());

        assertEquals(CARD_NUMBER, tokenReply.getPaymentInstrument().getCardNumber());
        assertNull(tokenReply.getPaymentInstrument().getCardHolderName());

        assertEquals(CVC, tokenReply.getPaymentInstrument().getCvc());
        assertEquals(CITY, tokenReply.getPaymentInstrument().getCardAddress().getCity());
        assertEquals(COUNTRY_CODE, tokenReply.getPaymentInstrument().getCardAddress().getCountryCode());
        assertEquals(POSTAL_CODE, tokenReply.getPaymentInstrument().getCardAddress().getPostalCode());
        assertEquals(LAST_NAME, tokenReply.getPaymentInstrument().getCardAddress().getLastName());
        assertEquals(CARD_HOLDER_NAME, tokenReply.getPaymentInstrument().getCardAddress().getFirstName());
        assertEquals(STATE, tokenReply.getPaymentInstrument().getCardAddress().getState());
        assertEquals(TELEPHONE_NUMBER, tokenReply.getPaymentInstrument().getCardAddress().getTelephoneNumber());
        assertEquals(STREET, tokenReply.getPaymentInstrument().getCardAddress().getStreet());
        assertEquals(HOUSE_NAME, tokenReply.getPaymentInstrument().getCardAddress().getHouseName());
        assertEquals(HOUSE_NUMBER, tokenReply.getPaymentInstrument().getCardAddress().getHouseNumber());
        assertEquals(HOUSE_NUMBER_EXTENSION, tokenReply.getPaymentInstrument().getCardAddress().getHouseNumberExtension());
        assertEquals(ADDRESS_1, tokenReply.getPaymentInstrument().getCardAddress().getAddress1());
        assertEquals(ADDRESS_2, tokenReply.getPaymentInstrument().getCardAddress().getAddress2());
        assertEquals(ADDRESS_3, tokenReply.getPaymentInstrument().getCardAddress().getAddress3());

        assertEquals(EXPIRY_YEAR, tokenReply.getPaymentInstrument().getExpiryDate().getYear());
        assertEquals(EXPIRY_MONTH, tokenReply.getPaymentInstrument().getExpiryDate().getMonth());
        assertEquals(DAY_OF_MONTH, tokenReply.getPaymentInstrument().getExpiryDate().getDayOfMonth());
        assertEquals(HOUR, tokenReply.getPaymentInstrument().getExpiryDate().getHour());
        assertEquals(MINUTE, tokenReply.getPaymentInstrument().getExpiryDate().getMinute());
        assertEquals(SECOND, tokenReply.getPaymentInstrument().getExpiryDate().getSecond());

        assertEquals(ERROR_CODE, tokenReply.getError().getCode());
        assertEquals(ERROR_MESSAGE, tokenReply.getError().getMessage());
    }

    @Test
    public void shouldBuildTokenReplyWithoutCardAddress() {
        final Token token = new Token();
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setvalue(AUTHENTICATED_SHOPPER_ID);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(TOKEN_REASON);
        tokenElements.add(tokenReason);

        final TokenDetails tokenDetail = new TokenDetails();
        tokenDetail.setTokenReason(tokenReason);
        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        tokenExpiry.setDate(createDate());
        tokenDetail.setPaymentTokenExpiry(tokenExpiry);
        final PaymentTokenID paymentTokenID = new PaymentTokenID();
        paymentTokenID.setvalue(PAYMENT_TOKEN_ID);
        tokenDetail.setPaymentTokenID(paymentTokenID);
        tokenDetail.setReportingTokenID(REPORTING_TOKEN);
        tokenDetail.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetail.setTokenEvent(TOKEN_EVENT);
        final ReportingTokenExpiry reportingTokenExpiry = new ReportingTokenExpiry();
        reportingTokenExpiry.setDate(createReportingTokenExpiryDate());
        tokenDetail.setReportingTokenExpiry(reportingTokenExpiry);
        tokenElements.add(tokenDetail);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetailsWithoutAddress());
        tokenElements.add(paymentInstrument);

        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_MESSAGE);
        tokenElements.add(error);

        final Date date = ((CardDetails) paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().get(0)).getExpiryDate().getDate();
        when(internalDateReverseConverterMock.convert(date)).thenReturn(dateMock);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertEquals(AUTHENTICATED_SHOPPER_ID, tokenReply.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, tokenReply.getTokenEventReference());

        assertEquals(TOKEN_REASON, tokenReply.getTokenReason());

        assertEquals(CARD_NUMBER, tokenReply.getPaymentInstrument().getCardNumber());
        assertEquals(cardHolderName.getvalue(), tokenReply.getPaymentInstrument().getCardHolderName());
        assertEquals(CVC, tokenReply.getPaymentInstrument().getCvc());
        assertNull(tokenReply.getPaymentInstrument().getCardAddress());

        assertEquals(EXPIRY_YEAR, tokenReply.getPaymentInstrument().getExpiryDate().getYear());
        assertEquals(EXPIRY_MONTH, tokenReply.getPaymentInstrument().getExpiryDate().getMonth());
        assertEquals(DAY_OF_MONTH, tokenReply.getPaymentInstrument().getExpiryDate().getDayOfMonth());
        assertEquals(HOUR, tokenReply.getPaymentInstrument().getExpiryDate().getHour());
        assertEquals(MINUTE, tokenReply.getPaymentInstrument().getExpiryDate().getMinute());
        assertEquals(SECOND, tokenReply.getPaymentInstrument().getExpiryDate().getSecond());

        assertEquals(ERROR_CODE, tokenReply.getError().getCode());
        assertEquals(ERROR_MESSAGE, tokenReply.getError().getMessage());
    }

    @Test
    public void shouldBuildTokenReplyWithoutExpiryDate() {
        final Token token = new Token();
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setvalue(AUTHENTICATED_SHOPPER_ID);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(TOKEN_REASON);
        tokenElements.add(tokenReason);

        final TokenDetails tokenDetail = new TokenDetails();
        tokenDetail.setTokenReason(tokenReason);
        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        tokenExpiry.setDate(createDate());
        tokenDetail.setPaymentTokenExpiry(tokenExpiry);
        final PaymentTokenID paymentTokenID = new PaymentTokenID();
        paymentTokenID.setvalue(PAYMENT_TOKEN_ID);
        tokenDetail.setPaymentTokenID(paymentTokenID);
        tokenDetail.setReportingTokenID(REPORTING_TOKEN);
        tokenDetail.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetail.setTokenEvent(TOKEN_EVENT);
        final ReportingTokenExpiry reportingTokenExpiry = new ReportingTokenExpiry();
        reportingTokenExpiry.setDate(createReportingTokenExpiryDate());
        tokenDetail.setReportingTokenExpiry(reportingTokenExpiry);
        tokenElements.add(tokenDetail);

        when(internalDateReverseConverterMock.convert(reportingTokenExpiry.getDate())).thenReturn(reportingDateMock);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetailsWithoutExpiryDate());
        tokenElements.add(paymentInstrument);

        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_MESSAGE);
        tokenElements.add(error);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertEquals(AUTHENTICATED_SHOPPER_ID, tokenReply.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, tokenReply.getTokenEventReference());

        assertEquals(TOKEN_REASON, tokenReply.getTokenReason());

        assertEquals(CARD_NUMBER, tokenReply.getPaymentInstrument().getCardNumber());
        assertEquals(cardHolderName.getvalue(), tokenReply.getPaymentInstrument().getCardHolderName());

        assertEquals(CVC, tokenReply.getPaymentInstrument().getCvc());
        assertEquals(CITY, tokenReply.getPaymentInstrument().getCardAddress().getCity());
        assertEquals(COUNTRY_CODE, tokenReply.getPaymentInstrument().getCardAddress().getCountryCode());
        assertEquals(POSTAL_CODE, tokenReply.getPaymentInstrument().getCardAddress().getPostalCode());
        assertEquals(LAST_NAME, tokenReply.getPaymentInstrument().getCardAddress().getLastName());
        assertEquals(CARD_HOLDER_NAME, tokenReply.getPaymentInstrument().getCardAddress().getFirstName());
        assertEquals(STATE, tokenReply.getPaymentInstrument().getCardAddress().getState());
        assertEquals(TELEPHONE_NUMBER, tokenReply.getPaymentInstrument().getCardAddress().getTelephoneNumber());
        assertEquals(STREET, tokenReply.getPaymentInstrument().getCardAddress().getStreet());
        assertEquals(HOUSE_NAME, tokenReply.getPaymentInstrument().getCardAddress().getHouseName());
        assertEquals(HOUSE_NUMBER, tokenReply.getPaymentInstrument().getCardAddress().getHouseNumber());
        assertEquals(HOUSE_NUMBER_EXTENSION, tokenReply.getPaymentInstrument().getCardAddress().getHouseNumberExtension());
        assertEquals(ADDRESS_1, tokenReply.getPaymentInstrument().getCardAddress().getAddress1());
        assertEquals(ADDRESS_2, tokenReply.getPaymentInstrument().getCardAddress().getAddress2());
        assertEquals(ADDRESS_3, tokenReply.getPaymentInstrument().getCardAddress().getAddress3());

        assertNull(tokenReply.getPaymentInstrument().getExpiryDate());

        assertEquals(ERROR_CODE, tokenReply.getError().getCode());
        assertEquals(ERROR_MESSAGE, tokenReply.getError().getMessage());
    }

    @Test
    public void shouldBuildTokenReplyWithoutDerived() {
        final Token token = new Token();
        cardBrand = new CardBrand();
        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setvalue(AUTHENTICATED_SHOPPER_ID);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(TOKEN_REASON);
        tokenElements.add(tokenReason);

        final TokenDetails tokenDetail = new TokenDetails();
        tokenDetail.setTokenReason(tokenReason);
        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        tokenExpiry.setDate(createDate());
        tokenDetail.setPaymentTokenExpiry(tokenExpiry);
        final PaymentTokenID paymentTokenID = new PaymentTokenID();
        paymentTokenID.setvalue(PAYMENT_TOKEN_ID);
        tokenDetail.setPaymentTokenID(paymentTokenID);
        tokenDetail.setReportingTokenID(REPORTING_TOKEN);
        tokenDetail.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetail.setTokenEvent(TOKEN_EVENT);
        final ReportingTokenExpiry reportingTokenExpiry = new ReportingTokenExpiry();
        reportingTokenExpiry.setDate(createReportingTokenExpiryDate());
        tokenDetail.setReportingTokenExpiry(reportingTokenExpiry);
        tokenElements.add(tokenDetail);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetailsWithoutDerived());
        tokenElements.add(paymentInstrument);

        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_MESSAGE);
        tokenElements.add(error);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertEquals(AUTHENTICATED_SHOPPER_ID, tokenReply.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, tokenReply.getTokenEventReference());

        assertEquals(TOKEN_REASON, tokenReply.getTokenReason());

        assertNull(tokenReply.getPaymentInstrument());

        assertEquals(ERROR_CODE, tokenReply.getError().getCode());
        assertEquals(ERROR_MESSAGE, tokenReply.getError().getMessage());
    }

    @Test
    public void shouldBuildWebformRefundReply() {
        final ShopperWebformRefundDetails shopperWebformRefundDetails = new ShopperWebformRefundDetails();
        createAmount();
        shopperWebformRefundDetails.setWebformURL(WEBFORM_URL);
        shopperWebformRefundDetails.setWebformId(WEBFORM_ID);
        shopperWebformRefundDetails.setWebformStatus(WEBFORM_STATUS);
        shopperWebformRefundDetails.setPaymentId(PAYMENT_ID);
        shopperWebformRefundDetails.setReason(REASON);
        shopperWebformRefundDetails.setRefundId(REFUND_ID);
        shopperWebformRefundDetails.setAmount(amount);

        when(internalAmountReverseConverterMock.convert(shopperWebformRefundDetails.getAmount())).thenReturn(amountMock);

        final WebformRefundReply result = testObj.buildWebformRefundReply(shopperWebformRefundDetails);

        assertEquals(WEBFORM_URL, result.getWebformURL());
        assertEquals(WEBFORM_ID, result.getWebformId());
        assertEquals(WEBFORM_STATUS, result.getWebformStatus());
        assertEquals(PAYMENT_ID, result.getPaymentId());
        assertEquals(REASON, result.getReason());
        assertEquals(REFUND_ID, result.getRefundId());
        final com.worldpay.data.Amount amountResult = result.getAmount();
        assertEquals(amount.getValue(), amountResult.getValue());
    }

    @Test
    public void shouldBuildUpdateTokenReply() {
        when(updateTokenReceivedMock.getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);

        final UpdateTokenReply result = testObj.buildUpdateTokenReply(updateTokenReceivedMock);

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenId());
    }

    @Test
    public void buildTokenReply_shouldBuildTokenReplyWithCardContainingAnExistingPaymentTypeWhenCardBrandReceivedIsNotInPaymentTypeNameMethods() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();
        cardBrand.setvalue(PaymentType.MASTERCARD.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("ECMC", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.MASTERCARD.getMethodCode());
    }

    @Test
    public void buildTokenReply_shouldBuildTokenReplyWithCardContainingAnExistingPaymentTypeWhenCardBrandReceivedIsInPaymentTypeNameMethods() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();
        cardBrand.setvalue(PaymentType.MASTERCARD.getMethodCode());

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails("ECMC-SSL", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.MASTERCARD.getMethodCode());
    }

    @Test
    public void buildTokenReply_shouldBuildTokenReplyWithCardContainingAnExistingPaymentTypeWhenCardBrandReceivedIsEmpty() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();

        cardBrand.setvalue(PaymentType.CARD_SSL.getMethodCode());
        when(cardBrandFactoryMock.createCardBrandWithValue(anyString())).thenReturn(cardBrand);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(createCardDetails(null, null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARD_SSL.getMethodCode());
    }

    @Test
    public void buildJournalReply_ShouldBuildTheJournal() {
        when(internalDateReverseConverterMock.convert(journalMock.getBookingDate().getDate())).thenReturn(dateMock);

        final JournalReply result = testObj.buildJournalReply(journalMock);

        assertThat(result).isNotNull();
        assertThat(result.getJournalType()).isEqualTo(AUTHORISED);
        assertThat(result.getBookingDate()).isNotNull();
        assertThat(result.getBookingDate().getMinute()).isEqualTo(MINUTE);
        assertThat(result.getAccountTransactions()).hasSize(2);
        assertThat(result.getAccountTransactions().get(0).getAccountType()).isEqualTo(PERSONAL_ACCOUNT);
        assertThat(result.getAccountTransactions().get(0).getBatchId()).isEqualTo(BATCH_1);
        assertThat(result.getAccountTransactions().get(1).getAccountType()).isEqualTo(BUSSINESS_ACCOUNT);
        assertThat(result.getAccountTransactions().get(1).getBatchId()).isEqualTo(BATCH_2);
    }

    @Test
    public void buildDeleteTokenReply_ShouldBuildTheTokenReply() {
        final DeleteTokenReply result = testObj.buildDeleteTokenReply(deletedTokenReceivedMock);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentTokenId()).isEqualTo(TOKEN_ID);
    }

    @Test
    public void buildExemptionResponse_ShouldReturnExemptionResponseInfoFullyPopulated_WhenAllDataRelatedToExemptionResponseIsPresentInExemptionResponse() {
        testObj.buildExemptionResponse(exemptionResponseMock);

        verify(exemptionResponseReverseConverterMock).convert(exemptionResponseMock);
    }

    @Test
    public void buildExemptionResponse_ShouldReturnNull_WhenExemptionResponseIsNull() {
        testObj.buildExemptionResponse(null);

        verify(exemptionResponseReverseConverterMock, never()).convert(exemptionResponseMock);
    }

    private CardDetails createCardDetails(final String cartBrand, final String cardSubBrand) {
        final CardDetails cardDetails = new CardDetails();
        cardDetails.setDerived(createDerived(cartBrand, cardSubBrand));
        cardDetails.setExpiryDate(createExpiryDate());
        cardDetails.setCardHolderName(cardHolderName);
        cardDetails.setCardAddress(createCardAddress());
        cardDetails.setCvc(createCVC());
        return cardDetails;
    }

    private CardAddress createCardAddress() {
        final CardAddress cardAddress = new CardAddress();
        final Address address = new Address();
        final CountryCode countryCode = new CountryCode();
        countryCode.setvalue(COUNTRY_CODE);
        address.setFirstName(CARD_HOLDER_NAME);
        address.setCity(CITY);
        address.setCountryCode(countryCode);
        address.setLastName(LAST_NAME);
        address.setPostalCode(POSTAL_CODE);
        address.setState(STATE);
        address.setTelephoneNumber(TELEPHONE_NUMBER);
        final List<Object> addressElements = address.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();
        final Street street = new Street();
        street.setvalue(STREET);
        addressElements.add(street);

        final HouseName houseName = new HouseName();
        houseName.setvalue(HOUSE_NAME);
        addressElements.add(houseName);
        final HouseNumber houseNumber = new HouseNumber();
        houseNumber.setvalue(HOUSE_NUMBER);
        addressElements.add(houseNumber);
        final HouseNumberExtension houseNumberExtension = new HouseNumberExtension();
        houseNumberExtension.setvalue(HOUSE_NUMBER_EXTENSION);
        addressElements.add(houseNumberExtension);
        final Address1 address1 = new Address1();
        address1.setvalue(ADDRESS_1);
        addressElements.add(address1);
        final Address2 address2 = new Address2();
        address2.setvalue(ADDRESS_2);
        addressElements.add(address2);
        final Address3 address3 = new Address3();
        address3.setvalue(ADDRESS_3);
        addressElements.add(address3);

        cardAddress.setAddress(address);
        return cardAddress;
    }

    private CardDetails createCardDetailsWithoutAddress() {
        final CardDetails cardDetails = new CardDetails();
        cardDetails.setDerived(createDerived("VISA", null));
        cardDetails.setExpiryDate(createExpiryDate());
        cardDetails.setCardHolderName(cardHolderName);
        cardDetails.setCvc(createCVC());
        return cardDetails;
    }

    private CardDetails createCardDetailsWithoutExpiryDate() {
        final CardDetails cardDetails = new CardDetails();
        cardDetails.setDerived(createDerived("VISA", null));
        cardDetails.setCardHolderName(cardHolderName);
        cardDetails.setCvc(createCVC());
        cardDetails.setCardAddress(createCardAddress());
        return cardDetails;
    }

    private CardDetails createCardDetailsWithoutDerived() {
        final CardDetails cardDetails = new CardDetails();
        cardDetails.setExpiryDate(createExpiryDate());
        cardDetails.setCardHolderName(cardHolderName);
        cardDetails.setCvc(createCVC());
        return cardDetails;
    }

    private CardDetails createCardDetailsWithoutCardHolderName() {
        final CardDetails cardDetails = new CardDetails();
        cardDetails.setDerived(createDerived("VISA", null));
        cardDetails.setExpiryDate(createExpiryDate());
        cardDetails.setCvc(createCVC());
        cardDetails.setCardAddress(createCardAddress());
        return cardDetails;
    }

    private Cvc createCVC() {
        final Cvc cvc = new Cvc();
        cvc.setvalue(CVC);
        return cvc;
    }

    private ExpiryDate createExpiryDate() {
        final ExpiryDate expiryDate = new ExpiryDate();
        expiryDate.setDate(createDate());
        return expiryDate;
    }

    private Derived createDerived(final String cardBrand, final String cardCoBrand) {
        final Derived derived = new Derived();
        this.cardBrand = new CardBrand();
        this.cardBrand.setvalue(cardBrand);
        derived.setCardBrand(this.cardBrand);
        derived.setCardCoBrand(cardCoBrand);
        derived.setCardSubBrand(CARD_SUB_BRAND);
        derived.setIssuerCountryCode(ISSUER_COUNTRY_CODE);
        derived.setObfuscatedPAN(CARD_NUMBER);
        derived.setBin(BIN);
        return derived;
    }

    private Date createDate() {
        final Date date = new Date();
        date.setYear(EXPIRY_YEAR);
        date.setMonth(EXPIRY_MONTH);
        date.setDayOfMonth(DAY_OF_MONTH);
        date.setHour(HOUR);
        date.setMinute(MINUTE);
        date.setSecond(SECOND);
        return date;
    }

    private Date createReportingTokenExpiryDate() {
        final Date date = new Date();
        date.setYear(REPORTING_EXPIRY_YEAR);
        date.setMonth(REPORTING_EXPIRY_MONTH);
        date.setDayOfMonth(REPORTING_DAY_OF_MONTH);
        date.setHour(REPORTING_HOUR);
        date.setMinute(REPORTING_MINUTE);
        date.setSecond(REPORTING_SECOND);
        return date;
    }

    private void createAavAddressResultCode() {
        aavAddressResultCode = new AAVAddressResultCode();
        aavAddressResultCode.getDescription().add(AAV_ADDRESS_RESULT_CODE_DESCRIPTION);
    }


    private void createAavCardholderNameResultCode() {
        aavCardholderNameResultCode = new AAVCardholderNameResultCode();
        aavCardholderNameResultCode.getDescription().add(AAV_CARDHOLDER_NAME_RESULT_CODE_DESCRIPTION);
    }

    private void createAavEmailResultCode() {
        aavEmailResultCode = new AAVEmailResultCode();
        aavEmailResultCode.getDescription().add(AAV_EMAIL_RESULT_CODE_DESCRIPTION);
    }

    private void createAavPostcodeResultCode() {
        aavPostcodeResultCode = new AAVPostcodeResultCode();
        aavPostcodeResultCode.getDescription().add(AAV_POSTCODE_RESULT_CODE_DESCRIPTION);
    }

    private void createAavTelephoneResultCode() {
        aavTelephoneResultCode = new AAVTelephoneResultCode();
        aavTelephoneResultCode.getDescription().add(AAV_TELEPHONE_RESULT_CODE_DESCRIPTION);
    }

    private void createRiskScore() {
        riskScore = new RiskScore();
        riskScore.setValue(RISK_SCORE_VALUE);
    }

    private void createIso8583ReturnCode() {
        iso8583ReturnCode = new ISO8583ReturnCode();
        iso8583ReturnCode.setCode(ISO8583_RETURN_CODE_CODE);
        iso8583ReturnCode.setDescription(ISO8583_RETURN_CODE_DESCRIPTION);
    }

    private void createBalance() {
        createBalanceAmount();

        balance = new Balance();
        balance.setAccountType(BALANCE_ACCOUNT_TYPE);
        balance.setAmount(balanceAmount);
    }

    private void createCvcResultCode() {
        cvcResultCode = new CVCResultCode();
        cvcResultCode.getDescription().add(CVC_RESULT_CODE_DESCRIPTION);
    }

    private void createAmount() {
        amount = new Amount();
        amount.setValue(AMOUNT_VALUE);
        amount.setCurrencyCode(AMOUNT_CURRENCY_CODE);
        amount.setExponent(AMOUNT_EXPONENT);
        amount.setDebitCreditIndicator(AMOUNT_DEBIT_CREDIT_INDICATOR);
    }

    private void createBalanceAmount() {
        balanceAmount = new Amount();
        balanceAmount.setValue(BALANCE_AMOUNT_VALUE);
        balanceAmount.setCurrencyCode(BALANCE_AMOUNT_CURRENCY_CODE);
        balanceAmount.setExponent(BALANCE_AMOUNT_EXPONENT);
        balanceAmount.setDebitCreditIndicator(BALANCE_AMOUNT_DEBIT_CREDIT_INDICATOR);
    }

    private void createAuthorisationId() {
        authorisationId = new AuthorisationId();
        authorisationId.setBy(AUTHORISATION_ID_BY);
        authorisationId.setId(AUTHORISATION_ID);
    }
}
