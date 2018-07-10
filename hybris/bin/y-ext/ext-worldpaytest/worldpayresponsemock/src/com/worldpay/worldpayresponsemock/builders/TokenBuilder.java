package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.enums.token.TokenEvent;
import com.worldpay.internal.model.*;
import org.joda.time.DateTime;

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
    private static final DateTime DATE_TIME = DateTime.now();

    private String authenticatedShopperId;
    private String tokenReasonValue = TOKEN_REASON;
    private String tokenEventReference;
    private Date tokenExpiryDate;
    private String tokenId = DATE_TIME.toString();
    private String tokenEvent = TokenEvent.NEW.toString();
    private Date cardExpiryDate;
    private String cardHolderNameValue = CARDHOLDER_NAME;
    private String obfuscatedPAN = OBFUSCATED_PAN;
    private String cardBrand = VISA_SSL;
    private String cardSubBrand = CREDIT;
    private String issuerCountryCode = GB;
    private String tokenReasonForTokenDetails;
    private String tokenDetailsTokenEventReference;
    private Address cardAddress;

    private TokenBuilder() {
    }

    /**
     * Factory method to create a builder
     *
     * @return an Token builder object
     */
    public static TokenBuilder aTokenBuilder() {
        return new TokenBuilder();
    }

    /**
     * Build with this given value
     *
     * @param authenticatedShopperId
     * @return this builder
     */
    public TokenBuilder withAuthenticatedShopperId(final String authenticatedShopperId) {
        this.authenticatedShopperId = authenticatedShopperId;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param tokenReasonValue
     * @return this builder
     */
    public TokenBuilder withTokenReason(final String tokenReasonValue) {
        this.tokenReasonValue = tokenReasonValue;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param cardAddress
     * @return this builder
     */
    public TokenBuilder withCardAddress(final Address cardAddress) {
        this.cardAddress = cardAddress;
        return this;
    }


    /**
     * Build with these given values
     *
     * @param day
     * @param month
     * @param year
     * @return this builder
     */
    public TokenBuilder withTokenExpiryDate(final String day, final String month, final String year) {
        tokenExpiryDate = buildDate(day, month, year);
        return this;
    }

    /**
     * Build with this given value
     *
     * @param tokenId
     * @return this builder
     */
    public TokenBuilder withTokenId(final String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param tokenReasonForTokenDetails
     * @return this builder
     */
    public TokenBuilder withTokenDetailsTokenReason(final String tokenReasonForTokenDetails) {
        this.tokenReasonForTokenDetails = tokenReasonForTokenDetails;
        return this;
    }


    /**
     * Build with this given value
     *
     * @param tokenDetailsTokenEventReference
     * @return this builder
     */
    public TokenBuilder withTokenDetailsTokenEventReference(final String tokenDetailsTokenEventReference) {
        this.tokenDetailsTokenEventReference = tokenDetailsTokenEventReference;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param tokenEvent
     * @return this builder
     */
    public TokenBuilder withTokenEvent(final String tokenEvent) {
        this.tokenEvent = tokenEvent;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param tokenEventReference
     * @return this builder
     */
    public TokenBuilder withTokenEventReference(final String tokenEventReference) {
        this.tokenEventReference = tokenEventReference;
        return this;
    }

    /**
     * Build with these given values
     *
     * @param month
     * @param year
     * @return this builder
     */
    public TokenBuilder withCardExpiryDate(final String month, final String year) {
        cardExpiryDate = buildDate(null, month, year);
        return this;
    }

    /**
     * Build with this given value
     *
     * @param cardHolderName
     * @return this builder
     */
    public TokenBuilder withCardHolderName(final String cardHolderName) {
        this.cardHolderNameValue = cardHolderName;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param obfuscatedPAN
     * @return this builder
     */
    public TokenBuilder withObfuscatedPAN(final String obfuscatedPAN) {
        this.obfuscatedPAN = obfuscatedPAN;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param cardBrand
     * @return this builder
     */
    public TokenBuilder withCardBrand(final String cardBrand) {
        this.cardBrand = cardBrand;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param cardSubBrand
     * @return this builder
     */
    public TokenBuilder withCardSubBrand(final String cardSubBrand) {
        this.cardSubBrand = cardSubBrand;
        return this;
    }

    /**
     * Build with this given value
     *
     * @param issuerCountryCode
     * @return this builder
     */
    public TokenBuilder withIssuerCountryCode(final String issuerCountryCode) {
        this.issuerCountryCode = issuerCountryCode;
        return this;
    }


    /**
     * Build the Token object based on the builders internal state
     *
     * @return the internal Address model
     */
    public Token build() {
        final Token token = new Token();

        final TokenDetails tokenDetails = new TokenDetails();
        final TokenReason tokenDetailsReason = new TokenReason();
        tokenDetailsReason.setvalue(tokenReasonForTokenDetails);
        tokenDetails.setTokenReason(tokenDetailsReason);

        if (tokenId == null) {
            tokenId = String.valueOf(DateTime.now().getMillis());
        }
        final PaymentTokenID paymentTokenId = new PaymentTokenID();
        paymentTokenId.setvalue(tokenId);

        tokenDetails.setPaymentTokenID(paymentTokenId);
        tokenDetails.setTokenEvent(tokenEvent);
        tokenDetails.setTokenEventReference(tokenDetailsTokenEventReference);

        final PaymentTokenExpiry tokenExpiry = new PaymentTokenExpiry();
        if (tokenExpiryDate == null) {
            tokenExpiryDate = buildDate(String.valueOf(DATE_TIME.getDayOfMonth()), String.valueOf(DATE_TIME.getMonthOfYear()), String.valueOf(DATE_TIME.plusYears(10).getYear()));
        }
        tokenExpiry.setDate(tokenExpiryDate);

        tokenDetails.setPaymentTokenExpiry(tokenExpiry);

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
            cardExpiryDate = buildDate(null, String.valueOf(DATE_TIME.getMonthOfYear()), String.valueOf(DATE_TIME.plusYears(4).getYear()));
        }
        expiryDateForCard.setDate(cardExpiryDate);
        cardDetails.setExpiryDate(expiryDateForCard);

        final CardHolderName cardHolderName = new CardHolderName();
        cardHolderName.setvalue(cardHolderNameValue);
        cardDetails.setCardHolderName(cardHolderName);
        final Derived derived = new Derived();
        derived.setCardSubBrand(cardSubBrand);
        derived.setCardBrand(cardBrand);
        derived.setObfuscatedPAN(obfuscatedPAN);
        derived.setIssuerCountryCode(issuerCountryCode);

        cardDetails.setDerived(derived);

        paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().add(cardDetails);
        token.setAuthenticatedShopperID(authenticatedShopperId);
        token.setTokenEventReference(tokenEventReference);
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(tokenReasonValue);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError().add(tokenReason);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError().add(tokenDetails);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError().add(paymentInstrument);

        return token;
    }

    private Date buildDate(final String day, final String month, final String year) {
        final Date date = new Date();
        date.setDayOfMonth(day);
        date.setMonth(month);
        date.setYear(year);
        return date;
    }
}
