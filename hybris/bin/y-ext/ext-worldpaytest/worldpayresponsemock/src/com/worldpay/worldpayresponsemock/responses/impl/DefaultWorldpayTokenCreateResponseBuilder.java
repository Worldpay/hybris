package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.enums.token.TokenEvent;
import com.worldpay.internal.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayTokenCreateResponseBuilder implements com.worldpay.worldpayresponsemock.responses.WorldpayTokenCreateResponseBuilder {

    private static final String CC_OWNER = "ccOwner";
    private static final String CARD_BRAND = "VISA";
    private static final String CARD_SUB_BRAND = "VISA_CREDIT";
    private static final String ISSUER_COUNTRY_CODE = "N/A";
    private static final String OBFUSCATED_PAN = "4444********1111";

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentService buildTokenResponse(final PaymentService paymentService) {

        final PaymentService response = new PaymentService();

        final Submit submit = (Submit) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final PaymentTokenCreate paymentTokenCreate = (PaymentTokenCreate) submit.
                getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge().get(0);
        final String authenticatedShopperID = paymentTokenCreate.getAuthenticatedShopperID().getvalue();

        final String tokenEventReference = paymentTokenCreate.getCreateToken().getTokenEventReference();

        final Reply reply = new Reply();
        final Token token = new Token();
        final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
        intAuthenticatedShopperID.setvalue(authenticatedShopperID);
        token.setAuthenticatedShopperID(intAuthenticatedShopperID);
        token.setTokenEventReference(tokenEventReference);
        final TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setTokenEventReference(tokenEventReference);
        tokenDetails.setTokenEvent(TokenEvent.NEW.name());
        final PaymentTokenExpiry paymentTokenExpiry = new PaymentTokenExpiry();
        final LocalDateTime dateTime = LocalDateTime.now();
        final Date paymentTokenExpiryDate = getExpiryDate(dateTime);
        paymentTokenExpiry.setDate(paymentTokenExpiryDate);
        tokenDetails.setPaymentTokenExpiry(paymentTokenExpiry);
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue("Reason for token");
        tokenDetails.setTokenReason(tokenReason);

        final PaymentTokenID paymentTokenId = new PaymentTokenID();
        paymentTokenId.setvalue(UUID.randomUUID().toString());
        tokenDetails.setPaymentTokenID(paymentTokenId);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError().add(tokenDetails);
        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        final CardDetails cardDetails = new CardDetails();
        final ExpiryDate expiryDate = new ExpiryDate();
        final Date cardExpiryDate = getExpiryDate(dateTime);
        expiryDate.setDate(cardExpiryDate);
        cardDetails.setExpiryDate(expiryDate);
        final CardHolderName cardHolderName = new CardHolderName();
        cardHolderName.setvalue(CC_OWNER);
        cardDetails.setCardHolderName(cardHolderName);

        final Derived derived = new Derived();
        derived.setCardBrand(CARD_BRAND);
        derived.setCardSubBrand(CARD_SUB_BRAND);
        derived.setIssuerCountryCode(ISSUER_COUNTRY_CODE);
        derived.setObfuscatedPAN(OBFUSCATED_PAN);
        cardDetails.setDerived(derived);

        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetails().add(cardDetails);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrError().add(paymentInstrument);
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrEcheckVerificationResponseOrPaymentOptionOrToken().add(token);
        response.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        return response;
    }

    private Date getExpiryDate(final LocalDateTime dateTime) {
        final Date paymentTokenExpiryDate = new Date();
        paymentTokenExpiryDate.setDayOfMonth(String.valueOf(dateTime.getDayOfMonth()));
        paymentTokenExpiryDate.setMonth(String.valueOf(dateTime.getMonthValue()));
        paymentTokenExpiryDate.setYear(String.valueOf(dateTime.plusYears(5).getYear()));
        return paymentTokenExpiryDate;
    }
}
