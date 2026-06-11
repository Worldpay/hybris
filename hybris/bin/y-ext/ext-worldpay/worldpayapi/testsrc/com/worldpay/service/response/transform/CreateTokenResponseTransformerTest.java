package com.worldpay.service.response.transform;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.data.token.TokenReply;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Address;
import com.worldpay.internal.model.AuthenticatedShopperID;
import com.worldpay.internal.model.CardAddress;
import com.worldpay.internal.model.CardBrand;
import com.worldpay.internal.model.CardDetails;
import com.worldpay.internal.model.CardHolderName;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.Derived;
import com.worldpay.internal.model.Error;
import com.worldpay.internal.model.ExpiryDate;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenExpiry;
import com.worldpay.internal.model.PaymentTokenID;
import com.worldpay.internal.model.Paypal;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Submit;
import com.worldpay.internal.model.Token;
import com.worldpay.internal.model.TokenDetails;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
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
    private static final String ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE = "Reply has no reply message or the reply type is not the expected one";

    @InjectMocks
    private CreateTokenResponseTransformer testObj;

    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;

    @Mock
    private PaymentService paymentServiceReplyMock;
    @Mock
    private TokenReply tokenReplyMock;

    @Test
    public void shouldRaiseErrorIfPaymentServiceReplyIsNull() {
        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(null));

        assertThatThrownBy(() -> testObj.transform(paymentServiceReplyMock))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
    }

    @Test
    public void shouldRaiseErrorIfResponseTypeIsNotReply() {
        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(new Submit()));

        assertThatThrownBy(() -> testObj.transform(paymentServiceReplyMock))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
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
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setValue(ERROR_VALUE);
        responses.add(error);
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.checkForError(any(ServiceResponse.class), eq(reply))).thenReturn(true);

        final ServiceResponse result = testObj.transform(paymentServiceReply);

        verify(serviceResponseTransformerHelperMock).checkForError(result, reply);
    }

    private PaymentService createServiceReplyCard() {
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final Date date = createExpiryDate();
        final Token token = createToken();

        responses.add(token);

        final List<Object> tokenResponses = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();
        final TokenDetails tokenDetails = createTokenDetails(date);
        tokenResponses.add(createTokenReason());
        tokenResponses.add(tokenDetails);

        final CardDetails cardDetails = createCardDetails(date);
        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(cardDetails);
        tokenResponses.add(paymentInstrument);

        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(reply));
        return paymentServiceReplyMock;
    }

    private PaymentService createServiceReplyPaypal() {
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final Date date = createExpiryDate();
        final Token token = createToken();

        responses.add(token);

        final List<Object> tokenResponses = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError();
        final TokenDetails tokenDetails = createTokenDetails(date);
        tokenResponses.add(createTokenReason());
        tokenResponses.add(tokenDetails);

        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        final Paypal paypal = new Paypal();
        paypal.setValue(PAYPAL_TOKEN);
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(paypal);
        tokenResponses.add(paymentInstrument);

        when(paymentServiceReplyMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(reply));
        return paymentServiceReplyMock;
    }

    private Token createToken() {
        final Token token = new Token();
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setValue(AUTHENTICATED_SHOPPER);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        return token;
    }

    private TokenDetails createTokenDetails(Date date) {
        final TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setTokenEvent(TOKEN_EVENT);
        final PaymentTokenID paymentTokenID = new PaymentTokenID();
        paymentTokenID.setValue(TOKEN_ID);
        tokenDetails.setPaymentTokenID(paymentTokenID);
        tokenDetails.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenDetails.setTokenReason(createTokenReason());
        final PaymentTokenExpiry paymentTokenExpiry = new PaymentTokenExpiry();
        paymentTokenExpiry.setDate(date);
        tokenDetails.setPaymentTokenExpiry(paymentTokenExpiry);
        return tokenDetails;
    }

    private TokenReason createTokenReason() {
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setValue(TOKEN_REASON);
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
        cardHolderName.setValue(CARD_HOLDER_NAME);
        cardDetails.setCardHolderName(cardHolderName);
        final CardAddress cardAddress = new CardAddress();
        final Address address = new Address();
        address.setCity(CARD_ADDRESS_CITY);
        cardAddress.setAddress(address);
        cardDetails.setCardAddress(cardAddress);
        final Derived derived = new Derived();
        final CardBrand cardBrand = new CardBrand();
        cardBrand.setValue(CARD_BRAND);
        derived.setCardBrand(cardBrand);
        derived.setCardSubBrand(CARD_SUB_BRAND);
        derived.setIssuerCountryCode(DK);
        derived.setObfuscatedPAN(OBFUSCATED_PAN);
        cardDetails.setDerived(derived);
        return cardDetails;
    }
}
