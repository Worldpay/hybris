package com.worldpay.service;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

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
                .build();
    }
}
