package com.worldpay.service;

import com.google.common.collect.ImmutableList;
import com.worldpay.data.*;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.AuthenticationRiskData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.threeds2.ShopperAccountRiskData;
import com.worldpay.data.threeds2.TransactionRiskData;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.lineItem.LineItemType;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static com.worldpay.service.model.payment.PaymentType.VISA;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@UnitTest
public class RedirectAuthoriseServiceRequestTest {

    private static final String ORDER_CONTENT = "orderContent";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String ORDER_CODE = "orderCode";
    private static final String INSTALLATION_ID = "installationId";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final String TAX_AMOUNT = "taxtAmount";
    private static final String TERMS_URL = "termsUrl";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReferenceNumber";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String SESSION_ID = "sessionId";
    private static final String SHOPPER_IP_ADDRESS = "shopperIPAddress";
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASS = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_DESC = "Your Order & Order desc";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "Postal code";
    private static final List<PaymentType> INCLUDED_PAYMENT_TYPES = singletonList(ONLINE);
    private static final List<PaymentType> EXCLUDED_PAYMENT_TYPES = singletonList(VISA);
    private static final List<String> INCLUDED_PAYMENT_TYPES_METHOD = singletonList(ONLINE.getMethodCode());
    private static final List<String> EXCLUDED_PAYMENT_TYPES_METHOD = singletonList(VISA.getMethodCode());
    private static final StoredCredentials STORED_CREDENTIALS = new StoredCredentials();
    private static final DynamicInteractionType DYNAMIC_INTERACTION_TYPE = DynamicInteractionType.ECOMMERCE;
    private static final List<PaymentMethodAttribute> PAYMENT_METHOD_ATTRIBUTE = List.of(new PaymentMethodAttribute());
    private static final String USD = "USD";
    private static final String EXPONENT = "2";
    private static final String MANDATE_TYPE = "mandateType";
    private static final String DELIVERY = "DELIVERY";
    private static final String AMOUNT = "110";
    private static final String CHECKOUT_ID = "checkoutId";

    private TokenRequest tokenRequest;
    private Address shippingAddress;
    private Address billingAddress;
    private Payment payment;
    private BasicOrderInfo basicOrderInfo;
    private Session session;
    private Shopper shopper;
    private Amount amount;
    private MerchantInfo merchantInfo;
    private ShopperFields shopperFields = new ShopperFields();
    private CustomNumericFields customNumericFields = new CustomNumericFields();
    private CustomStringFields customStringFields = new CustomStringFields();
    private Purchase purchase;
    private RiskData riskData;
    private LineItem lineItemOne;
    private LineItem lineItemTwo;
    private OrderLines orderLines;
    private FraudSightData fraudSightData;
    private BranchSpecificExtension branchSpecificExtension;
    private GuaranteedPaymentsData guaranteedPaymentsData;
    private AlternativeShippingAddress alternativeShippingAddress;

    @Before
    public void setUp() throws Exception {
        lineItemOne = createLineItem("id", "value", LineItemType.PHYSICAL, "name", "200", "PCS", "taxRate", "20", "0", 20D, "0.5");
        lineItemTwo = createLineItem("id2", "value2", LineItemType.PHYSICAL, "name2", "2000", "PCS2", "taxRate2", "200", "00", 0D, "0.5");
        final OrderLines orderLines = new OrderLines();
        orderLines.setOrderTaxAmount(TAX_AMOUNT);
        orderLines.setTermsURL(TERMS_URL);
        orderLines.setLineItems(List.of(lineItemOne, lineItemTwo));
        this.orderLines = orderLines;
        riskData = createRiskData();
        purchase = createPurchaseItem();

        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASS);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;

        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setMerchantToken(false);
        this.tokenRequest = tokenRequest;

        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode("EUR");
        amount.setValue("100");
        this.amount = amount;

        final BasicOrderInfo basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setOrderCode(ORDER_CODE);
        basicOrderInfo.setDescription(ORDER_DESC);
        basicOrderInfo.setAmount(amount);
        this.basicOrderInfo = basicOrderInfo;

        riskData = new RiskData();

        final FraudSightData fraudSightData = new FraudSightData();
        fraudSightData.setShopperFields(shopperFields);
        fraudSightData.setCustomStringFields(customStringFields);
        fraudSightData.setCustomNumericFields(customNumericFields);
        this.fraudSightData = fraudSightData;

        final BranchSpecificExtension branchSpecificExtension = new BranchSpecificExtension();
        branchSpecificExtension.setPurchase(ImmutableList.of(purchase));
        this.branchSpecificExtension = branchSpecificExtension;

