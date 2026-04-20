package com.worldpay.worldpayresponsemock.form;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ResponseForm implements java.io.Serializable {

    private String selectedPaymentMethod;
    private String transactionAmount;
    private String currencyCode;
    private String cardHolderName;
    private String worldpayOrderCode;
    private Integer exponent;
    private String currentMonth;
    private String riskValue;
    private String cardYear;
    private String cardMonth;
    private String currentDay;
    private String currentYear;
    private String responseType;
    private String merchantCode;
    private Integer responseCode;
    private String responseDescription;
    private String testCreditCard;
    private String ccPaymentType;
    private String apmPaymentType;
    private String finalScore;
    private String lastEvent;
    private String selectedRiskScore;
    private String journalType;
    private String aavAddress;
    private String aavCardholderName;
    private String aavEmail;
    private String aavPostcode;
    private String aavTelephone;
    private String authenticatedShopperId;
    private String tokenEventReference;
    private String tokenReason;
    private String tokenEvent;
    private String paymentTokenId;
    private String tokenExpiryDay;
    private String tokenExpiryMonth;
    private String tokenExpiryYear;
    private String tokenDetailsEventReference;
    private String tokenDetailsReason;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private String tokenCardHolderName;
    private String lastName;
    private String address1;
    private String address2;
    private String address3;
    private String postalCode;
    private String city;
    private String countryCode;
    private String cardSubBrand;
    private String cardBrand;
    private String issuerCountry;
    private String obfuscatedPAN;
    private String selectToken;
    private String webformId;
    private String paymentId;
    private String webformStatus;
    private String refundReason;
    private String webformURL;
    private String refundId;
    private String reference;
    private boolean merchantToken;
    private String selectStoredCredentials;
    private String transactionIdentifier;
    private String paypalToken;
    private boolean useFraudSight;
    private double fraudSightScore;
    private String fraudSightMessage;
    private Set<String> fraudSightReasonCodes;
    private boolean useGuaranteedPayments;
    private double guaranteedPaymentsScore;
    private String guaranteedPaymentsMessage;
    private String guaranteedPaymentsTriggeredRules;

}
