package com.worldpay.service;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.request.AuthoriseRequestParameters;
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

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static org.junit.Assert.*;

@IntegrationTest
public class DirectAuthoriseServiceRequestIntegrationTest extends ServicelayerBaseTest {

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

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();
    private String orderCode;
    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway testObj;
    private Additional3DSData additional3DSData;

    @Before
    public void setUp() {
        merchantInfo = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
        orderCode = String.valueOf(new java.util.Date().getTime());
        basicOrderInfo = new BasicOrderInfo(orderCode, "Your Order & Order desc", new Amount("100", "EUR", "2"));
        additional3DSData = new Additional3DSData("referenceId");
    }

    @Test
    public void createDirectAuthoriseRequestShouldRaiseWorldpayValidationExceptionWithMinimumValues() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(null)
                .withPayment(null)
                .withShopper(null)
                .withShippingAddress(null)
                .withBillingAddress(null)
                .withStatementNarrative(null)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

        testObj.directAuthorise(request);
    }

    @Test
    public void createDirectAuthoriseRequestShouldReturnAnAuthorisedPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

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
    public void testDirectAuthoriseRefusedWithCardPayment() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "REFUSED", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

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
    public void createDirectAuthoriseRequestShouldReturnReferredPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "REFERRED", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

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
    public void createDirectAuthoriseRequestShouldReturnFraudPaymentReply() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "FRAUD", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

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
    public void createDirectAuthoriseRequestWithERRORMagicValue() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "ERROR", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

        final Order requestOrder = request.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, requestOrder.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, requestOrder.getShippingAddress());

        final DirectAuthoriseServiceResponse directAuthorise = testObj.directAuthorise(request);

        assertNotNull("Authorise response is null!", directAuthorise);
        assertTrue("No Errors returned from authorise request", directAuthorise.isError());
    }

    @Test
    public void testDirectAuthoriseGoingTo3DUsingHelperMethodAndAdds3DDetails() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "3D", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

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
    public void createDirectAuthoriseRequestWith3DSecureGetsResponseWithRequest3DInfo() throws WorldpayException {
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "3D", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(payment)
                .withShopper(SHOPPER)
                .withShippingAddress(SHIPPING_ADDRESS)
                .withBillingAddress(BILLING_ADDRESS)
                .withStatementNarrative(STATEMENT_NARRATIVE)
                .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE).build();

        final DirectAuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);

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

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
                .withMerchantInfo(merchantInfo)
                .withOrderInfo(basicOrderInfo)
                .withPayment(null)
                .withShopper(SHOPPER)
                .withShippingAddress(null)
                .withBillingAddress(null)
                .withStatementNarrative(null)
                .withDynamicInteractionType(null)


                .withPaRes(FAKED_PA_RESPONSE).build();

        final DirectAuthoriseServiceRequest direct3DAuthoriseRequest = DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(requestParameters3D);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.directAuthorise(direct3DAuthoriseRequest);

        assertNotNull("Authorise response is null!", directAuthoriseServiceResponse);
        // We have to assert that this response actually has errors, as 3d secure hasn't actually been checked (redirect)
        assertTrue("Expect errors returned", directAuthoriseServiceResponse.isError());
        assertEquals("Expect error code 7, verification of PaRes failed", "7", directAuthoriseServiceResponse.getErrorDetail().getCode());
        assertEquals("Expect error code 7, verification of PaRes failed", "verification of PaRes failed", directAuthoriseServiceResponse.getErrorDetail().getMessage());
    }
}
