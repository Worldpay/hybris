package com.worldpay.service;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.threeds2.AuthenticationRiskData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.threeds2.ShopperAccountRiskData;
import com.worldpay.service.model.threeds2.TransactionRiskData;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
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
    private static final List<PaymentType> INCLUDED_PAYMENT_TYPES = singletonList(ONLINE);
    private static final List<PaymentType> EXCLUDED_PAYMENT_TYPES = singletonList(VISA);

    private static final TokenRequest TOKEN_REQUEST = new TokenRequest("tokenEventReferenceNumber", "tokenReason");
    private static final Address SHIPPING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final String SESSION_ID = "sessionId";
    private static final String SHOPPER_IP_ADDRESS = "shopperIPAddress";
    private static final Session SESSION = new Session(SHOPPER_IP_ADDRESS, SESSION_ID);
    private static final Shopper SHOPPER = new Shopper(EMAIL_ADDRESS, null, null, SESSION);
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");
    private static final Payment PAYMENT = new Cse(ENCRYPTED_DATA, SHIPPING_ADDRESS);
    private static final StoredCredentials STORED_CREDENTIALS = new StoredCredentials();
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
    private static final String ORDER_DESC = "Your Order & Order desc";
    private static final BasicOrderInfo BASIC_ORDER_INFO = new BasicOrderInfo(ORDER_CODE, ORDER_DESC, AMOUNT);
    private static final DynamicInteractionType DYNAMIC_INTERACTION_TYPE = DynamicInteractionType.ECOMMERCE;
    private static final List<PaymentMethodAttribute> PAYMENT_METHOD_ATTRIBUTE = List.of(new PaymentMethodAttribute());
    private RiskData riskData;
    private LineItem lineItemOne;
    private LineItem lineItemTwo;
    private OrderLines orderLines;

    @Before
    public void setUp() throws Exception {
        lineItemOne = createLineItem("id", "value", LineItem.LINE_ITEM_TYPE.PHYSICAL, "name", "200", "PCS", "taxRate", "20", "0", 20D, "0.5");
        lineItemTwo = createLineItem("id2", "value2", LineItem.LINE_ITEM_TYPE.PHYSICAL, "name2", "2000", "PCS2", "taxRate2", "200", "00", 0D, "0.5");
        orderLines = new OrderLines(TAX_AMOUNT, TERMS_URL, List.of(lineItemOne, lineItemTwo));
        riskData = createRiskData();
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
        assertEquals(AMOUNT, order.getAmount());
        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
        assertNotNull(order.getPaymentDetails());
        assertEquals(PAYMENT, order.getPaymentDetails().getPayment());
        assertEquals(SESSION, order.getPaymentDetails().getSession());
        assertEquals(STORED_CREDENTIALS, order.getPaymentDetails().getStoredCredentials());
        assertEquals(INSTALLATION_ID, order.getInstallationId());
        assertEquals(INCLUDED_PAYMENT_TYPES, order.getPaymentMethodMask().getIncludes());
        assertEquals(EXCLUDED_PAYMENT_TYPES, order.getPaymentMethodMask().getExcludes());
        assertEquals(ORDER_CONTENT, order.getOrderContent());
        assertEquals(SHOPPER, order.getShopper());
        assertEquals(TOKEN_REQUEST, order.getTokenRequest());
        assertEquals(SHIPPING_ADDRESS, order.getShippingAddress());
        assertEquals(BILLING_ADDRESS, order.getBillingAddress());
        assertEquals(STATEMENT_NARRATIVE, order.getStatementNarrative());
        assertEquals(DYNAMIC_INTERACTION_TYPE, order.getDynamicInteractionType());
        assertEquals(PAYMENT_METHOD_ATTRIBUTE, order.getPaymentMethodAttributes());
        assertEquals(riskData, order.getRiskData());
        assertEquals(orderLines, order.getOrderLines());
    }

    private AuthoriseRequestParameters createRequestParameters() {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(MERCHANT_INFO)
            .withOrderInfo(BASIC_ORDER_INFO)
            .withPayment(PAYMENT)
            .withShopper(SHOPPER)
            .withStoredCredentials(STORED_CREDENTIALS)
            .withInstallationId(INSTALLATION_ID)
            .withIncludedPTs(INCLUDED_PAYMENT_TYPES)
            .withExcludedPTs(EXCLUDED_PAYMENT_TYPES)
            .withOrderContent(ORDER_CONTENT)
            .withTokenRequest(TOKEN_REQUEST)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(BILLING_ADDRESS)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DYNAMIC_INTERACTION_TYPE)
            .withPaymentMethodAttributes(PAYMENT_METHOD_ATTRIBUTE)
            .withOrderLines(orderLines)
            .withRiskData(riskData)
            .build();
    }

    private LineItem createLineItem(final String id, final String value, final LineItem.LINE_ITEM_TYPE physical, final String name, final String s, final String pcs, final String taxRate, final String totalAmount, final String totalDiscountAmount, final double totalTaxAmountValue, final String unitPrice) {
        final LineItem lineItem = new LineItem();
        lineItem.setLineItemReference(new LineItemReference("id", "value"));
        lineItem.setLineItemType(LineItem.LINE_ITEM_TYPE.DISCOUNT);
        lineItem.setName("name");
        lineItem.setQuantity("200");
        lineItem.setQuantityUnit("PCS");
        lineItem.setTaxRate("taxRate");
        lineItem.setTotalAmount("20");
        lineItem.setTotalDiscountAmount("0");
        lineItem.setTotalTaxAmountValue(20D);
        lineItem.setUnitPrice("1");

        return lineItem;
    }

    private RiskData createRiskData() {
        riskData = new RiskData();

        final AuthenticationRiskData authenticationRiskData = new AuthenticationRiskData();
        authenticationRiskData.setAuthenticationMethod("authenticationMethod");
        authenticationRiskData.setAuthenticationTimestamp(new Date(LocalDateTime.now()));
        riskData.setAuthenticationRiskData(authenticationRiskData);

        final ShopperAccountRiskData shopperAccountRiskData = new ShopperAccountRiskData();
        shopperAccountRiskData.setAddCardAttemptsLastDay("attempts");
        shopperAccountRiskData.setPreviousSuspiciousActivity("previousActivity");
        shopperAccountRiskData.setPurchasesCompletedLastSixMonths("purchasesCompletedLastSixMonths");
        shopperAccountRiskData.setShippingNameMatchesAccountName("shippingNameMatchesAccountName");
        shopperAccountRiskData.setShopperAccountAgeIndicator("shopperAccountAgeIndicator");
        shopperAccountRiskData.setShopperAccountChangeIndicator("shopperAccountAgeIndicator");
        shopperAccountRiskData.setShopperAccountCreationDate(new Date(LocalDateTime.now()));
        shopperAccountRiskData.setShopperAccountModificationDate(new Date(LocalDateTime.now()));
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
}
