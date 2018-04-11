package com.worldpay.service;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayCommunicationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static org.junit.Assert.*;

@IntegrationTest
public class DirectAuthoriseServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String SHOPPER_EMAIL = "jshopper@myprovider.com";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String FAKED_PA_RESPONSE = "fakedPaResponse";

    private static final Address SHIPPING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final com.worldpay.service.model.Date EXPIRY_DATE = new com.worldpay.service.model.Date(LocalDateTime.now().plusYears(1));
    private static final Session SESSION = new Session("192.168.1.1", "sessionId1234");
    private static final Browser BROWSER = new Browser("text/html,application/xhtml+xml,application/xml;q=0. 9,*/*;q=0.8", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)", "0");
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Shopper SHOPPER = new Shopper(SHOPPER_EMAIL, null, BROWSER, SESSION);

    private String orderCode;
    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway testObj;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        merchantInfo = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
        orderCode = String.valueOf(new java.util.Date().getTime());
        basicOrderInfo = new BasicOrderInfo(orderCode, "Your Order & Order desc", new Amount("100", "EUR", "2"));
    }

    @Test
    public void createDirectAuthorisedWithoutMerchantShouldRaiseWorldpayCommunicationException() throws WorldpayException {
        thrown.expect(WorldpayCommunicationException.class);
        merchantInfo = new MerchantInfo(null, null);
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        testObj.directAuthorise(request);
    }

    @Test
    public void createDirectAuthorisedWithTokenRequestWithoutMerchantShouldRaiseWorldpayCommunicationException() throws WorldpayException {
        thrown.expect(WorldpayCommunicationException.class);
        merchantInfo = new MerchantInfo(null, null);
        final Token tokenSsl = new Token(TOKEN_EVENT_REFERENCE);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, tokenSsl,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        testObj.directAuthorise(request);

        assertEquals(TOKEN_REQUEST, request.getOrder().getTokenRequest());
    }

    @Test
    public void createDirectAuthoriseRequestShouldRaiseWorldpayValidationExceptionWithMinimumValues() throws WorldpayException {
        thrown.expect(WorldpayValidationException.class);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, null,
                null, null, null, null, null, DynamicInteractionType.ECOMMERCE);

        testObj.directAuthorise(request);
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestShouldRaiseWorldpayValidationExceptionWithMinimumValues() throws WorldpayException {
        thrown.expect(WorldpayValidationException.class);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, null,
                null, null, null, null, null, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        testObj.directAuthorise(request);
    }

    @Test
    public void createDirectAuthoriseRequestShouldReturnAnAuthorisedPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", AUTHORISED, authStatus);
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestShouldReturnAnAuthorisedPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", BILLING_ADDRESS);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthoriseServiceResponse);
        assertFalse("Errors returned from authorise request", directAuthoriseServiceResponse.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthoriseServiceResponse.getOrderCode());
        final PaymentReply paymentReply = directAuthoriseServiceResponse.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", AUTHORISED, authStatus);
    }

    @Test
    public void testDirectAuthoriseRefusedWithCardPayment() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "REFUSED", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", REFUSED, authStatus);
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestShouldReturnRefusedPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "REFUSED", "123", BILLING_ADDRESS);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", REFUSED, authStatus);
    }

    @Test
    public void createDirectAuthoriseRequestShouldReturnReferredPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "REFERRED", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", AUTHORISED, authStatus);
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestShouldReturnReferredPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "REFERRED", "123", BILLING_ADDRESS);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", AUTHORISED, authStatus);
    }

    @Test
    public void createDirectAuthoriseRequestShouldReturnFraudPaymentReply() throws WorldpayException {

        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "FRAUD", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", AUTHORISED, authStatus);
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestShouldReturnFraudPaymentReply() throws WorldpayException {

        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "FRAUD", "123", BILLING_ADDRESS);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNotNull("Payment reply is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals("Authorised status is not correct", AUTHORISED, authStatus);
    }

    @Test
    public void createDirectAuthoriseRequestWithERRORMagicValue() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "ERROR", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertTrue("No Errors returned from authorise request", directAuthorise.isError());
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestWithERRORMagicValue() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "ERROR", "123", BILLING_ADDRESS);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "J. Shopper" + orderCode, BROWSER, SESSION);

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertTrue("No Errors returned from authorise request", directAuthorise.isError());
    }

    @Test
    public void testDirectAuthoriseGoingTo3DUsingHelperMethodAndAdds3DDetails() throws WorldpayException {

        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "3D", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNull("Payment reply is not null!", paymentReply);
        final Request3DInfo request3dInfo = directAuthorise.getRequest3DInfo();
        assertNotNull("Request 3D info is null", request3dInfo);
        final String issuerUrl = request3dInfo.getIssuerUrl();
        assertNotNull("Issuer URL cannot be null", issuerUrl);
        final String paRequest = request3dInfo.getPaRequest();
        assertNotNull("paRequest cannot be null", paRequest);
        final String echoData = directAuthorise.getEchoData();
        assertNotNull("echoData cannot be null", echoData);
    }

    @Test
    public void createTokenAndDirectAuthoriseShouldGetResponseWithRequest3DInfoAndAdds3DDetails() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "3D", "123", BILLING_ADDRESS);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNull("Payment reply is not null!", paymentReply);
        final Request3DInfo request3dInfo = directAuthorise.getRequest3DInfo();
        assertNotNull("Request 3D info is null", request3dInfo);
        final String issuerUrl = request3dInfo.getIssuerUrl();
        assertNotNull("Issuer URL cannot be null", issuerUrl);
        final String paRequest = request3dInfo.getPaRequest();
        assertNotNull("paRequest cannot be null", paRequest);
        final String echoData = directAuthorise.getEchoData();
        assertNotNull("echoData cannot be null", echoData);
    }

    @Test
    public void createDirectAuthoriseRequestWith3DSecureGetsResponseWithRequest3DInfo() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "3D", "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNull("Payment reply is not null!", paymentReply);
        final Request3DInfo request3dInfo = directAuthorise.getRequest3DInfo();
        assertNotNull("Request 3D info is null", request3dInfo);
        final String issuerUrl = request3dInfo.getIssuerUrl();
        assertNotNull("Issuer URL cannot be null", issuerUrl);
        final String paRequest = request3dInfo.getPaRequest();
        assertNotNull("paRequest cannot be null", paRequest);

        final DirectAuthoriseServiceRequest direct3DAuthoriseRequest = DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(merchantInfo, basicOrderInfo,
                SESSION, FAKED_PA_RESPONSE);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.directAuthorise(direct3DAuthoriseRequest);

        assertNotNull("Authorise response is null!", directAuthoriseServiceResponse);
        // We have to assert that this response actually has errors, as 3d secure hasn't actually been checked (redirect)
        assertTrue("Expect errors returned", directAuthoriseServiceResponse.isError());
        assertEquals("Expect error code 7, verification of PaRes failed", "7", directAuthoriseServiceResponse.getErrorDetail().getCode());
        assertEquals("Expect error code 7, verification of PaRes failed", "verification of PaRes failed", directAuthoriseServiceResponse.getErrorDetail().getMessage());
    }

    @Test
    public void createTokenAndDirectAuthoriseShouldGetResponseWithRequest3DInfo() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "3D", "123", BILLING_ADDRESS);
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);
        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final Order requestOrder = request.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertFalse("Errors returned from authorise request", directAuthorise.isError());
        assertEquals("Order code returned is incorrect", orderCode, directAuthorise.getOrderCode());
        final PaymentReply paymentReply = directAuthorise.getPaymentReply();
        assertNull("Payment reply is not null!", paymentReply);
        final Request3DInfo request3dInfo = directAuthorise.getRequest3DInfo();
        assertNotNull("Request 3D info is null", request3dInfo);
        final String issuerUrl = request3dInfo.getIssuerUrl();
        assertNotNull("Issuer URL cannot be null", issuerUrl);
        final String paRequest = request3dInfo.getPaRequest();
        assertNotNull("paRequest cannot be null", paRequest);

        final DirectAuthoriseServiceRequest direct3DAuthoriseRequest = DirectAuthoriseServiceRequest.createTokenAndDirect3DAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, FAKED_PA_RESPONSE, directAuthorise.getEchoData(), SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.directAuthorise(direct3DAuthoriseRequest);

        assertNotNull("Authorise response is null!", directAuthoriseServiceResponse);
        // We have to assert that this response actually has errors, as 3d secure hasn't actually been checked (redirect)
        assertTrue("No errors returned", directAuthoriseServiceResponse.isError());
    }

    @Test
    public void testDirectAuthoriseSuccessWithAPreviousToken() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", BILLING_ADDRESS);
        orderCode = String.valueOf(new java.util.Date().getTime());
        final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, "JShopper" + orderCode, BROWSER, SESSION);

        final TokenRequest TOKEN_REQUEST = new TokenRequest("JShopper" + "REF" + orderCode, TOKEN_REASON);
        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST, DynamicInteractionType.ECOMMERCE);

        assertEquals(TOKEN_REQUEST, request.getOrder().getTokenRequest());

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.directAuthorise(request);

        final String paymentTokenID = directAuthoriseServiceResponse.getToken().getTokenDetails().getPaymentTokenID();
        final Token tokenSsl = new Token(paymentTokenID);

        orderCode = String.valueOf(new java.util.Date().getTime());
        basicOrderInfo = new BasicOrderInfo(orderCode, "Your Order & Order desc with token", new Amount("1000", "EUR", "2"));
        final DirectAuthoriseServiceRequest tokenRequest = DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(merchantInfo, basicOrderInfo, tokenSsl,
                SHOPPER_WITH_SHOPPER_ID, SHIPPING_ADDRESS, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

        final DirectAuthoriseServiceResponse directAuthoriseExistingToken = testObj.directAuthorise(tokenRequest);

        assertNotNull(directAuthoriseExistingToken);
        assertFalse(directAuthoriseExistingToken.isError());
        assertEquals(this.orderCode, directAuthoriseExistingToken.getOrderCode());
        final Order requestOrder = tokenRequest.getOrder();
        final Payment tokenPayment = requestOrder.getPaymentDetails().getPayment();
        assertEquals(paymentTokenID, ((Token) tokenPayment).getPaymentTokenID());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());
        final PaymentReply paymentReply = directAuthoriseExistingToken.getPaymentReply();
        assertNotNull(paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertEquals(AUTHORISED, authStatus);
    }
}
