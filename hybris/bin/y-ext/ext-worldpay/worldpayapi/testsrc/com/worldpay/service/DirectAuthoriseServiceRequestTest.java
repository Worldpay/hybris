package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.*;
import com.worldpay.service.model.klarna.KlarnaMerchantUrls;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@UnitTest
public class DirectAuthoriseServiceRequestTest {

    private static final String expiryYear;
    private static final String TOKEN_ID = "tokenId";
    private static final String ECHO_DATA = "echoData";
    private static final String MERCHANT1ECOM = "MERCHANT1ECOM";
    private static final String DESCRIPTION = "Your Order & Order desc";
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");
    private static final String PA_RES = "paRes";
    private static final String SHOPPER_NAME = "J. Shopper";
    private static final String EXTRA_MERCHANT_DATA = "extraMerchantData";
    private static final String SHOPPER_LOCALE = "en-GB";
    private static final String LINE_ITEM_REFERENCE_ID = "id";
    private static final String LINE_ITEM_REFERENCE_VALUE = "value";

    static {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        expiryYear = new SimpleDateFormat("YYYY").format(cal.getTime());
    }

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String SHOPPER_EMAIL = "jshopper@myprovider.com";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String ORDER_CODE = "orderCode";
    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();

    private static final TokenRequest TOKEN_REQUEST = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
    private static final String COUNTRY_CODE = "GB";
    private static final Address SHIPPING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", COUNTRY_CODE);
    private static final com.worldpay.service.model.Date EXPIRY_DATE = new com.worldpay.service.model.Date("09", expiryYear);
    private static final Session SESSION = new Session("192.168.1.1", "sessionId1234");
    private static final Browser BROWSER = new Browser("text/html,application/xhtml+xml,application/xml;q=0. 9,*/*;q=0.8", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)", "0");
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", COUNTRY_CODE);
    private static final Shopper SHOPPER = new Shopper(SHOPPER_EMAIL, null, BROWSER, SESSION);
    private static final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, AUTHENTICATED_SHOPPER_ID, BROWSER, SESSION);

    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    private Payment payment;

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        merchantInfo = new MerchantInfo(MERCHANT1ECOM, "3l3ph4nt_&_c4st!3");
        basicOrderInfo = new BasicOrderInfo(ORDER_CODE, DESCRIPTION, AMOUNT);
    }

    @Test
    public void createTokenAndDirectAuthoriseRequestWillRaiseIllegalArgumentExceptionWhenParametersAreNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);

        DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(null, merchantInfo, null, null, null, null, null, null, null, null);
    }

    @Test
    public void createDirectAuthoriseRequestWillRaiseIllegalArgumentExceptionWhenParametersAreNull() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);

        DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(null, merchantInfo, null, null, null, null, null, null, null);
    }

    @Test
    public void createDirectAuthoriseRequestShouldNotRequestTokenAndHaveAllTheInformation() throws WorldpayException {
        payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE);

        final Order requestOrder = result.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, result.getOrder().getBillingAddress());
        assertEquals(SHOPPER, result.getOrder().getShopper());
        assertEquals(payment, result.getOrder().getPaymentDetails().getPayment());
        assertCommonOrderRequestData(result);
    }

    @Test
    public void createDirectAuthoriseRequestShouldRequestTokenAndHaveAllTheInformation() throws WorldpayException {
        payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createTokenAndDirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);

        final Order requestOrder = result.getOrder();
        assertEquals(TOKEN_REQUEST, requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, result.getOrder().getBillingAddress());
        assertEquals(SHOPPER_WITH_SHOPPER_ID, result.getOrder().getShopper());

        assertCommonOrderRequestData(result);
    }

    @Test
    public void shouldContainOrderLinesForKlarnaDirectAuthorize() throws WorldpayException {
        final KlarnaMerchantUrls merchantUrls = new KlarnaMerchantUrls("checkoutURL", "confirmationURL");
        payment = PaymentBuilder.createKLARNASSL(COUNTRY_CODE, SHOPPER_LOCALE, merchantUrls, EXTRA_MERCHANT_DATA);

        final LineItem lineItem = new LineItem();
        lineItem.setLineItemType(LineItem.LINE_ITEM_TYPE.SHIPPING_FEE);

        final LineItemReference lineItemReference = new LineItemReference(LINE_ITEM_REFERENCE_ID, LINE_ITEM_REFERENCE_VALUE);
        lineItem.setLineItemReference(lineItemReference);

        final OrderLines orderLines = new OrderLines("orderTaxAmount", "termsURL", singletonList(lineItem));

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createKlarnaDirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SESSION, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, orderLines);

        final Order requestOrder = result.getOrder();
        assertEquals(BILLING_ADDRESS, result.getOrder().getBillingAddress());
        assertEquals(SHOPPER_WITH_SHOPPER_ID, result.getOrder().getShopper());
        assertThat(requestOrder.getOrderLines().getLineItems().size(), is(1));
        assertThat(requestOrder.getOrderLines().getLineItems().get(0).getLineItemReference().getId(), is(LINE_ITEM_REFERENCE_ID));
        assertThat(requestOrder.getOrderLines().getLineItems().get(0).getLineItemReference().getValue(), is(LINE_ITEM_REFERENCE_VALUE));

        assertCommonOrderRequestData(result);
    }

    @Test
    public void createTokenisedDirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionIfPaymentIsNotToken() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Payment type needs to be a type of Token");

        payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);

        basicOrderInfo = new BasicOrderInfo(ORDER_CODE, "Your Order & Order desc with token", new Amount("1000", "EUR", "2"));
        DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SHIPPING_ADDRESS, STATEMENT_NARRATIVE);
    }

    @Test
    public void createTokenisedDirectAuthoriseRequestShouldCreateAnOrderRequestWithToken() {
        payment = new Token(TOKEN_ID);
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SHIPPING_ADDRESS, STATEMENT_NARRATIVE);

        assertEquals("TOKEN-SSL", result.getOrder().getPaymentDetails().getPayment().getPaymentType().getMethodCode());
        assertEquals(TOKEN_ID, ((Token) result.getOrder().getPaymentDetails().getPayment()).getPaymentTokenID());
        assertEquals(SHOPPER_WITH_SHOPPER_ID, result.getOrder().getShopper());
        assertCommonOrderRequestData(result);
    }

    @Test
    public void createTokenAndDirect3DAuthoriseRequestShouldContainEchoDataAndTokenRequest() {
        payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createTokenAndDirect3DAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER, SESSION, PA_RES, ECHO_DATA, SHIPPING_ADDRESS, BILLING_ADDRESS, STATEMENT_NARRATIVE, TOKEN_REQUEST);

        assertCommonOrderRequestData(result);
        assertEquals(BILLING_ADDRESS, result.getOrder().getBillingAddress());
        assertEquals(TOKEN_REQUEST, result.getOrder().getTokenRequest());
        assertEquals(ECHO_DATA, result.getOrder().getEchoData());
        assertEquals(PA_RES, result.getOrder().getPaymentDetails().getPaResponse());
        assertEquals(SHOPPER, result.getOrder().getShopper());
    }

    @Test
    public void createDirect3DAuthoriseRequestShouldContainPaResponse() {
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, SESSION, PA_RES);

        assertEquals(WORLD_PAY_CONFIG, result.getWorldpayConfig());
        assertEquals(merchantInfo, result.getMerchantInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ORDER_CODE, result.getOrder().getOrderCode());
        assertEquals(PA_RES, result.getOrder().getPaResponse());
        assertEquals(SESSION, result.getOrder().getSession());
    }

    @Test
    public void shouldRaiseIllegalArgumentExceptionIfPaymentIsNotToken() throws WorldpayException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Payment type needs to be a type of Token");

        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);

        basicOrderInfo = new BasicOrderInfo(ORDER_CODE, "Your Order & Order desc with token", new Amount("1000", "EUR", "2"));
        DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(WORLD_PAY_CONFIG, merchantInfo, basicOrderInfo, payment,
                SHOPPER_WITH_SHOPPER_ID, SHIPPING_ADDRESS, STATEMENT_NARRATIVE);
    }


    private void assertCommonOrderRequestData(final DirectAuthoriseServiceRequest result) {
        assertEquals(WORLD_PAY_CONFIG, result.getWorldpayConfig());
        assertEquals(merchantInfo, result.getMerchantInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ORDER_CODE, result.getOrder().getOrderCode());
        assertEquals(DESCRIPTION, result.getOrder().getDescription());
        assertEquals(AMOUNT, result.getOrder().getAmount());
        assertEquals(SHIPPING_ADDRESS, result.getOrder().getShippingAddress());
        assertEquals(STATEMENT_NARRATIVE, result.getOrder().getStatementNarrative());
        assertEquals(payment, result.getOrder().getPaymentDetails().getPayment());
        assertEquals(SESSION, result.getOrder().getPaymentDetails().getSession());
    }
}
