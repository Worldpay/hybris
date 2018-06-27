package com.worldpay.service.model.payment;

import com.worldpay.service.model.Address;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.klarna.KlarnaMerchantUrls;
import com.worldpay.service.model.klarna.KlarnaPayment;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;

/**
 * Convenience class grouping methods that create all the different types of {@link Payment} objects. Provides clean interface with only the required
 * parameters for the specific payment methods to be passed
 */
public class PaymentBuilder {

    // Cards

    /**
     * Create a CSE payment
     *
     * @param encryptedData
     * @param cardAddress
     * @return Cse object
     */
    public static Cse createCSE(String encryptedData, Address cardAddress) {
        return new Cse(encryptedData, cardAddress);
    }

    /**
     * Create a Visa card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createVISASSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.VISA, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Mastercard
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createECMCSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.MASTERCARD, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a BHS card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createBHSSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.BHS, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create an IKEA card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createIKEASSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.IKEA, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create an American Express card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createAMEXSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.AMERICAN_EXPRESS, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Diners Club card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createDINERSSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.DINERS, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Carte Bancaire card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createCBSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.CARTE_BANCAIRE, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create an Airplus card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createAIRPLUSSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.AIRPLUS, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create an UATP card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createUATPSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.UATP, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Carte Bleue card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createCARTEBLEUESSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.CARTE_BLEUE, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a SOLO card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @param issueNumber
     * @param startDate
     * @return Card object
     */
    public static Card createSOLOGBSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress, String issueNumber, Date startDate) {
        return new Card(PaymentType.SOLO, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, startDate, issueNumber);
    }

    /**
     * Create a Laser card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @param startDate
     * @return Card object
     */
    public static Card createLASERSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress, Date startDate) {
        return new Card(PaymentType.LASER_CARD, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, startDate, null);
    }

    /**
     * Create a Dankort card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createDANKORTSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.DANKORT, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Discover card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createDISCOVERSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.DISCOVER, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a JCB card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createJCBSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.JCB, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create an Aurore card
     *
     * @param cardNumber
     * @param birthDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createAURORESSL(String cardNumber, Date birthDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.AURORE, cardNumber, cvc, null, cardHolderName, cardAddress, birthDate, null, null);
    }

    /**
     * Create a GE Capital card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createGECAPITALSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.GE_CAPITAL, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Maestro card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @param issueNumber
     * @param startDate
     * @return Card object
     */
    public static Card createMAESTROSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress, String issueNumber, Date startDate) {
        return new Card(PaymentType.MAESTRO, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, startDate, issueNumber);
    }

    /**
     * Create a Switch card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @param issueNumber
     * @param startDate
     * @return Card object
     */
    public static Card createSWITCHSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress, String issueNumber, Date startDate) {
        return new Card(PaymentType.SWITCH, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, startDate, issueNumber);
    }

    /**
     * Create a NCPB2B card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createNCPB2BSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.NCPB2B, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a NCP Season card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createNCPSEASONSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.NCPSEASON, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    /**
     * Create a Card card
     *
     * @param cardNumber
     * @param expiryDate
     * @param cardHolderName
     * @param cvc
     * @param cardAddress
     * @return Card object
     */
    public static Card createCARDSSL(String cardNumber, Date expiryDate, String cardHolderName, String cvc, Address cardAddress) {
        return new Card(PaymentType.CARD_SSL, cardNumber, cvc, expiryDate, cardHolderName, cardAddress, null, null, null);
    }

    // Bank Accounts

    /**
     * Create an ELV bank account
     *
     * @param accountHolderName
     * @param accountNumber
     * @param bankName
     * @param bankLocation
     * @param bankLocationId
     * @param birthDate
     * @param address
     * @return BankAccount object
     */
    public static BankAccount createELVSSL(String accountHolderName, String accountNumber, String bankName, String bankLocation, String bankLocationId, Date birthDate, Address address) {
        return new BankAccount(PaymentType.ELV, accountHolderName, accountNumber, bankName, bankLocation, bankLocationId, birthDate, address);
    }

    /**
     * Create a Payout bank account
     *
     * @return BankAccount object
     */
    public static BankAccount createPAYOUTBANK() {
        return new BankAccount(PaymentType.PAYOUT, null, null, null, null, null, null, null);
    }

    // Alternative Payment Methods

    /**
     * Create a PayPal Express alternative payment
     *
     * @param firstInBillingRun
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPAYPALEXPRESS(String firstInBillingRun, String successURL, String failureURL, String cancelURL) {
        return new AlternativePayPalPayment(PaymentType.PAYPAL, null, successURL, failureURL, cancelURL, null, firstInBillingRun);
    }

    /**
     * Create a GIRO pay alternative payment
     *
     * @param bankCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createGIROPAYSSL(String bankCode, String successURL, String failureURL, String cancelURL) {
        return new AlternativeBankCodePayment(PaymentType.GIROPAY, null, successURL, failureURL, cancelURL, null, bankCode);
    }

    /**
     * Create an IDEAL alternative payment
     *
     * @param shopperBankCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createIDEALSSL(String shopperBankCode, String successURL, String failureURL, String cancelURL) {
        return new AlternativeShopperBankCodePayment(PaymentType.IDEAL, null, successURL, failureURL, cancelURL, null, shopperBankCode);
    }

    /**
     * Create an ACH alternative payment
     *
     * @param achType
     * @param firstName
     * @param lastName
     * @param address
     * @param bankAccountType
     * @param routingNumber
     * @param accountNumber
     * @return AchPayment object
     */
    public static AchPayment createACHSSL(AchType achType, String firstName, String lastName, Address address, String bankAccountType, String routingNumber, String accountNumber) {
        return new AchPayment(PaymentType.ACH, achType, firstName, lastName, address, bankAccountType, routingNumber, accountNumber);
    }

    /**
     * Create an ABAQOOS alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createABAQOOSSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.ABAQOOS, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an ALIPAY alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createALIPAYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.ALIPAY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an ALIPAY mobile alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createALIPAYMOBILESSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.ALIPAY_MOBILE, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Baloto alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createBALOTOSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.BALOTO, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a BankLink Nordea alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createBANKLINKNORDEASSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.BANKLINK_NORDEA, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Billing Partner alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createBILLINGPARTNERSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.BILLINGPARTNER, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a CashU alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createCASHUSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.CASHU, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Dineromail 7Eleven alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createDINEROMAIL7ELEVENSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.DINEROMAIL_7ELEVEN, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Dineromail Oxxo alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createDINEROMAILOXXOSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.DINEROMAIL_OXXO, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an EKONTO alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createEKONTOSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.EKONTO, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an EPay alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createEPAYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.EPAY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an Euteller alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createEUTELLERSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.EUTELLER, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an Ewire dk alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createEWIREDKSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.EWIREDK, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an Ewire no alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createEWIRENOSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.EWIRENO, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an Ewire sl alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createEWIRESESSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.EWIRESE, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an Halcash alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createHALCASHSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.HALCASH, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an Instadebit alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createINSTADEBITSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.INSTADEBIT, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Lobanet ar alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createLOBANETARSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.LOBANET_AR, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Lobanet br alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createLOBANETBRSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.LOBANET_BR, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Lobanet cl alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createLOBANETCLSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.LOBANET_CL, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Lobanet mx alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createLOBANETMXSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.LOBANET_MX, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Lobanet pe alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createLOBANETPESSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.LOBANET_PE, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Lobanet uy alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createLOBANETUYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.LOBANET_UY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Mister Cash alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createMISTERCASHSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.MISTERCASH, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a MultiBanco alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createMULTIBANCOSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.MULTIBANCO, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Neosurf alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createNEOSURFSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.NEOSURF, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Paga alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPAGASSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.PAGA, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Paga Serve alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPAGAVERVESSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.PAGA_VERVE, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Paysafe Card alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPAYSAFECARDSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.PAYSAFECARD, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a PayU alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPAYUSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.PAYU, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a PlusPay alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPLUSPAYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.PLUSPAY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Poli alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPOLISSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.POLI, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Polinz alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPOLINZSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.POLINZ, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a PostePay alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPOSTEPAYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.POSTEPAY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Przelewy alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createPRZELEWYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.PRZELEWY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Qiwi alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createQIWISSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.QIWI, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a SID alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createSIDSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.SID, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Skrill alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createSKRILLSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.SKRILL, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Sofort alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createSOFORTSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.SOFORT, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a SporoPay alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createSPOROPAYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.SPOROPAY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Swiff alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @param pan
     * @param cvv
     * @param expiryDate
     * @return AlternativePayment object
     */
    public static AlternativePayment createSWIFFSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL, String pan, String cvv, Date expiryDate) {
        return new AlternativePanCvvPayment(PaymentType.SWIFF, shopperCountryCode, successURL, null, cancelURL, pendingURL, pan, cvv, expiryDate);
    }

    /**
     * Create a TeleIngreso alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createTELEINGRESOSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.TELEINGRESO, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a TicketSurf alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createTICKETSURFSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.TICKETSURF, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a TrustPay cz alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createTRUSTPAYCZSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.TRUSTPAY_CZ, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a TrustPay ee alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createTRUSTPAYEESSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.TRUSTPAY_EE, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a TrustPay sk alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createTRUSTPAYSKSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.TRUSTPAY_SK, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a WebMoney alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createWEBMONEYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.WEBMONEY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create a Yandex Money alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @return AlternativePayment object
     */
    public static AlternativePayment createYANDEXMONEYSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL) {
        return new AlternativePayment(PaymentType.YANDEXMONEY, shopperCountryCode, successURL, null, cancelURL, pendingURL);
    }

    /**
     * Create an AstroPay Card alternative payment
     *
     * @param shopperCountryCode
     * @param successURL
     * @param cancelURL
     * @param pendingURL
     * @param pan
     * @param cvv
     * @param expiryDate
     * @return AlternativePayment object
     */
    public static AlternativePayment createASTROPAYCARDSSL(String shopperCountryCode, String successURL, String cancelURL, String pendingURL, String pan, String cvv, Date expiryDate) {
        return new AlternativePanCvvPayment(PaymentType.ASTROPAYCARD, shopperCountryCode, successURL, null, cancelURL, pendingURL, pan, cvv, expiryDate);
    }

    /**
     * Create a VME Card alternative payment
     *
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param cardAddress
     * @return AlternativePayment object
     */
    public static AlternativePayment createVMESSL(String successURL, String failureURL, String cancelURL, Address cardAddress) {
        return new AlternativeCardAddressPayment(PaymentType.VME, null, successURL, failureURL, cancelURL, null, cardAddress);
    }

    // Envoy Transfer Payments

    /**
     * Create an AUD envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERAUDBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_AUD, shopperCountryCode);
    }

    /**
     * Create a CAD envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERCADBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_CAD, shopperCountryCode);
    }

    /**
     * Create a CHF envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERCHFBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_CHF, shopperCountryCode);
    }

    /**
     * Create a CZK envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERCZKBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_CZK, shopperCountryCode);
    }

    /**
     * Create a DKK envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERDKKBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_DKK, shopperCountryCode);
    }

    /**
     * Create an EUR envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFEREURBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_EUR, shopperCountryCode);
    }

    /**
     * Create a GBP envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERGBPBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_GBP, shopperCountryCode);
    }

    /**
     * Create an HKD envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERHKDBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_HKD, shopperCountryCode);
    }

    /**
     * Create an HUF envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERHUFBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_HUF, shopperCountryCode);
    }

    /**
     * Create a JPY envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERJPYBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_JPY, shopperCountryCode);
    }

    /**
     * Create a NOK envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERNOKBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_NOK, shopperCountryCode);
    }

    /**
     * Create a NZD envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERNZDBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_NZD, shopperCountryCode);
    }

    /**
     * Create a PLN envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERPLNBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_PLN, shopperCountryCode);
    }

    /**
     * Create a SEK envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERSEKBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_SEK, shopperCountryCode);
    }

    /**
     * Create a SGD envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERSGDBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_SGD, shopperCountryCode);
    }

    /**
     * Create a THB envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERTHBBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_THB, shopperCountryCode);
    }

    /**
     * Create an USD envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERUSDBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_USD, shopperCountryCode);
    }

    /**
     * Create a ZAR envoy transfer payment
     *
     * @param shopperCountryCode
     * @return EnvoyTransferPayment object
     */
    public static EnvoyTransferPayment createENVOYTRANSFERZARBANK(String shopperCountryCode) {
        return new EnvoyTransferPayment(PaymentType.ENVOY_TRANSFER_ZAR, shopperCountryCode);
    }

    /**
     * Creates a token with details
     *
     * @param subscriptionId Subscription id from Worldpay
     * @param cvc            cvc code of the payment method used
     * @param merchantToken  if true, the token will have "merchant" scope, otherwise, will be "shopper" scope.
     * @return Token object
     */
    public static Token createToken(final String subscriptionId, final String cvc, final boolean merchantToken) {
        if (cvc != null) {
            final CardDetails cardDetails = new CardDetails();
            cardDetails.setCvcNumber(cvc);
            return new Token(subscriptionId, cardDetails, merchantToken);
        }
        return new Token(subscriptionId, merchantToken);
    }

    /**
     * Creates a Klarna payment type
     *
     * @param purchaseCountry
     * @param shopperLocale
     * @param merchantUrls
     * @param extraMerchantData
     * @return
     */
    public static KlarnaPayment createKLARNASSL(final String purchaseCountry, final String shopperLocale, final KlarnaMerchantUrls merchantUrls, final String extraMerchantData) {
        return new KlarnaPayment(purchaseCountry, shopperLocale, merchantUrls, extraMerchantData);
    }

}
