package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.enums.token.TokenEvent;
import com.worldpay.internal.model.*;
import org.apache.commons.lang.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static com.worldpay.worldpayresponsemock.builders.AddressBuilder.anAddressBuilder;

/**
 * Builder for the internal Token model generated from the Worldpay DTD
 */
public final class TokenBuilder {

    private static final String VISA_SSL = "VISA-SSL";
    private static final String OBFUSCATED_PAN = "4111********1111";
    private static final String CARDHOLDER_NAME = "Mr Test Test";
    private static final String CREDIT = "CREDIT";
    private static final String GB = "GB";
    private static final String TOKEN_REASON = "tokenReason";
    private static final LocalDate DATE_TIME = LocalDate.now();

    private String authenticatedShopperId;
    private String tokenReasonValue = TOKEN_REASON;
    private String tokenEventReference;
    private String tokenId = DATE_TIME.toString();
    private String tokenEvent = TokenEvent.NEW.toString();
    private String cardHolderNameValue = CARDHOLDER_NAME;
    private String obfuscatedPAN = OBFUSCATED_PAN;
    private String cardBrandCode = VISA_SSL;
    private String cardSubBrand = CREDIT;
    private String issuerCountryCode = GB;
    private String tokenReasonForTokenDetails;
    private String tokenDetailsTokenEventReference;
    private boolean paypalToken;
    private Date tokenExpiryDate;
    private Date cardExpiryDate;
    private Address cardAddress;
    private final CardBrand cardBrand;

    private TokenBuilder() {
        cardBrand = new CardBrand();
    }

    public static TokenBuilder aTokenBuilder() {
        return new TokenBuilder();
    }

    public TokenBuilder withAuthenticatedShopperId(final String authenticatedShopperId) {
        this.authenticatedShopperId = authenticatedShopperId;
        return this;
    }

    public TokenBuilder withTokenReason(final String tokenReasonValue) {
        this.tokenReasonValue = tokenReasonValue;
        return this;
    }

    public TokenBuilder withCardAddress(final Address cardAddress) {
        this.cardAddress = cardAddress;
        return this;
    }

    public TokenBuilder withTokenExpiryDate(final String day, final String month, final String year) {
        tokenExpiryDate = buildDate(day, month, year);
        return this;
    }

