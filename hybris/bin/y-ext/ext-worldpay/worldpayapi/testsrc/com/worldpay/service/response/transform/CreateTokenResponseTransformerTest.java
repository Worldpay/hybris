package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class CreateTokenResponseTransformerTest {

    private static final String AUTHENTICATED_SHOPPER = "authenticatedShopper";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT = "tokenEvent";
    private static final String TOKEN_ID = "tokenId";
    private static final String DAY_OF_MONTH = "12";
    private static final String MONTH = "1";
    private static final String YEAR = "2012";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String CARD_ADDRESS_CITY = "cardAddressCity";
    private static final String OBFUSCATED_PAN = "obfuscatedPAN";
    private static final String CARD_BRAND = "VISA-SSL";
    private static final String CARD_SUB_BRAND = "cardSubBrand";
    private static final String DK = "DK";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_VALUE = "errorValue";
    private static final String PAYPAL_TOKEN = "paypalToken";
    
    private PaymentService paymentServiceReply;
    private CreateTokenResponseTransformer testObj = new CreateTokenResponseTransformer();

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        paymentServiceReply = new PaymentService();
    }

    @Test
    public void shouldRaiseErrorIfPaymentServiceReplyIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No reply message in Worldpay create token response");
        final Reply reply = null;
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldRaiseErrorIfResponseTypeIsNotReply() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Reply type from Worldpay not the expected type");
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(new Submit());
        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldReturnCreateTokenResponseForCard() throws WorldpayModelTransformationException {
        final CreateTokenResponse result = (CreateTokenResponse) testObj.transform(createServiceReplyCard());

        assertEquals(AUTHENTICATED_SHOPPER, result.getToken().getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, result.getToken().getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getToken().getTokenReason());
        assertEquals(TOKEN_EVENT, result.getToken().getTokenDetails().getTokenEvent());
        assertEquals(TOKEN_ID, result.getToken().getTokenDetails().getPaymentTokenID());
        assertEquals(DAY_OF_MONTH, result.getToken().getTokenDetails().getPaymentTokenExpiry().getDayOfMonth());
        assertEquals(MONTH, result.getToken().getTokenDetails().getPaymentTokenExpiry().getMonth());
        assertEquals(YEAR, result.getToken().getTokenDetails().getPaymentTokenExpiry().getYear());
        assertEquals(TOKEN_EVENT_REFERENCE, result.getToken().getTokenDetails().getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getToken().getTokenDetails().getTokenReason());
        assertEquals(MONTH, result.getToken().getPaymentInstrument().getExpiryDate().getMonth());
        assertEquals(YEAR, result.getToken().getPaymentInstrument().getExpiryDate().getYear());
        assertEquals(CARD_HOLDER_NAME, result.getToken().getPaymentInstrument().getCardHolderName());
        assertEquals(CARD_ADDRESS_CITY, result.getToken().getPaymentInstrument().getCardAddress().getCity());
        assertEquals(OBFUSCATED_PAN, result.getToken().getPaymentInstrument().getCardNumber());
        assertEquals(CARD_BRAND, result.getToken().getPaymentInstrument().getPaymentType().getMethodCode());
        assertNull(result.getToken().getPaypalDetails());
    }

    @Test
    public void shouldReturnCreateTokenResponseForPaypal() throws WorldpayModelTransformationException {
        final CreateTokenResponse result = (CreateTokenResponse) testObj.transform(createServiceReplyPaypal());

        assertEquals(AUTHENTICATED_SHOPPER, result.getToken().getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, result.getToken().getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getToken().getTokenReason());
        assertEquals(TOKEN_EVENT, result.getToken().getTokenDetails().getTokenEvent());
        assertEquals(TOKEN_ID, result.getToken().getTokenDetails().getPaymentTokenID());
        assertEquals(DAY_OF_MONTH, result.getToken().getTokenDetails().getPaymentTokenExpiry().getDayOfMonth());
        assertEquals(MONTH, result.getToken().getTokenDetails().getPaymentTokenExpiry().getMonth());
        assertEquals(YEAR, result.getToken().getTokenDetails().getPaymentTokenExpiry().getYear());
        assertEquals(TOKEN_EVENT_REFERENCE, result.getToken().getTokenDetails().getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getToken().getTokenDetails().getTokenReason());
        assertEquals(PAYPAL_TOKEN, result.getToken().getPaypalDetails());
        assertNull(result.getToken().getPaymentInstrument());
    }

    @Test
    public void shouldReturnTokenResponseWithErrorWhenErrorOccurs() throws WorldpayModelTransformationException {

        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_VALUE);
        responses.add(error);
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        final ServiceResponse result = testObj.transform(paymentServiceReply);

        assertEquals(ERROR_VALUE, result.getErrorDetail().getMessage());
        assertEquals(ERROR_CODE, result.getErrorDetail().getCode());
    }

    private PaymentService createServiceReplyCard() {
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final Date date = createExpiryDate();
        final Token token = createToken();

        responses.add(token);

        final List<Object> tokenResponses = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError();
        final TokenDetails tokenDetails = createTokenDetails(date);
        tokenResponses.add(createTokenReason());
        tokenResponses.add(tokenDetails);

        final CardDetails cardDetails = createCardDetails(date);
        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().add(cardDetails);
        tokenResponses.add(paymentInstrument);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        return paymentServiceReply;
    }

    private PaymentService createServiceReplyPaypal() {
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final Date date = createExpiryDate();
        final Token token = createToken();

        responses.add(token);

        final List<Object> tokenResponses = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError();
        final TokenDetails tokenDetails = createTokenDetails(date);
        tokenResponses.add(createTokenReason());
        tokenResponses.add(tokenDetails);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        final Paypal paypal = new Paypal();
        paypal.setvalue(PAYPAL_TOKEN);
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().add(paypal);
        tokenResponses.add(paymentInstrument);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        return paymentServiceReply;
    }

    private Token createToken() {
        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        return token;
    }

    private TokenDetails createTokenDetails(Date date) {
        final TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setTokenEvent(TOKEN_EVENT);
        tokenDetails.setPaymentTokenID(TOKEN_ID);
        tokenDetails.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetails.setTokenReason(createTokenReason());
        final PaymentTokenExpiry paymentTokenExpiry = new PaymentTokenExpiry();
        paymentTokenExpiry.setDate(date);
        tokenDetails.setPaymentTokenExpiry(paymentTokenExpiry);
        return tokenDetails;
    }

    private TokenReason createTokenReason() {
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(TOKEN_REASON);
        return tokenReason;
    }

    private Date createExpiryDate() {
        final Date date = new Date();
        date.setDayOfMonth(DAY_OF_MONTH);
        date.setMonth(MONTH);
        date.setYear(YEAR);
        return date;
    }

    private CardDetails createCardDetails(Date date) {
        final CardDetails cardDetails = new CardDetails();
        final ExpiryDate expiryDate = new ExpiryDate();
        expiryDate.setDate(date);
        cardDetails.setExpiryDate(expiryDate);
        final CardHolderName cardHolderName = new CardHolderName();
        cardHolderName.setvalue(CARD_HOLDER_NAME);
        cardDetails.setCardHolderName(cardHolderName);
        final CardAddress cardAddress = new CardAddress();
        final Address address = new Address();
        address.setCity(CARD_ADDRESS_CITY);
        cardAddress.setAddress(address);
        cardDetails.setCardAddress(cardAddress);
        final Derived derived = new Derived();
        derived.setCardBrand(CARD_BRAND);
        derived.setCardSubBrand(CARD_SUB_BRAND);
        derived.setIssuerCountryCode(DK);
        derived.setObfuscatedPAN(OBFUSCATED_PAN);
        cardDetails.setDerived(derived);
        return cardDetails;
    }
}
