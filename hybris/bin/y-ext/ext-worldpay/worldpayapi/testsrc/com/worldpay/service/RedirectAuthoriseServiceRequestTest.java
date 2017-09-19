package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static com.worldpay.service.model.payment.PaymentType.VISA;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@UnitTest
public class RedirectAuthoriseServiceRequestTest {

    private static final String ORDER_CONTENT = "orderContent";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String AUTH_SHOPPER_ID = "authShopperId";
    private static final String ORDER_CODE = "orderCode";
    private static final String INSTALLATION_ID = "installationId";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final List<PaymentType> includedPaymentMethods = singletonList(ONLINE);
    private static final List<PaymentType> excludedPaymentTypes = singletonList(VISA);

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final TokenRequest TOKEN_REQUEST = new TokenRequest("tokenEventReferenceNumber", "tokenReason");
    private static final Address SHIPPING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Shopper SHOPPER = new Shopper(EMAIL_ADDRESS, null, null, null);
    private static final Shopper AUTHENTICATED_SHOPPER = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");

    private static final MerchantInfo merchantInfo = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
    private static final BasicOrderInfo basicOrderInfo = new BasicOrderInfo(ORDER_CODE, "Your Order & Order desc", AMOUNT);

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenWorldpayConfigIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(null, merchantInfo, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);
    }

    @Test
    public void createRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(WORLD_PAY_CONFIG, null, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);
    }

    @Test
    public void createRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenOrderInfoIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, null, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);
    }

    @Test
    public void shouldCreateRedirectAuthoriseRequest() {
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);
        verifyOrder(request);
        assertEquals(SHOPPER, request.getOrder().getShopper());
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenWorldpayConfigIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(null, merchantInfo, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, null, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenOrderInfoIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, null, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionWhenTokenRequestIsNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, null);
    }

    @Test
    public void shouldCreateRedirectAuthoriseRequestWithTokenRequest() {
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, INSTALLATION_ID, ORDER_CONTENT,
                includedPaymentMethods, excludedPaymentTypes, AUTHENTICATED_SHOPPER, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);

        verifyOrder(request);
        assertEquals(TOKEN_REQUEST, request.getOrder().getTokenRequest());
        assertEquals(AUTHENTICATED_SHOPPER, request.getOrder().getShopper());
    }

    private void verifyOrder(final RedirectAuthoriseServiceRequest request) {
        final Order order = request.getOrder();
        assertEquals(WORLD_PAY_CONFIG, request.getWorldpayConfig());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertNull(order.getPaymentDetails());
        assertEquals(STATEMENT_NARRATIVE, order.getStatementNarrative());
        assertEquals(BILLING_ADDRESS, order.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, order.getShippingAddress());
        assertEquals(basicOrderInfo.getDescription(), order.getDescription());
        assertEquals(AMOUNT, order.getAmount());
        assertEquals(INSTALLATION_ID, order.getInstallationId());
        assertEquals(ORDER_CODE, order.getOrderCode());
        assertEquals(ORDER_CONTENT, order.getOrderContent());
        assertTrue(order.getPaymentMethodMask().getExcludes().contains(VISA));
        assertTrue(order.getPaymentMethodMask().getIncludes().contains(ONLINE));
    }
}
