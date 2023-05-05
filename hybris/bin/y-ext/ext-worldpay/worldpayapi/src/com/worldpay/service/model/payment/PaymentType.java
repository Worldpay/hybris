package com.worldpay.service.model.payment;

import com.worldpay.internal.model.*;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representation of all the payment types. Note the special "ALL" PaymentType which can be used in the {@link PaymentMethodMask} includes to include all
 * the payment methods
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods to create specific Payment objects
 */
public enum PaymentType implements Serializable {

    ONLINE("ONLINE", null),
    VISA("VISA-SSL", VISASSL.class),
    MASTERCARD("ECMC-SSL", ECMCSSL.class),
    BHS("BHS-SSL", BHSSSL.class),
    IKEA("IKEA-SSL", IKEASSL.class),
    AMERICAN_EXPRESS("AMEX-SSL", AMEXSSL.class),
    ELV("ELV-SSL", ELVSSL.class),
    DINERS("DINERS-SSL", DINERSSSL.class),
    CARTE_BANCAIRE("CB-SSL", CBSSL.class),
    AIRPLUS("AIRPLUS-SSL", AIRPLUSSSL.class),
    UATP("UATP-SSL", UATPSSL.class),
    CARTE_BLEUE("CARTEBLEUE-SSL", CARTEBLEUESSL.class),
    SOLO("SOLO_GB-SSL", SOLOGBSSL.class),
    LASER_CARD("LASER-SSL", LASERSSL.class),
    DANKORT("DANKORT-SSL", DANKORTSSL.class),
    DISCOVER("DISCOVER-SSL", DISCOVERSSL.class),
    JCB("JCB-SSL", JCBSSL.class),
    AURORE("AURORE-SSL", AURORESSL.class),
    GE_CAPITAL("GECAPITAL-SSL", GECAPITALSSL.class),
    PAYOUT("PAYOUT-BANK", PAYOUTBANK.class),
    PAYPAL("PAYPAL-EXPRESS", PAYPALEXPRESS.class),
    GIROPAY("GIROPAY-SSL", GIROPAYSSL.class),
    MAESTRO("MAESTRO-SSL", MAESTROSSL.class),
    SWITCH("SWITCH-SSL", SWITCHSSL.class),
    NCPB2B("NCPB2B-SSL", NCPB2BSSL.class),
    NCPSEASON("NCPSEASON-SSL", NCPSEASONSSL.class),
    IDEAL("IDEAL-SSL", IDEALSSL.class),
    ACH("ACH-SSL", ACHSSL.class),
    CARD_SSL("CARD-SSL", CARDSSL.class),
    ABAQOOS("ABAQOOS-SSL", ABAQOOSSSL.class),
    ALIPAY("ALIPAY-SSL", ALIPAYSSL.class),
    ALIPAY_MOBILE("ALIPAYMOBILE-SSL", ALIPAYMOBILESSL.class),
    BALOTO("BALOTO-SSL", BALOTOSSL.class),
    BILLINGPARTNER("BILLINGPARTNER-SSL", BILLINGPARTNERSSL.class),
    CASHU("CASHU-SSL", CASHUSSL.class),
    DINEROMAIL_7ELEVEN("DINEROMAIL_7ELEVEN-SSL", DINEROMAIL7ELEVENSSL.class),
    DINEROMAIL_OXXO("DINEROMAIL_OXXO-SSL", DINEROMAILOXXOSSL.class),
    EKONTO("EKONTO-SSL", EKONTOSSL.class),
    EPAY("EPAY-SSL", EPAYSSL.class),
    EUTELLER("EUTELLER-SSL", EUTELLERSSL.class),
    EWIREDK("EWIREDK-SSL", EWIREDKSSL.class),
    EWIRENO("EWIRENO-SSL", EWIRENOSSL.class),
    EWIRESE("EWIRESE-SSL", EWIRESESSL.class),
    HALCASH("HALCASH-SSL", HALCASHSSL.class),
    INSTADEBIT("INSTADEBIT-SSL", INSTADEBITSSL.class),
    LOBANET_AR("LOBANET_AR-SSL", LOBANETARSSL.class),
    LOBANET_BR("LOBANET_BR-SSL", LOBANETBRSSL.class),
    LOBANET_CL("LOBANET_CL-SSL", LOBANETCLSSL.class),
    LOBANET_MX("LOBANET_MX-SSL", LOBANETMXSSL.class),
    LOBANET_PE("LOBANET_PE-SSL", LOBANETPESSL.class),
    LOBANET_UY("LOBANET_UY-SSL", LOBANETUYSSL.class),
    MISTERCASH("MISTERCASH-SSL", MISTERCASHSSL.class),
    MULTIBANCO("MULTIBANCO-SSL", MULTIBANCOSSL.class),
    NEOSURF("NEOSURF-SSL", NEOSURFSSL.class),
    PAGA("PAGA-SSL", PAGASSL.class),
    PAGA_VERVE("PAGA_VERVE-SSL", PAGAVERVESSL.class),
    PAYSAFECARD("PAYSAFECARD-SSL", PAYSAFECARDSSL.class),
    PAYU("PAYU-SSL", PAYUSSL.class),
    PLUSPAY("PLUSPAY-SSL", PLUSPAYSSL.class),
    POLI("POLI-SSL", POLISSL.class),
    POLINZ("POLINZ-SSL", POLINZSSL.class),
    PRZELEWY("PRZELEWY-SSL", PRZELEWYSSL.class),
    QIWI("QIWI-SSL", QIWISSL.class),
    SID("SID-SSL", SIDSSL.class),
    SKRILL("SKRILL-SSL", SKRILLSSL.class),
    SOFORT("SOFORT-SSL", SOFORTSSL.class),
    SOFORTCH("SOFORT_CH-SSL", SOFORTCHSSL.class),
    SPOROPAY("SPOROPAY-SSL", SPOROPAYSSL.class),
    SWIFF("SWIFF-SSL", SWIFFSSL.class),
    TELEINGRESO("TELEINGRESO-SSL", TELEINGRESOSSL.class),
    TICKETSURF("TICKETSURF-SSL", TICKETSURFSSL.class),
    TRUSTPAY_CZ("TRUSTPAY_CZ-SSL", TRUSTPAYCZSSL.class),
    TRUSTPAY_EE("TRUSTPAY_EE-SSL", TRUSTPAYEESSL.class),
    TRUSTPAY_SK("TRUSTPAY_SK-SSL", TRUSTPAYSKSSL.class),
    WEBMONEY("WEBMONEY-SSL", WEBMONEYSSL.class),
    YANDEXMONEY("YANDEXMONEY-SSL", YANDEXMONEYSSL.class),
    ASTROPAYCARD("ASTROPAYCARD-SSL", ASTROPAYCARDSSL.class),
    BANCOSANTANDER("BANCOSANTANDER-SSL", BANCOSANTANDERSSL.class),
    BOLETO("BOLETO-SSL", BOLETOSSL.class),
    MONETA("MONETA-SSL", MONETASSL.class),
    SEPA("SEPA_DIRECT_DEBIT-SSL", SEPADIRECTDEBITSSL.class),
    TODITOCARD("TODITOCARD-SSL", TODITOCARDSSL.class),
    CHINA_UNION_PAY("CHINAUNIONPAY-SSL", CHINAUNIONPAYSSL.class),
    CSEDATA("CSE-DATA", com.worldpay.internal.model.CSEDATA.class),
    TOKENSSL("TOKEN-SSL", com.worldpay.internal.model.TOKENSSL.class),
    KLARNAV2SSL("KLARNA_V2-SSL", com.worldpay.internal.model.KLARNAV2SSL.class),
    KLARNAPAYLATERSSL("KLARNA_PAYLATER-SSL", com.worldpay.internal.model.KLARNAPAYLATERSSL.class),
    KLARNASLICESSL("KLARNA_SLICEIT-SSL", com.worldpay.internal.model.KLARNASLICEITSSL.class),
    KLARNAPAYNOWSSL("KLARNA_PAYNOW-SSL", com.worldpay.internal.model.KLARNAPAYNOWSSL.class),
    APPLEPAYSSL("APPLEPAY-SSL", com.worldpay.internal.model.APPLEPAYSSL.class),
    PAYWITHGOOGLESSL("PAYWITHGOOGLE-SSL", com.worldpay.internal.model.PAYWITHGOOGLESSL.class),
    ENVOY_TRANSFER_AUD("ENVOY_TRANSFER_AUD-BANK", ENVOYTRANSFERAUDBANK.class),
    ENVOY_TRANSFER_CAD("ENVOY_TRANSFER_CAD-BANK", ENVOYTRANSFERCADBANK.class),
    ENVOY_TRANSFER_CHF("ENVOY_TRANSFER_CHF-BANK", ENVOYTRANSFERCHFBANK.class),
    ENVOY_TRANSFER_CZK("ENVOY_TRANSFER_CZK-BANK", ENVOYTRANSFERCZKBANK.class),
    ENVOY_TRANSFER_DKK("ENVOY_TRANSFER_DKK-BANK", ENVOYTRANSFERDKKBANK.class),
    ENVOY_TRANSFER_EUR("ENVOY_TRANSFER_EUR-BANK", ENVOYTRANSFEREURBANK.class),
    ENVOY_TRANSFER_GBP("ENVOY_TRANSFER_GBP-BANK", ENVOYTRANSFERGBPBANK.class),
    ENVOY_TRANSFER_HKD("ENVOY_TRANSFER_HKD-BANK", ENVOYTRANSFERHKDBANK.class),
    ENVOY_TRANSFER_HUF("ENVOY_TRANSFER_HUF-BANK", ENVOYTRANSFERHUFBANK.class),
    ENVOY_TRANSFER_JPY("ENVOY_TRANSFER_JPY-BANK", ENVOYTRANSFERJPYBANK.class),
    ENVOY_TRANSFER_NOK("ENVOY_TRANSFER_NOK-BANK", ENVOYTRANSFERNOKBANK.class),
    ENVOY_TRANSFER_NZD("ENVOY_TRANSFER_NZD-BANK", ENVOYTRANSFERNZDBANK.class),
    ENVOY_TRANSFER_PLN("ENVOY_TRANSFER_PLN-BANK", ENVOYTRANSFERPLNBANK.class),
    ENVOY_TRANSFER_SEK("ENVOY_TRANSFER_SEK-BANK", ENVOYTRANSFERSEKBANK.class),
    ENVOY_TRANSFER_SGD("ENVOY_TRANSFER_SGD-BANK", ENVOYTRANSFERSGDBANK.class),
    ENVOY_TRANSFER_THB("ENVOY_TRANSFER_THB-BANK", ENVOYTRANSFERTHBBANK.class),
    ENVOY_TRANSFER_USD("ENVOY_TRANSFER_USD-BANK", ENVOYTRANSFERUSDBANK.class),
    ENVOY_TRANSFER_ZAR("ENVOY_TRANSFER_ZAR-BANK", ENVOYTRANSFERZARBANK.class);

    private static final Map<String, PaymentType> lookup = new HashMap<>();

    static {
        for (PaymentType pt : EnumSet.allOf(PaymentType.class)) {
            lookup.put(pt.getMethodCode().toUpperCase(), pt);
        }
    }

    private String methodCode;
    private Class<?> modelClass;

    // Private constructor ensures that these are the only payment types that can be used
    PaymentType(final String methodCode, final Class<?> modelClass) {
        this.methodCode = methodCode;
        this.modelClass = modelClass;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    /**
     * Lookup the enum representation of a payment type
     *
     * @param methodCode to be looked up
     * @return PaymentType representation of the supplied methodCode, or null if it can't be found
     */
    public static PaymentType getPaymentType(final String methodCode) {
        return methodCode == null ? null : lookup.get(methodCode.toUpperCase());
    }

}
