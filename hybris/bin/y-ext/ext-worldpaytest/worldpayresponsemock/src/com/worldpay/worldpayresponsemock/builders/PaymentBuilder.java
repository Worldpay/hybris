package com.worldpay.worldpayresponsemock.builders;


import com.worldpay.internal.model.*;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * Builder for the internal Payment model generated from the Worldpay DTD
 */
public final class PaymentBuilder {

    private static final String TEST_CREDIT_CARD = "4111********1111";
    private static final String CREDIT_CARD = "creditCard";
    private static final String IN_PROCESS_AUTHORISED = "IN_PROCESS_AUTHORISED";
    private static final String RMM = "RMM";
    private static final String RG = "RG";
    private static final String CC = "CC";
    private static final String VISA_SSL = "VISA-SSL";
    private static final LocalDate DATE = LocalDate.now();
    private static final String DEFAULT_CARD_HOLDER_NAME = "cardHolderName";
    private static final String RESPONSE_CODE_DESCRIPTION = "REPEAT OF LAST TRANSACTION";
    private static final String RESPONSE_CODE = "19";
    private static final String DEFAULT_CURRENCY_CODE = "GBP";
    private static final String DEFAULT_RISK_SCORE = "1.00";
    private static final String NO_RISK_SCORE = "NoRisk";

    private String cardNumber = TEST_CREDIT_CARD;
    private String cardType = CREDIT_CARD;
    private String expiryMonth = String.valueOf(DATE.getMonth());
    private String expiryYear = String.valueOf(DATE.plusYears(3).getYear());
    private String cardHolderNameValue = DEFAULT_CARD_HOLDER_NAME;
    private String lastEvent;
    private String paymentMethod = CC;
    private String transactionAmount;
    private String currencyCode = DEFAULT_CURRENCY_CODE;
    private String exponent;
    private String responseCode = RESPONSE_CODE;
    private String responseCodeDescription = RESPONSE_CODE_DESCRIPTION;
    private String selectedRiskScore = RMM;
    private String riskValue = DEFAULT_RISK_SCORE;
    private String finalScore;
    private String creditCardPaymentMethod = VISA_SSL;
    private String apmPaymentMethod;
    private String refundReference;
    private String transactionIdentifier;

    private PaymentBuilder() {
    }

    /**
     * Factory method to create a builder
     * @return an Payment builder object
     */
    public static PaymentBuilder aPaymentBuilder() {
        return new PaymentBuilder();
    }

    /**
     * Build with this given value
     * @param cardNumber
     * @return this builder
     */
    public PaymentBuilder withCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    /**
     * Build with this given value
     * @param cardType
     * @return this builder
     */
    public PaymentBuilder withCardType(final String cardType) {
        this.cardType = cardType;
        return this;
    }

    /**
     * Build with this given value
     * @param expiryMonth
     * @return this builder
     */
    public PaymentBuilder withExpiryMonth(final String expiryMonth) {
        this.expiryMonth = expiryMonth;
        return this;
    }

    /**
     * Build with this given value
     * @param expiryYear
     * @return this builder
     */
    public PaymentBuilder withExpiryYear(final String expiryYear) {
        this.expiryYear = expiryYear;
        return this;
    }

    /**
     * Build with this given value
     * @param cardHolderName
     * @return this builder
     */
    public PaymentBuilder withCardHolderName(final String cardHolderName) {
        this.cardHolderNameValue = cardHolderName;
        return this;
    }

    /**
     * Build with this given value
     * @param lastEvent
     * @return this builder
     */
    public PaymentBuilder withLastEvent(final String lastEvent) {
        this.lastEvent = lastEvent;
        return this;
    }

    /**
     * Build with this given value
     * @param selectedRiskScore
     * @return this builder
     */
    public PaymentBuilder withSelectedRiskScore(final String selectedRiskScore) {
        this.selectedRiskScore = selectedRiskScore;
        return this;
    }

    /**
     * Build with this given value
     * @param riskValue
     * @return this builder
     */
    public PaymentBuilder withRiskValue(final String riskValue) {
        this.riskValue = riskValue;
        return this;
    }

    /**
     * Build with this given value
     * @param finalScore
     * @return
     */
    public PaymentBuilder withFinalScore(final String finalScore) {
        this.finalScore = finalScore;
        return this;
    }

    /**
     * Build with this given value
     * @param paymentMethod
     * @return this builder
     */
    public PaymentBuilder withPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    /**
     * Build with this given value
     * @param creditCardPaymentMethod
     * @return this builder
     */
    public PaymentBuilder withCreditCardPaymentMethod(final String creditCardPaymentMethod) {
        this.creditCardPaymentMethod = creditCardPaymentMethod;
        return this;
    }