    public TokenBuilder withTokenId(final String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    public TokenBuilder withTokenDetailsTokenReason(final String tokenReasonForTokenDetails) {
        this.tokenReasonForTokenDetails = tokenReasonForTokenDetails;
        return this;
    }

    public TokenBuilder withTokenDetailsTokenEventReference(final String tokenDetailsTokenEventReference) {
        this.tokenDetailsTokenEventReference = tokenDetailsTokenEventReference;
        return this;
    }


    public TokenBuilder withTokenEvent(final String tokenEvent) {
        this.tokenEvent = tokenEvent;
        return this;
    }


    public TokenBuilder withTokenEventReference(final String tokenEventReference) {
        this.tokenEventReference = tokenEventReference;
        return this;
    }

    public TokenBuilder withCardExpiryDate(final String month, final String year) {
        cardExpiryDate = buildDate(null, month, year);
        return this;
    }


    public TokenBuilder withCardHolderName(final String cardHolderName) {
        this.cardHolderNameValue = cardHolderName;
        return this;
    }


    public TokenBuilder withObfuscatedPAN(final String obfuscatedPAN) {
        this.obfuscatedPAN = obfuscatedPAN;
        return this;
    }

    public TokenBuilder withCardBrand(final String cardBrand) {
        this.cardBrandCode = cardBrand;
        return this;
    }

    public TokenBuilder withCardSubBrand(final String cardSubBrand) {
        this.cardSubBrand = cardSubBrand;
        return this;
    }

    public TokenBuilder withIssuerCountryCode(final String issuerCountryCode) {
        this.issuerCountryCode = issuerCountryCode;
        return this;
    }

    public TokenBuilder withPaypalToken() {
        this.paypalToken = true;
        return this;
    }

    public Token build() {
        final Token token = new Token();

        final TokenDetails tokenDetails = new TokenDetails();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(tokenReasonForTokenDetails)) {
            final TokenReason tokenDetailsReason = new TokenReason();
            tokenDetailsReason.setvalue(tokenReasonForTokenDetails);
            tokenDetails.setTokenReason(tokenDetailsReason);
        }

        final PaymentTokenID paymentTokenId = new PaymentTokenID();
        paymentTokenId.setvalue(Optional.ofNullable(tokenId).orElseGet(() -> String.valueOf(Instant.now().toEpochMilli())));
        tokenDetails.setPaymentTokenID(paymentTokenId);
        Optional.ofNullable(tokenEvent).ifPresent(tokenDetails::setTokenEvent);
        Optional.ofNullable(tokenDetailsTokenEventReference).ifPresent(tokenDetails::setTokenEventReference);

        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        if (Objects.isNull(tokenExpiryDate) || StringUtils.isEmpty(tokenExpiryDate.getYear()) || StringUtils.isEmpty(tokenExpiryDate.getDayOfMonth()) || StringUtils.isEmpty(tokenExpiryDate.getMonth())) {
            tokenExpiryDate = buildDate(String.valueOf(LocalDate.from(DATE_TIME).getDayOfMonth()), String.valueOf(LocalDate.from(DATE_TIME).getMonthValue()), String.valueOf(LocalDate.from(DATE_TIME).plusYears(10).getYear()));
        }
        tokenExpiry.setDate(tokenExpiryDate);

        tokenDetails.setPaymentTokenExpiry(tokenExpiry);

        Optional.ofNullable(authenticatedShopperId).map(shopperId -> {
            final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
            intAuthenticatedShopperID.setvalue(shopperId);
            return intAuthenticatedShopperID;
        }).ifPresent(token::setAuthenticatedShopperID);
        Optional.ofNullable(tokenEventReference).ifPresent(token::setTokenEventReference);
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(tokenReasonValue);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError().add(tokenReason);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError().add(tokenDetails);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrSchemeResponseOrSelectedSchemeOrError().add(createPaymentInstrument());

        return token;
    }

    private PaymentInstrument createPaymentInstrument() {
        if (paypalToken) {
            return addPaypalDetails();
        } else {
            return addCardDetails();
        }
    }

    private PaymentInstrument addPaypalDetails() {
        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        final Paypal paypal = new Paypal();
        paypal.setvalue(StringUtils.EMPTY);
        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(paypal);
        return paymentInstrument;
    }

    private PaymentInstrument addCardDetails() {
        final PaymentInstrument paymentInstrument = new PaymentInstrument();

        final CardDetails cardDetails = new CardDetails();
        final CardAddress cardDetailsAddress = new CardAddress();
        if (cardAddress == null) {
            cardAddress = anAddressBuilder().build();
        }
        cardDetailsAddress.setAddress(cardAddress);
        cardDetails.setCardAddress(cardDetailsAddress);

        final ExpiryDate expiryDateForCard = new ExpiryDate();
        if (cardExpiryDate == null) {
            cardExpiryDate = buildDate(null, String.valueOf(LocalDate.from(DATE_TIME).getMonthValue()), String.valueOf(LocalDate.from(DATE_TIME).plusYears(4).getYear()));
        }
        expiryDateForCard.setDate(cardExpiryDate);
        cardDetails.setExpiryDate(expiryDateForCard);

        final CardHolderName cardHolderName = new CardHolderName();
        cardHolderName.setvalue(cardHolderNameValue);
        cardDetails.setCardHolderName(cardHolderName);
        final Derived derived = new Derived();
        derived.setCardSubBrand(cardSubBrand);
        cardBrand.setvalue(cardBrandCode);
        derived.setCardBrand(cardBrand);
        derived.setObfuscatedPAN(obfuscatedPAN);
        derived.setIssuerCountryCode(issuerCountryCode);

        cardDetails.setDerived(derived);

        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(cardDetails);
        return paymentInstrument;
    }

    private Date buildDate(final String day, final String month, final String year) {
        final Date date = new Date();
        date.setDayOfMonth(day);
        date.setMonth(month);
        date.setYear(year);
        return date;
    }
}
