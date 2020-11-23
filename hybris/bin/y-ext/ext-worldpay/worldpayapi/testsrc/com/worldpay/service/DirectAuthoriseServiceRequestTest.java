package com.worldpay.service;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.*;
import com.worldpay.service.model.applepay.ApplePay;
import com.worldpay.service.model.applepay.Header;
import com.worldpay.service.model.klarna.KlarnaMerchantUrls;
import com.worldpay.service.model.payment.PayWithGoogleSSL;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class DirectAuthoriseServiceRequestTest {

    private static final String TOKEN_ID = "tokenId";
    private static final String MERCHANT1ECOM = "MERCHANT1ECOM";
    private static final String DESCRIPTION = "Your Order & Order desc";
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");
    private static final String PA_RES = "paRes";
    private static final String SHOPPER_NAME = "J. Shopper";
    private static final String EXTRA_MERCHANT_DATA = "extraMerchantData";
    private static final String SHOPPER_LOCALE = "en-GB";
    private static final String LINE_ITEM_REFERENCE_ID = "id";
    private static final String LINE_ITEM_REFERENCE_VALUE = "value";

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String SHOPPER_EMAIL = "jshopper@myprovider.com";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String ORDER_CODE = "orderCode";

    private static final TokenRequest TOKEN_REQUEST = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
    private static final String COUNTRY_CODE = "GB";
    private static final Address SHIPPING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", COUNTRY_CODE);
    private static final com.worldpay.service.model.Date EXPIRY_DATE = new com.worldpay.service.model.Date(LocalDateTime.now().plusYears(1));
    private static final Session SESSION = new Session("192.168.1.1", "sessionId1234");
    private static final Browser BROWSER = new Browser("text/html,application/xhtml+xml,application/xml;q=0. 9,*/*;q=0.8", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)", "0");
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", COUNTRY_CODE);
    private static final Shopper SHOPPER = new Shopper(SHOPPER_EMAIL, null, BROWSER, SESSION);
    private static final Shopper SHOPPER_WITHOUT_BROWSER_NOR_SESSION = new Shopper(SHOPPER_EMAIL, null, null, null);
    private static final Shopper SHOPPER_WITH_SHOPPER_ID = new Shopper(SHOPPER_EMAIL, AUTHENTICATED_SHOPPER_ID, BROWSER, SESSION);
    private static final String REFERENCE_ID = "referenceId";

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    private Payment payment;
    private Additional3DSData additional3DSData;
    private RiskData riskData;

    @Before
    public void setUp() {
        merchantInfo = new MerchantInfo(MERCHANT1ECOM, "3l3ph4nt_&_c4st!3");
        basicOrderInfo = new BasicOrderInfo(ORDER_CODE, DESCRIPTION, AMOUNT);
        additional3DSData = new Additional3DSData(REFERENCE_ID);
        riskData = new RiskData();
    }

    @Test
    public void createDirectAuthoriseRequestWillRaiseIllegalArgumentExceptionWhenParametersAreNull() {
        thrown.expect(IllegalArgumentException.class);

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(null)
            .withPayment(null)
            .withShopper(null)
            .withShippingAddress(null)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters3D);
    }

    @Test
    public void createDirectAuthoriseRequestShouldNotRequestTokenAndHaveAllTheInformation() {
        payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(BILLING_ADDRESS)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters3D);

        final Order requestOrder = result.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(BILLING_ADDRESS, result.getOrder().getBillingAddress());
        assertEquals(SHOPPER, result.getOrder().getShopper());
        assertEquals(payment, result.getOrder().getPaymentDetails().getPayment());
        assertCommonOrderRequestData(result);
    }

    @Test
    public void shouldContainOrderLinesForKlarnaDirectAuthorize() {
        final KlarnaMerchantUrls merchantUrls = new KlarnaMerchantUrls("checkoutURL", "confirmationURL");
        payment = PaymentBuilder.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, merchantUrls, EXTRA_MERCHANT_DATA);

        final LineItem lineItem = new LineItem();
        lineItem.setLineItemType(LineItem.LINE_ITEM_TYPE.SHIPPING_FEE);

        final LineItemReference lineItemReference = new LineItemReference(LINE_ITEM_REFERENCE_ID, LINE_ITEM_REFERENCE_VALUE);
        lineItem.setLineItemReference(lineItemReference);

        final OrderLines orderLines = new OrderLines("orderTaxAmount", "termsURL", singletonList(lineItem));

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER_WITH_SHOPPER_ID)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(BILLING_ADDRESS)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(orderLines)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createKlarnaDirectAuthoriseRequest(requestParameters3D);

        final Order requestOrder = result.getOrder();
        assertEquals(BILLING_ADDRESS, result.getOrder().getBillingAddress());
        assertEquals(SHOPPER_WITH_SHOPPER_ID, result.getOrder().getShopper());
        assertThat(requestOrder.getOrderLines().getLineItems()).hasSize(1);
        assertThat(requestOrder.getOrderLines().getLineItems().get(0).getLineItemReference().getId()).isEqualTo(LINE_ITEM_REFERENCE_ID);
        assertThat(requestOrder.getOrderLines().getLineItems().get(0).getLineItemReference().getValue()).isEqualTo(LINE_ITEM_REFERENCE_VALUE);

        assertCommonOrderRequestData(result);
    }

    @Test
    public void createTokenisedDirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionIfPaymentIsNotToken() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Payment type needs to be a type of Token");

        payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, SHOPPER_NAME, "123", BILLING_ADDRESS);

        basicOrderInfo = new BasicOrderInfo(ORDER_CODE, "Your Order & Order desc with token", new Amount("1000", "EUR", "2"));

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER_WITH_SHOPPER_ID)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(null)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withAdditional3DSData(additional3DSData)
            .build();
        DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters3D);
    }

    @Test
    public void createTokenisedDirectAuthoriseRequestShouldCreateAnOrderRequestWithToken() {
        payment = new Token(TOKEN_ID, false);

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER_WITH_SHOPPER_ID)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(null)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withAdditional3DSData(additional3DSData)
            .build();
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters3D);

        assertEquals("TOKEN-SSL", result.getOrder().getPaymentDetails().getPayment().getPaymentType().getMethodCode());
        assertEquals(TOKEN_ID, ((Token) result.getOrder().getPaymentDetails().getPayment()).getPaymentTokenID());
        assertEquals(SHOPPER_WITH_SHOPPER_ID, result.getOrder().getShopper());
        assertEquals(REFERENCE_ID, result.getOrder().getAdditional3DSData().getDfReferenceId());
        assertCommonOrderRequestData(result);
    }

    @Test
    public void createApplePayDirectAuthoriseRequestShouldCreateAnOrderRequestWithApplePayAsPaymentMethod() {
        payment = new ApplePay(new Header("ephKey", "keyHash", "transactionId", "applicationData"), "signature", "version", "data", "tokenRequestorID");

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER_WITH_SHOPPER_ID)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(null).build();
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createApplePayDirectAuthoriseRequest(requestParameters3D);

        assertEquals("APPLEPAY-SSL", result.getOrder().getPaymentDetails().getPayment().getPaymentType().getMethodCode());
        assertEquals("data", ((ApplePay) result.getOrder().getPaymentDetails().getPayment()).getData());
        assertEquals(SHOPPER_WITH_SHOPPER_ID, result.getOrder().getShopper());
    }

    @Test
    public void createDirect3DAuthoriseRequestShouldContainPaResponse() {

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(null)
            .withShopper(SHOPPER_WITH_SHOPPER_ID)
            .withShippingAddress(null)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(null)


            .withPaRes(PA_RES).build();
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(requestParameters3D);

        assertEquals(merchantInfo, result.getMerchantInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ORDER_CODE, result.getOrder().getOrderCode());
        assertEquals(PA_RES, result.getOrder().getPaResponse());
        assertEquals(SESSION, result.getOrder().getSession());
    }


    @Test
    public void createDirectAuthorisedWithoutMerchantShouldRaiseIllegalArgumentExceptionException() {
        thrown.expect(IllegalArgumentException.class);
        merchantInfo = new MerchantInfo(null, null);
        final Payment payment = PaymentBuilder.createVISASSL("4444333322221111", EXPIRY_DATE, "J. Shopper", "123", BILLING_ADDRESS);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(BILLING_ADDRESS)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);
    }

    @Test
    public void shouldCreateGooglePayDirectAuthorisationRequest() {
        final PayWithGoogleSSL payment = new PayWithGoogleSSL("protocolVersion", "signature", "signedMessage");
        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER_WITHOUT_BROWSER_NOR_SESSION)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(null)
            .withStatementNarrative(null)
                .withTokenRequest(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createGooglePayDirectAuthoriseRequest(requestParameters);

        assertThat(result.getOrder().getPaymentDetails().getPayment().getPaymentType().getMethodCode()).isEqualTo(PaymentType.PAYWITHGOOGLESSL.getMethodCode());
    }

    @Test
    public void shouldRaiseIllegalArgumentExceptionWhenPaymentIsNotPayWithGoogleSSL() {
        thrown.expect(IllegalArgumentException.class);
        final Token payment = new Token(TOKEN_EVENT_REFERENCE, false);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER_WITHOUT_BROWSER_NOR_SESSION)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        DirectAuthoriseServiceRequest.createGooglePayDirectAuthoriseRequest(requestParameters);

    }

    @Test
    public void createDirectTokenAndAuthoriseRequest_ShouldReturnADirectAuthoriseServiceRequestCorrectlyFilled_WhenWePassToItAuthoriseRequestParameters() {
        basicOrderInfo.setOrderCode(ORDER_CODE);
        final AuthoriseRequestParameters authoriseRequestParametersMock = getAuthoriseRequestParametersMock();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createDirectTokenAndAuthoriseRequest(authoriseRequestParametersMock);
        final Order resultOrder = result.getOrder();

        assertEquals(merchantInfo, result.getMerchantInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(resultOrder.getBillingAddress(), BILLING_ADDRESS);
        assertEquals(resultOrder.getShippingAddress(), SHIPPING_ADDRESS);
        assertEquals(resultOrder.getStatementNarrative(), STATEMENT_NARRATIVE);
        assertEquals(resultOrder.getDynamicInteractionType(), DynamicInteractionType.ECOMMERCE);
        assertEquals(resultOrder.getTokenRequest(), TOKEN_REQUEST);
        assert3DSInfoOrderRequestData(result);
    }

    protected AuthoriseRequestParameters getAuthoriseRequestParametersMock() {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(SHOPPER)
            .withShippingAddress(SHIPPING_ADDRESS)
            .withBillingAddress(BILLING_ADDRESS)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withTokenRequest(TOKEN_REQUEST)
            .withAdditional3DSData(additional3DSData)
            .withRiskData(riskData)
            .build();
    }

    private void assertCommonOrderRequestData(final DirectAuthoriseServiceRequest result) {
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

    private void assert3DSInfoOrderRequestData(final DirectAuthoriseServiceRequest result) {
        assertEquals(riskData, result.getOrder().getRiskData());
        assertEquals(additional3DSData, result.getOrder().getAdditional3DSData());
    }
}
