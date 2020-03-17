package com.worldpay.service;

import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@IntegrationTest
public class RedirectAuthoriseServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String ORDER_CONTENT = "orderContent";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String AUTH_SHOPPER_ID = "authShopperId";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final String SESSION_ID = "sessionId";
    private static final String SHOPPER_IP_ADDRESS = "shopperIPAddress";

    private static final Address SHIPPING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final TokenRequest TOKEN_REQUEST = new TokenRequest("tokenEventReferenceNumber", "tokenReason");
    private static final Address ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final List<PaymentType> includedPaymentMethods = singletonList(ONLINE);
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Payment PAYMENT = new Cse(ENCRYPTED_DATA, SHIPPING_ADDRESS);
    private static final Session SESSION = new Session(SHOPPER_IP_ADDRESS, SESSION_ID);
    private static final Shopper SHOPPER = new Shopper(EMAIL_ADDRESS, null, null, SESSION);
    private static final StoredCredentials STORED_CREDENTIALS = new StoredCredentials(null,null, Usage.FIRST);
    private String orderCode;
    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        orderCode = String.valueOf(new Date().getTime());
        merchantInfo = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
        basicOrderInfo = new BasicOrderInfo(orderCode, "Your Order & Order desc", new Amount("100", "EUR", "2"));
    }

    protected AuthoriseRequestParameters getAuthoriseRequestParameters() {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .build();
    }

    @Test
    public void createRedirectAuthoriseRequestShouldReturnErrorWhenThereAreMissingFields() throws WorldpayException {
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(PAYMENT)
                .withShopper(SHOPPER)
                .withStoredCredentials(STORED_CREDENTIALS)
                .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        RedirectAuthoriseServiceResponse response = gateway.redirectAuthorise(request);
        assertTrue(response.isError());
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldReturnErrorWhenThereAreMissingFields() throws WorldpayException {
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withTokenRequest(TOKEN_REQUEST)
                .withPayment(PAYMENT)
                .withShopper(SHOPPER)
                .withStoredCredentials(STORED_CREDENTIALS)
                .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        RedirectAuthoriseServiceResponse response = gateway.redirectAuthorise(request);
        assertTrue(response.isError());
    }

    @Test
    public void createRedirectAuthoriseRequestShouldGetRedirectReference() throws WorldpayException {
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, null, null, null);
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withOrderContent(ORDER_CONTENT)
                .withIncludedPTs(includedPaymentMethods)
                .withShippingAddress(ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withShopper(shopper)
                .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

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
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withOrderContent(ORDER_CONTENT)
                .withIncludedPTs(includedPaymentMethods)
                .withShopper(shopper)
                .withShippingAddress(ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withTokenRequest(TOKEN_REQUEST)
                .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

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
