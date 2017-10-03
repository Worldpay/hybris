package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreateTokenResponseTransformerTest {

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

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

    @InjectMocks
    private CreateTokenResponseTransformer testObj;

    @Mock
    private PaymentService paymentServiceReplyMock;
    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;
    @Mock
    private TokenReply tokenReplyMock;

    @Test
    public void shouldRaiseErrorIfPaymentServiceReplyIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No reply message in Worldpay create token response");
        final Reply reply = null;
        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(reply));

        testObj.transform(paymentServiceReplyMock);
    }

    @Test
    public void shouldRaiseErrorIfResponseTypeIsNotReply() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Reply type from Worldpay not the expected type");
        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(new Submit()));

        testObj.transform(paymentServiceReplyMock);
    }

    @Test
    public void shouldReturnCreateTokenResponseForCard() throws WorldpayModelTransformationException {
        when(serviceResponseTransformerHelperMock.buildTokenReply(any(Token.class))).thenReturn(tokenReplyMock);

        final CreateTokenResponse result = (CreateTokenResponse) testObj.transform(createServiceReplyCard());

        assertEquals(tokenReplyMock, result.getToken());
    }

    @Test
    public void shouldReturnCreateTokenResponseForPaypal() throws WorldpayModelTransformationException {
        when(serviceResponseTransformerHelperMock.buildTokenReply(any(Token.class))).thenReturn(tokenReplyMock);

        final CreateTokenResponse result = (CreateTokenResponse) testObj.transform(createServiceReplyPaypal());

        assertEquals(tokenReplyMock, result.getToken());
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

        when(serviceResponseTransformerHelperMock.checkForError(any(ServiceResponse.class), eq(reply))).thenReturn(true);

        final ServiceResponse result = testObj.transform(paymentServiceReply);

        verify(serviceResponseTransformerHelperMock).checkForError(result, reply);
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

        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(reply));
        return paymentServiceReplyMock;
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

        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(reply));
        return paymentServiceReplyMock;
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
