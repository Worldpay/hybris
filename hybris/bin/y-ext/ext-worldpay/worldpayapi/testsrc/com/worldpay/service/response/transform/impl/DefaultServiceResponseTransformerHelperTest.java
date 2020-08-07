package com.worldpay.service.response.transform.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.WebformRefundReply;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.model.token.UpdateTokenReply;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultServiceResponseTransformerHelperTest {

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

    private DefaultServiceResponseTransformerHelper testObj = new DefaultServiceResponseTransformerHelper();

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

    @Mock
    private UpdateTokenReceived updateTokenReceivedMock;

    @Before
    public void setUp() {
        cardHolderName = new CardHolderName();
        cardHolderName.setvalue(CARD_HOLDER_NAME);
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

        assertEquals(PAYMENT_METHOD, paymentReply.getMethodCode());
        assertEquals(EXPIRY_MONTH, paymentReply.getCardDetails().getExpiryDate().getMonth());
        assertEquals(EXPIRY_YEAR, paymentReply.getCardDetails().getExpiryDate().getYear());
        assertEquals(CARD_HOLDER_NAME, paymentReply.getCardDetails().getCardHolderName());
        assertEquals(CARD_NUMBER, paymentReply.getCardDetails().getCardNumber());
        assertEquals(PaymentType.VISA, paymentReply.getCardDetails().getPaymentType());
        assertEquals(AuthorisedStatus.AUTHORISED, paymentReply.getAuthStatus());
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

        final PaymentReply paymentReply = testObj.buildPaymentReply(payment);

        assertEquals(PAYMENT_METHOD, paymentReply.getMethodCode());
        assertEquals(EXPIRY_MONTH, paymentReply.getCardDetails().getExpiryDate().getMonth());
        assertEquals(EXPIRY_YEAR, paymentReply.getCardDetails().getExpiryDate().getYear());
        assertEquals(CARD_HOLDER_NAME, paymentReply.getCardDetails().getCardHolderName());
        assertEquals(CARD_NUMBER, paymentReply.getCardDetails().getCardNumber());
        assertEquals(PaymentType.VISA, paymentReply.getCardDetails().getPaymentType());
        assertEquals(AuthorisedStatus.AUTHORISED, paymentReply.getAuthStatus());
        assertEquals(CVC_RESULT_CODE_DESCRIPTION, paymentReply.getCvcResultDescription());
        assertEquals(ISO8583_RETURN_CODE_CODE, paymentReply.getReturnCode());
        assertThat(paymentReply.getRiskScore()).isEqualTo(null);
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
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

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
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("VISA", null));
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
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("VISA", "CARTEBLEUE"));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARTE_BLEUE);
    }

    @Test
    public void shouldBuildTokenReplyWithCarteBancaireMasterCard() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("ECMC", "CB"));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARTE_BANCAIRE);
    }

    @Test
    public void shouldBuildTokenReplyWithVisa() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("VISA", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.VISA);
    }

    @Test
    public void shouldBuildTokenReplyWithAirplus() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("AIRPLUS", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.AIRPLUS);
    }

    @Test
    public void shouldBuildTokenReplyWithAMEX() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("AMEX", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.AMERICAN_EXPRESS);
    }

    @Test
    public void shouldBuildTokenReplyWithDankort() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("DANKORT", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.DANKORT);
    }

    @Test
    public void shouldBuildTokenReplyWithDiners() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("DINERS", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.DINERS);
    }

    @Test
    public void shouldBuildTokenReplyWithDiscover() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("DISCOVER", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.DISCOVER);
    }

    @Test
    public void shouldBuildTokenReplyWithJCB() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("JCB", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.JCB);
    }

    @Test
    public void shouldBuildTokenReplyWithMaestro() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("MAESTRO", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.MAESTRO);
    }

    @Test
    public void shouldBuildTokenReplyWithUATP() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("UATP", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.UATP);
    }

    @Test
    public void shouldBuildTokenReplyWithUnknownBrand() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("MONZO", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARD_SSL);
    }

    @Test
    public void shouldBuildTokenReplyWithoutCardHolderNameInPaymentInstrument() {
        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

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
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetailsWithoutCardHolderName());
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
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

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
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetailsWithoutAddress());
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
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

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
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetailsWithoutExpiryDate());
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
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

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
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetailsWithoutDerived());
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

        final WebformRefundReply result = testObj.buildWebformRefundReply(shopperWebformRefundDetails);

        assertEquals(WEBFORM_URL, result.getWebformURL());
        assertEquals(WEBFORM_ID, result.getWebformId());
        assertEquals(WEBFORM_STATUS, result.getWebformStatus());
        assertEquals(PAYMENT_ID, result.getPaymentId());
        assertEquals(REASON, result.getReason());
        assertEquals(REFUND_ID, result.getRefundId());
        final com.worldpay.service.model.Amount amountResult = result.getAmount();
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
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("ECMC", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.MASTERCARD);
    }

    @Test
    public void buildTokenReply_shouldBuildTokenReplyWithCardContainingAnExistingPaymentTypeWhenCardBrandReceivedIsInPaymentTypeNameMethods() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails("ECMC-SSL", null));
        tokenElements.add(paymentInstrument);

        final TokenReply tokenReply = testObj.buildTokenReply(token);

        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.MASTERCARD);
    }

    @Test
    public void buildTokenReply_shouldBuildTokenReplyWithCardContainingAnExistingPaymentTypeWhenCardBrandReceivedIsEmpty() {
        final Token token = new Token();
        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError();
        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(createCardDetails(null,null));
        tokenElements.add(paymentInstrument);
        final TokenReply tokenReply = testObj.buildTokenReply(token);
        assertThat(tokenReply.getPaymentInstrument().getPaymentType()).isEqualTo(PaymentType.CARD_SSL);
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
        address.setFirstName(CARD_HOLDER_NAME);
        address.setCity(CITY);
        address.setCountryCode(COUNTRY_CODE);
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
        derived.setCardBrand(cardBrand);
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