    /**
     * Build with this given value
     * @param apmPaymentMethod
     * @return this builder
     */
    public PaymentBuilder withApmPaymentMethod(final String apmPaymentMethod) {
        this.apmPaymentMethod = apmPaymentMethod;
        return this;
    }

    /**
     * Build with this given value
     * @param transactionAmount
     * @return this builder
     */
    public PaymentBuilder withTransactionAmount(final String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    /**
     * Build with this given value
     * @param currencyCode
     * @return this builder
     */
    public PaymentBuilder withCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    /**
     * Build with this given value
     * @param exponent
     * @return this builder
     */
    public PaymentBuilder withExponent(final String exponent) {
        this.exponent = exponent;
        return this;
    }

    /**
     * Build with this given value
     * @param responseCode
     * @return this builder
     */
    public PaymentBuilder withResponseCode(final String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    /**
     * Build with this given value
     * @param responseCodeDescription
     * @return this builder
     */
    public PaymentBuilder withResponseCodeDescription(final String responseCodeDescription) {
        this.responseCodeDescription = responseCodeDescription;
        return this;
    }

    /**
     * Build with this given value
     * @param refundReference
     * @return this builder
     */
    public PaymentBuilder withRefundReference(final String refundReference) {
        this.refundReference = refundReference;
        return this;
    }

    /**
     * Build with this given value
     * @param transactionIdentifier
     * @return this builder
     */
    public PaymentBuilder withTransactionIdentifier(final String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
        return this;
    }

    /**
     * Build the Payment object based on the builders internal state
     * @return the internal Payment model
     */
    public Payment build() {
        final Payment payment = new Payment();

        if (!paymentMethod.equalsIgnoreCase(CC)) {
            payment.setPaymentMethod(this.apmPaymentMethod);
        } else {

            final PaymentMethodDetail paymentMethodDetail = new PaymentMethodDetail();
            final Card card = new Card();

            final Date date = new Date();
            date.setMonth(this.expiryMonth);
            date.setYear(this.expiryYear);
            final ExpiryDate expiryDate = new ExpiryDate();
            expiryDate.setDate(date);

            card.setExpiryDate(expiryDate);
            card.setNumber(this.cardNumber);
            card.setType(cardType);
            paymentMethodDetail.setCard(card);

            payment.setPaymentMethodDetail(paymentMethodDetail);

            final AuthorisationId authorisationId = new AuthorisationId();
            authorisationId.setBy("authorisedBy");
            authorisationId.setId("authorisationId");
            payment.setAuthorisationId(authorisationId);

            final CVCResultCode cvcResultCode = new CVCResultCode();
            cvcResultCode.getDescription().add("111");
            payment.setCVCResultCode(cvcResultCode);
            final AVSResultCode avsResultCode = new AVSResultCode();
            avsResultCode.getDescription().add("111");
            payment.setAVSResultCode(avsResultCode);

            final CardHolderName cardHolderName = new CardHolderName();
            cardHolderName.setvalue(this.cardHolderNameValue);
            payment.setCardHolderName(cardHolderName);

            payment.setIssuerCountryCode("CH");
            payment.setPaymentMethod(this.creditCardPaymentMethod);
        }
        if (!StringUtils.isBlank(this.refundReference)) {
            payment.setRefundReference(this.refundReference);
        }

        if (!StringUtils.equalsIgnoreCase(selectedRiskScore, NO_RISK_SCORE)) {
            final RiskScore riskScore = new RiskScore();
            if (equalsIgnoreCase(selectedRiskScore, RMM)) {
                riskScore.setValue(this.riskValue);
            } else if (equalsIgnoreCase(selectedRiskScore, RG)) {
                riskScore.setFinalScore(this.finalScore);
            }
            payment.setRiskScore(riskScore);
        }

        final Amount amount = AmountBuilder.anAmountBuilder()
                .withExponent(this.exponent)
                .withAmount(this.transactionAmount)
                .withCurrencyCode(this.currencyCode)
                .build();
        payment.setAmount(amount);

        final Balance balance = new Balance();
        balance.setAccountType(IN_PROCESS_AUTHORISED);
        balance.setAmount(amount);
        payment.getBalance().add(balance);

        final ISO8583ReturnCode iso8583ReturnCode = new ISO8583ReturnCode();
        iso8583ReturnCode.setCode(String.valueOf(responseCode));
        iso8583ReturnCode.setDescription(responseCodeDescription);
        payment.setISO8583ReturnCode(iso8583ReturnCode);

        payment.setLastEvent(this.lastEvent);

        final SchemeResponse schemeResponse = new SchemeResponse();
        schemeResponse.setTransactionIdentifier(transactionIdentifier);
        payment.setSchemeResponse(schemeResponse);

        return payment;
    }
}
