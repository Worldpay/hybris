package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayCommunicationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@IntegrationTest
public class RedirectAuthoriseServiceRequestIntegrationTest {

    private static final String ORDER_CONTENT = "orderContent";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String AUTH_SHOPPER_ID = "authShopperId";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final TokenRequest TOKEN_REQUEST = new TokenRequest("tokenEventReferenceNumber", "tokenReason");
    private static final Address ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final List<PaymentType> includedPaymentMethods = singletonList(ONLINE);
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");

    private WorldpayServiceGateway gateway;
    private String orderCode;
    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        gateway = WorldpayServiceGateway.getInstance();
        orderCode = String.valueOf(new Date().getTime());
        merchantInfo = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
        basicOrderInfo = new BasicOrderInfo(orderCode, "Your Order & Order desc", new Amount("100", "EUR", "2"));
    }

    @Test
    public void createRedirectAuthoriseRequestShouldRaiseWorldpayComunicationExceptionWhenValidatedByTheGatewayWhenMerchantIsNull() throws WorldpayException {
        thrown.expect(WorldpayCommunicationException.class);
        merchantInfo = new MerchantInfo(null, null);

        final Shopper shopper = new Shopper(EMAIL_ADDRESS, null, null, null);

        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPaymentMethods, null, shopper, ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);

        gateway.redirectAuthorise(request);
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldRaiseWorldpayComunicationExceptionWhenValidatedByTheGatewayWhenMerchantIsNull() throws WorldpayException {
        thrown.expect(WorldpayCommunicationException.class);
        merchantInfo = new MerchantInfo(null, null);

        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);

        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPaymentMethods, null, shopper, ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);

        gateway.redirectAuthorise(request);
    }

    @Test
    public void createRedirectAuthoriseRequestShouldRaiseWorldpayValidationExceptionWhenThereAreNullValues() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(null, merchantInfo, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldRaiseWorldpayValidationExceptionWhenThereAreNullValues() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(null, merchantInfo, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void createRedirectAuthoriseRequestShouldReturnErrorWhenThereAreMissingFields() throws WorldpayException {
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, null, null,
                null, null, null, null, null, null);

        RedirectAuthoriseServiceResponse response = gateway.redirectAuthorise(request);
        assertTrue(response.isError());
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldReturnErrorWhenThereAreMissingFields() throws WorldpayException {
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, null, null,
                null, null, null, null, null, null, TOKEN_REQUEST);

        RedirectAuthoriseServiceResponse response = gateway.redirectAuthorise(request);
        assertTrue(response.isError());
    }

    @Test
    public void createRedirectAuthoriseRequestShouldGetRedirectReference() throws WorldpayException {
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, null, null, null);
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPaymentMethods, null, shopper, ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);

        assertNull(request.getOrder().getTokenRequest());

        final RedirectAuthoriseServiceResponse redirectAuthorise = gateway.redirectAuthorise(request);

        assertNotNull("Authorise response is null!", redirectAuthorise);
        assertFalse("Errors returned from authorise request", redirectAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, redirectAuthorise.getOrderCode());
        final RedirectReference redirectReference = redirectAuthorise.getRedirectReference();
        assertNotNull("Authorise redirect reference is null!", redirectReference);
        final String url = redirectReference.getValue();
        assertNotNull("URL returned is null", url);
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldGetRedirectReferenceAndRequestTokenCreation() throws WorldpayException {
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPaymentMethods, null, shopper, ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);

        assertEquals(TOKEN_REQUEST, request.getOrder().getTokenRequest());

        final RedirectAuthoriseServiceResponse redirectAuthorise = gateway.redirectAuthorise(request);

        assertNotNull("Authorise response is null!", redirectAuthorise);
        assertFalse("Errors returned from authorise request", redirectAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, redirectAuthorise.getOrderCode());
        final RedirectReference redirectReference = redirectAuthorise.getRedirectReference();
        assertNotNull("Authorise redirect reference is null!", redirectReference);
        final String url = redirectReference.getValue();
        assertNotNull("URL returned is null", url);
    }
}
