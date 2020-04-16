package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.enums.token.TokenEvent;
import com.worldpay.internal.model.*;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayTokenCreateResponseBuilderTest {

    private static final String CC_OWNER = "ccOwner";
    private static final String CARD_BRAND = "VISA";
    private static final String CARD_SUB_BRAND = "VISA_CREDIT";
    private static final String ISSUER_COUNTRY_CODE = "N/A";
    private static final String OBFUSCATED_PAN = "4444********1111";

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @InjectMocks
    private DefaultWorldpayTokenCreateResponseBuilder testObj;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Submit submitMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTokenCreate paymentTokenCreateMock;

    @Test
    public void buildTokenResponse() {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(Collections.singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge()).thenReturn(Collections.singletonList(paymentTokenCreateMock));
        when(paymentTokenCreateMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(paymentTokenCreateMock.getCreateToken().getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);

        final PaymentService result = testObj.buildTokenResponse(paymentServiceMock);

        final Reply reply = (Reply) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final Token token = (Token) reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);

        final TokenDetails tokenDetails = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError().stream().filter(TokenDetails.class::isInstance).map(TokenDetails.class::cast).findAny().orElseThrow(() -> new IllegalStateException("TokenDetails not present"));
        final PaymentInstrument paymentInstrument = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError().stream().filter(PaymentInstrument.class::isInstance).map(PaymentInstrument.class::cast).findAny().orElseThrow(() -> new IllegalStateException("PaymentDetails not present"));

        final CardDetails cardDetails = (CardDetails) paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().get(0);
        final Derived derived = cardDetails.getDerived();
        final Date expiryDate = cardDetails.getExpiryDate().getDate();

        assertEquals(AUTHENTICATED_SHOPPER_ID, token.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, token.getTokenEventReference());

        assertEquals(CARD_BRAND, derived.getCardBrand());
        assertEquals(CARD_SUB_BRAND, derived.getCardSubBrand());
        assertEquals(ISSUER_COUNTRY_CODE, derived.getIssuerCountryCode());
        assertEquals(OBFUSCATED_PAN, derived.getObfuscatedPAN());
        assertEquals(Integer.toString(LocalDate.now().getMonthValue()), expiryDate.getMonth());
        assertEquals(Integer.toString(LocalDate.now().plusYears(5).getYear()), expiryDate.getYear());
        assertFalse(StringUtils.isEmpty(tokenDetails.getPaymentTokenID().getvalue()));
        assertEquals(TokenEvent.NEW.name(), tokenDetails.getTokenEvent());
        assertEquals(CC_OWNER, cardDetails.getCardHolderName().getvalue());
    }
}
