package com.worldpay.worldpayresponsemock.builders;


import com.worldpay.internal.model.AVSResultCode;
import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.AuthorisationId;
import com.worldpay.internal.model.Balance;
import com.worldpay.internal.model.CVCResultCode;
import com.worldpay.internal.model.Card;
import com.worldpay.internal.model.CardHolderName;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.ExpiryDate;
import com.worldpay.internal.model.ISO8583ReturnCode;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentMethodDetail;
import com.worldpay.internal.model.RiskScore;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

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

    private String cardNumber = TEST_CREDIT_CARD;
    private String cardType = CREDIT_CARD;
    private String expiryMonth = String.valueOf(DATE.getMonthOfYear());
    private String expiryYear = String.valueOf(DATE.plusYears(3).getYear());
    private String cardHolderName = DEFAULT_CARD_HOLDER_NAME;
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
    public PaymentBuilder withCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    /**
     * Build with this given value
     * @param cardType
     * @return this builder
     */
    public PaymentBuilder withCardType(String cardType) {
        this.cardType = cardType;
        return this;
    }

    /**
     * Build with this given value
     * @param expiryMonth
     * @return this builder
     */
    public PaymentBuilder withExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
        return this;
    }

    /**
     * Build with this given value
     * @param expiryYear
     * @return this builder
     */
    public PaymentBuilder withExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
        return this;
    }

    /**
     * Build with this given value
     * @param cardHolderName
     * @return this builder
     */
    public PaymentBuilder withCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
        return this;
    }

    /**
     * Build with this given value
     * @param lastEvent
     * @return this builder
     */
    public PaymentBuilder withLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
        return this;
    }

    /**
     * Build with this given value
     * @param selectedRiskScore
     * @return this builder
     */
    public PaymentBuilder withSelectedRiskScore(String selectedRiskScore) {
        this.selectedRiskScore = selectedRiskScore;
        return this;
    }

    /**
     * Build with this given value
     * @param riskValue
     * @return this builder
     */
    public PaymentBuilder withRiskValue(String riskValue) {
        this.riskValue = riskValue;
        return this;
    }

    /**
     * Build with this given value
     * @param finalScore
     * @return
     */
    public PaymentBuilder withFinalScore(String finalScore) {
        this.finalScore = finalScore;
        return this;
    }

    /**
     * Build with this given value
     * @param paymentMethod
     * @return this builder
     */
    public PaymentBuilder withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    /**
     * Build with this given value
     * @param creditCardPaymentMethod
     * @return this builder
     */
    public PaymentBuilder withCreditCardPaymentMethod(String creditCardPaymentMethod) {
        this.creditCardPaymentMethod = creditCardPaymentMethod;
        return this;
    }

    /**
     * Build with this given value
     * @param apmPaymentMethod
     * @return this builder
     */
    public PaymentBuilder withApmPaymentMethod(String apmPaymentMethod) {
        this.apmPaymentMethod = apmPaymentMethod;
        return this;
    }

    /**
     * Build with this given value
     * @param transactionAmount
     * @return this builder
     */
    public PaymentBuilder withTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    /**
     * Build with this given value
     * @param currencyCode
     * @return this builder
     */
    public PaymentBuilder withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    /**
     * Build with this given value
     * @param exponent
     * @return this builder
     */
    public PaymentBuilder withExponent(String exponent) {
        this.exponent = exponent;
        return this;
    }

    /**
     * Build with this given value
     * @param responseCode
     * @return this builder
     */
    public PaymentBuilder withResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    /**
     * Build with this given value
     * @param responseCodeDescription
     * @return this builder
     */
    public PaymentBuilder withResponseCodeDescription(String responseCodeDescription) {
        this.responseCodeDescription = responseCodeDescription;
        return this;
    }

    /**
     * Build with this given value
     * @param refundReference
     * @return this builder
     */
    public PaymentBuilder withRefundReference(String refundReference) {
        this.refundReference = refundReference;
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
            cardHolderName.setvalue(this.cardHolderName);
            payment.setCardHolderName(cardHolderName);

            payment.setIssuerCountryCode("CH");
            payment.setPaymentMethod(this.creditCardPaymentMethod);
        }
        if (!StringUtils.isBlank(this.refundReference)) {
            payment.setRefundReference(this.refundReference);
        }

        if (StringUtils.isNotBlank(selectedRiskScore)) {
            RiskScore riskScore = new RiskScore();
            if (equalsIgnoreCase(selectedRiskScore, RMM)) {
                riskScore.setValue(this.riskValue);
            } else if (equalsIgnoreCase(selectedRiskScore, RG)) {
                riskScore.setFinalScore(this.finalScore);
            }
            payment.setRiskScore(riskScore);
        }

        final Amount amount = AmountBuilder.anAmountBuilder().withExponent(this.exponent).withAmount(this.transactionAmount).withCurrencyCode(this.currencyCode).build();
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
        return payment;
    }
}