        final Session session = new Session();
        session.setId(SESSION_ID);
        session.setShopperIPAddress(SHOPPER_IP_ADDRESS);
        this.session = session;

        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setSession(this.session);
        this.shopper = shopper;

        final Address address = new Address();
        address.setFirstName(NAME);
        address.setLastName(LAST_NAME);
        address.setAddress1(SHOPPER_ADDRESS_1);
        address.setAddress2(SHOPPER_ADDRESS_2);
        address.setAddress3(SHOPPER_ADDRESS_3);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);

        this.shippingAddress = address;
        this.billingAddress = address;

        final Cse cse = new Cse();
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());
        cse.setAddress(this.shippingAddress);
        cse.setEncryptedData(ENCRYPTED_DATA);
        payment = cse;

        final GuaranteedPaymentsData guaranteedPaymentsData = new GuaranteedPaymentsData();
        guaranteedPaymentsData.setUserAccount(new UserAccount());
        guaranteedPaymentsData.setFulfillmentMethodType(DELIVERY);
        guaranteedPaymentsData.setTotalShippingCost(AMOUNT);
        guaranteedPaymentsData.setSurchargeAmount(AMOUNT);
        guaranteedPaymentsData.setSecondaryAmount(AMOUNT);
        guaranteedPaymentsData.setProductDetails(Collections.emptyList());
        guaranteedPaymentsData.setDiscountCodes(Collections.emptyList());
        guaranteedPaymentsData.setMemberships(Collections.emptyList());

        this.guaranteedPaymentsData = guaranteedPaymentsData;
    }

    @Test
    public void createRedirectAuthoriseRequest_ShouldCreateRedirectAuthoriseServiceRequestCorrectly_WhenAllRequestParametersAreProvided() {
        final RedirectAuthoriseServiceRequest result = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(createRequestParameters());

        verifyOrder(result);
    }

    private void verifyOrder(final RedirectAuthoriseServiceRequest request) {
        final Order order = request.getOrder();
        assertEquals(ORDER_CODE, order.getOrderCode());
        assertEquals(ORDER_DESC, order.getDescription());
        assertEquals(amount, order.getAmount());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertNotNull(order.getPaymentDetails());
        assertEquals(payment, order.getPaymentDetails().getPayment());
        assertEquals(session, order.getPaymentDetails().getSession());
        assertEquals(STORED_CREDENTIALS, order.getPaymentDetails().getStoredCredentials());
        assertEquals(INSTALLATION_ID, order.getInstallationId());
        assertEquals(INCLUDED_PAYMENT_TYPES_METHOD, order.getPaymentMethodMask().getIncludes());
        assertEquals(EXCLUDED_PAYMENT_TYPES_METHOD, order.getPaymentMethodMask().getExcludes());
        assertEquals(ORDER_CONTENT, order.getOrderContent());
        assertEquals(shopper, order.getShopper());
        assertEquals(tokenRequest, order.getTokenRequest());
        assertEquals(shippingAddress, order.getShippingAddress());
        assertEquals(billingAddress, order.getBillingAddress());
        assertEquals(STATEMENT_NARRATIVE, order.getStatementNarrative());
        assertEquals(DYNAMIC_INTERACTION_TYPE, order.getDynamicInteractionType());
        assertEquals(PAYMENT_METHOD_ATTRIBUTE, order.getPaymentMethodAttributes());
        assertEquals(riskData, order.getRiskData());
        assertEquals(orderLines, order.getOrderLines());
        assertEquals(fraudSightData, order.getFraudSightData());
        assertEquals(branchSpecificExtension, order.getBranchSpecificExtension());
        assertEquals(MANDATE_TYPE, order.getMandateType());
        assertEquals(guaranteedPaymentsData, order.getGuaranteedPaymentsData());
        assertEquals(alternativeShippingAddress, order.getAlternativeShippingAddress());
        assertEquals(CHECKOUT_ID, order.getCheckoutId());
    }

    private AuthoriseRequestParameters createRequestParameters() {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withStoredCredentials(STORED_CREDENTIALS)
            .withInstallationId(INSTALLATION_ID)
            .withIncludedPTs(INCLUDED_PAYMENT_TYPES)
            .withExcludedPTs(EXCLUDED_PAYMENT_TYPES)
            .withOrderContent(ORDER_CONTENT)
            .withTokenRequest(tokenRequest)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DYNAMIC_INTERACTION_TYPE)
            .withPaymentMethodAttributes(PAYMENT_METHOD_ATTRIBUTE)
            .withOrderLines(orderLines)
            .withRiskData(riskData)
            .withFraudSightData(fraudSightData)
            .withLevel23Data(branchSpecificExtension)
            .withMandateType(MANDATE_TYPE)
            .withGuaranteedPaymentsData(guaranteedPaymentsData)
            .withCheckoutId(CHECKOUT_ID)
            .withAlternativeShippingAddress(alternativeShippingAddress)
            .build();
    }

    private LineItem createLineItem(final String id, final String value, final LineItemType physical, final String name, final String qunatity, final String quantityUnit, final String taxRate, final String totalAmount, final String totalDiscountAmount, final double totalTaxAmountValue, final String unitPrice) {
        final LineItem lineItem = new LineItem();
        final LineItemReference lineItemReference = new LineItemReference();
        lineItemReference.setId(id);
        lineItemReference.setValue(value);
        lineItem.setLineItemReference(lineItemReference);
        lineItem.setLineItemType(physical);
        lineItem.setName(name);
        lineItem.setQuantity(qunatity);
        lineItem.setQuantityUnit(quantityUnit);
        lineItem.setTaxRate(taxRate);
        lineItem.setTotalAmount(totalAmount);
        lineItem.setTotalDiscountAmount(totalDiscountAmount);
        lineItem.setTotalTaxAmountValue(totalTaxAmountValue);
        lineItem.setUnitPrice(unitPrice);

        return lineItem;
    }

    private RiskData createRiskData() {
        riskData = new RiskData();

        final AuthenticationRiskData authenticationRiskData = new AuthenticationRiskData();
        authenticationRiskData.setAuthenticationMethod("authenticationMethod");
        authenticationRiskData.setAuthenticationTimestamp(WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now()));
        riskData.setAuthenticationRiskData(authenticationRiskData);

        final ShopperAccountRiskData shopperAccountRiskData = new ShopperAccountRiskData();
        shopperAccountRiskData.setAddCardAttemptsLastDay("attempts");
        shopperAccountRiskData.setPreviousSuspiciousActivity("previousActivity");
        shopperAccountRiskData.setPurchasesCompletedLastSixMonths("purchasesCompletedLastSixMonths");
        shopperAccountRiskData.setShippingNameMatchesAccountName("shippingNameMatchesAccountName");
        shopperAccountRiskData.setShopperAccountAgeIndicator("shopperAccountAgeIndicator");
        shopperAccountRiskData.setShopperAccountChangeIndicator("shopperAccountAgeIndicator");
        shopperAccountRiskData.setShopperAccountCreationDate(WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now()));
        shopperAccountRiskData.setShopperAccountModificationDate(WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now()));
        riskData.setShopperAccountRiskData(shopperAccountRiskData);

        final TransactionRiskData transactionRiskData = new TransactionRiskData();
        transactionRiskData.setDeliveryEmailAddress("delivery@address.com");
        transactionRiskData.setDeliveryTimeframe("TODAY");
        transactionRiskData.setGiftCardCount("0");
        transactionRiskData.setPreOrderPurchase("20005");
        transactionRiskData.setShippingMethod("FREE");
        riskData.setTransactionRiskData(transactionRiskData);

        return riskData;
    }

    private Purchase createPurchaseItem() {
        purchase = new Purchase();
        purchase.setInvoiceReferenceNumber("invoiceNumber");
        purchase.setCustomerReference("customerReference");
        purchase.setCardAcceptorTaxId("cardAcceptorId");

        final Amount salesTaxAmount = new Amount();
        salesTaxAmount.setExponent(EXPONENT);
        salesTaxAmount.setCurrencyCode(USD);
        salesTaxAmount.setValue("10000");

        final Amount discontAmount = new Amount();
        discontAmount.setExponent(EXPONENT);
        discontAmount.setCurrencyCode(USD);
        discontAmount.setValue("1000");

        final Amount shippingAmount = new Amount();
        shippingAmount.setExponent(EXPONENT);
        shippingAmount.setCurrencyCode(USD);
        shippingAmount.setValue("0");

        final Amount dutyAmount = new Amount();
        dutyAmount.setExponent(EXPONENT);
        dutyAmount.setCurrencyCode(USD);
        dutyAmount.setValue("0");

        purchase.setSalesTax(salesTaxAmount);
        purchase.setDiscountAmount(discontAmount);
        purchase.setShippingAmount(shippingAmount);
        purchase.setDutyAmount(dutyAmount);
        purchase.setDestinationPostalCode("10200");
        purchase.setDestinationCountryCode("US");

        return purchase;
    }
}
