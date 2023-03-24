package com.worldpay.service;

import com.google.common.collect.ImmutableList;
import com.worldpay.data.*;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.applepay.Header;
import com.worldpay.data.klarna.KlarnaRedirectURLs;
import com.worldpay.data.payment.Card;
import com.worldpay.data.payment.PayWithGoogleSSL;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.lineItem.LineItemType;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class DirectAuthoriseServiceRequestTest {

    private static final String TOKEN_ID = "tokenId";
    private static final String MERCHANT1ECOM = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String DESCRIPTION = "Your Order & Order desc";
    private static final String PA_RES = "paRes";
    private static final String SHOPPER_NAME = "J. Shopper";
    private static final String EXTRA_MERCHANT_DATA = "extraMerchantData";
    private static final String SHOPPER_LOCALE = "en-GB";
    private static final String LINE_ITEM_REFERENCE_ID = "id";
    private static final String LINE_ITEM_REFERENCE_VALUE = "value";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "Postal code";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String SHOPPER_EMAIL = "jshopper@myprovider.com";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String ORDER_CODE = "orderCode";
    private static final String COUNTRY_CODE = "GB";
    private static final String IP = "192.168.1.1";
    private static final String SESSION_ID = "sessionId1234";
    private static final String ACCEPT_HEADER = "text/html,application/xhtml+xml,application/xml;q=0. 9,*/*;q=0.8";
    private static final String USER_AGENT_HEADER = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)";
    private static final String DEVICE_TYPE = "0";
    private static final String ORDER_TAX_AMOUNT = "orderTaxAmount";
    private static final String TERMS_URL = "termsURL";
    private static final String REFERENCE_ID = "referenceId";
    private static final String DEVICE_SESSION_ID = "device_session_id";
    private static final String KLARNA_PAYMENT_METHOD = "KLARNA_V2-SSL";
    private static final String KLARNA_CANCEL_URL = "klarna-cancel-redirect-URL";
    private static final String KLARNA_FAILURE_URL = "klarna-failure-redirect-URL";
    private static final String KLARNA_PENDING_URL = "klarna-pending-redirect-URL";
    private static final String KLARNA_SUCCESS_URL = "klarna-success-redirect-URL";
    private static final String DELIVERY = "DELIVERY";
    private static final String AMOUNT = "100";
    private static final String CHECKOUT_ID = "checkoutId";

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ShopperFields shopperFields = new ShopperFields();
    private CustomNumericFields customNumericFields = new CustomNumericFields();
    private CustomStringFields customStringFields = new CustomStringFields();
    private Purchase purchase = new Purchase();
    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    private Payment payment;
    private Additional3DSData additional3DSData;
    private RiskData riskData;
    private FraudSightData fraudSightData;
    private BranchSpecificExtension branchSpecificExtension;
    private TokenRequest tokenRequest;
    private Amount amount;
    private Address shippingAddress;
    private Address billingAddress;
    private Session session;
    private Browser browser;
    private Card visa;
    private Date expiryDate;
    private Shopper shopper;
    private Shopper shopperWithoutBrowserNorSession;
    private Shopper shopperWithShopperID;
    private GuaranteedPaymentsData guaranteedPaymentsData;
    private String checkoutId;

    @Before
    public void setUp() {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT1ECOM);
        this.merchantInfo = merchantInfo;

        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setMerchantToken(false);
        this.tokenRequest = tokenRequest;

        final Amount amount = new Amount();
        amount.setExponent("2");
        amount.setCurrencyCode("EUR");
        amount.setValue(AMOUNT);
        this.amount = amount;

        final BasicOrderInfo basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setOrderCode(ORDER_CODE);
        basicOrderInfo.setDescription(DESCRIPTION);
        basicOrderInfo.setAmount(amount);
        this.basicOrderInfo = basicOrderInfo;

        final Additional3DSData additional3DSData = new Additional3DSData();
        additional3DSData.setDfReferenceId(REFERENCE_ID);
        this.additional3DSData = additional3DSData;

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
        session.setShopperIPAddress(IP);
        this.session = session;

        final Browser browser = new Browser();
        browser.setUserAgentHeader(USER_AGENT_HEADER);
        browser.setAcceptHeader(ACCEPT_HEADER);
        browser.setDeviceType(DEVICE_TYPE);
        this.browser = browser;

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

        expiryDate = WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now().plusYears(1));

        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(SHOPPER_EMAIL);
        shopper.setSession(this.session);
        shopper.setBrowser(this.browser);
        this.shopper = shopper;

        final Shopper shopperWithoutBrowserNorSession = new Shopper();
        shopperWithoutBrowserNorSession.setShopperEmailAddress(SHOPPER_EMAIL);
        this.shopperWithoutBrowserNorSession = shopperWithoutBrowserNorSession;

        final Shopper shopperWithShopperID = new Shopper();
        shopperWithShopperID.setShopperEmailAddress(SHOPPER_EMAIL);
        shopperWithShopperID.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        shopperWithShopperID.setSession(this.session);
        shopperWithShopperID.setBrowser(this.browser);
        this.shopperWithShopperID = shopperWithShopperID;

        final Card visa = new Card();
        visa.setPaymentType(PaymentType.VISA.getMethodCode());
        visa.setCardNumber("4444333322221111");
        visa.setCvc("123");
        visa.setExpiryDate(expiryDate);
        visa.setCardAddress(billingAddress);
        visa.setCardHolderName(SHOPPER_NAME);
        this.visa = visa;

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

        this.checkoutId = CHECKOUT_ID;
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
        payment = visa;

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters3D);

        final Order requestOrder = result.getOrder();
        assertNull(requestOrder.getTokenRequest());
        assertEquals(billingAddress, result.getOrder().getBillingAddress());
        assertEquals(shopper, result.getOrder().getShopper());
        assertEquals(payment, result.getOrder().getPaymentDetails().getPayment());
        assertCommonOrderRequestData(result);
    }

    @Test
    public void shouldContainOrderLinesForKlarnaDirectAuthorize() {
        final KlarnaRedirectURLs klarnaRedirectURLs = new KlarnaRedirectURLs();
        klarnaRedirectURLs.setCancelURL(KLARNA_CANCEL_URL);
        klarnaRedirectURLs.setFailureURL(KLARNA_FAILURE_URL);
        klarnaRedirectURLs.setPendingURL(KLARNA_PENDING_URL);
        klarnaRedirectURLs.setSuccessURL(KLARNA_SUCCESS_URL);

        payment = WorldpayInternalModelTransformerUtil.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, EXTRA_MERCHANT_DATA, KLARNA_PAYMENT_METHOD, klarnaRedirectURLs);

        final LineItem lineItem = new LineItem();
        lineItem.setLineItemType(LineItemType.SHIPPING_FEE);

        final LineItemReference lineItemReference = new LineItemReference();
        lineItemReference.setValue(LINE_ITEM_REFERENCE_VALUE);
        lineItemReference.setId(LINE_ITEM_REFERENCE_ID);
        lineItem.setLineItemReference(lineItemReference);

        final OrderLines orderLines = new OrderLines();
        orderLines.setLineItems(singletonList(lineItem));
        orderLines.setOrderTaxAmount(ORDER_TAX_AMOUNT);
        orderLines.setTermsURL(TERMS_URL);

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopperWithShopperID)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(orderLines)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createKlarnaDirectAuthoriseRequest(requestParameters3D);

        final Order requestOrder = result.getOrder();
        assertEquals(billingAddress, result.getOrder().getBillingAddress());
        assertEquals(shopperWithShopperID, result.getOrder().getShopper());
        assertThat(requestOrder.getOrderLines().getLineItems()).hasSize(1);
        assertThat(requestOrder.getOrderLines().getLineItems().get(0).getLineItemReference().getId()).isEqualTo(LINE_ITEM_REFERENCE_ID);
        assertThat(requestOrder.getOrderLines().getLineItems().get(0).getLineItemReference().getValue()).isEqualTo(LINE_ITEM_REFERENCE_VALUE);

        assertCommonOrderRequestData(result);
    }

    @Test
    public void createTokenisedDirectAuthoriseRequestShouldRaiseIllegalArgumentExceptionIfPaymentIsNotToken() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Payment type needs to be a type of Token");

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(visa)
            .withShopper(shopperWithShopperID)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(null)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withAdditional3DSData(additional3DSData)
            .withFraudSightData(fraudSightData)
            .withGuaranteedPaymentsData(guaranteedPaymentsData)
            .withCheckoutId(checkoutId)
            .withDeviceSession(DEVICE_SESSION_ID)
            .build();
        DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters3D);
    }

    @Test
    public void createTokenisedDirectAuthoriseRequest_ShouldCreateAnOrderRequestWithToken() {
        final Token token = new Token();
        token.setPaymentType(PaymentType.TOKENSSL.getMethodCode());
        token.setPaymentTokenID(TOKEN_ID);
        token.setMerchantToken(false);
        payment = token;

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopperWithShopperID)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(null)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withAdditional3DSData(additional3DSData)
            .withFraudSightData(fraudSightData)
            .withGuaranteedPaymentsData(guaranteedPaymentsData)
            .withCheckoutId(checkoutId)
            .withDeviceSession(DEVICE_SESSION_ID)
            .withLevel23Data(branchSpecificExtension)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(requestParameters3D);

        assertEquals("TOKEN-SSL", result.getOrder().getPaymentDetails().getPayment().getPaymentType());
        assertEquals(TOKEN_ID, ((Token) result.getOrder().getPaymentDetails().getPayment()).getPaymentTokenID());
        assertEquals(shopperWithShopperID, result.getOrder().getShopper());
        assertEquals(REFERENCE_ID, result.getOrder().getAdditional3DSData().getDfReferenceId());
        assertEquals(fraudSightData, result.getOrder().getFraudSightData());
        assertEquals(guaranteedPaymentsData, result.getOrder().getGuaranteedPaymentsData());
        assertEquals(checkoutId, result.getOrder().getCheckoutId());
        assertEquals(shopperFields, result.getOrder().getFraudSightData().getShopperFields());
        assertEquals(DEVICE_SESSION_ID, result.getOrder().getDeviceSession());
        assertEquals(branchSpecificExtension, result.getOrder().getBranchSpecificExtension());
        assertCommonOrderRequestData(result);
    }

    @Test
    public void createApplePayDirectAuthoriseRequestShouldCreateAnOrderRequestWithApplePayAsPaymentMethod() {
        final Header header = new Header();
        header.setTransactionId("transactionId");
        header.setApplicationData("applicationData");
        header.setPublicKeyHash("keyHash");
        header.setEphemeralPublicKey("ephKey");

        final ApplePay applePay = new ApplePay();
        applePay.setHeader(header);
        applePay.setSignature("signature");
        applePay.setVersion("version");
        applePay.setData("data");
        applePay.setTokenRequestorID("tokenRequestorID");
        applePay.setPaymentType(PaymentType.APPLEPAYSSL.getMethodCode());

        payment = applePay;

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopperWithShopperID)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(null).build();
        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createApplePayDirectAuthoriseRequest(requestParameters3D);

        assertEquals("APPLEPAY-SSL", result.getOrder().getPaymentDetails().getPayment().getPaymentType());
        assertEquals("data", ((ApplePay) result.getOrder().getPaymentDetails().getPayment()).getData());
        assertEquals(shopperWithShopperID, result.getOrder().getShopper());
    }

    @Test
    public void createDirect3DAuthoriseRequestShouldContainPaResponse() {

        final AuthoriseRequestParameters requestParameters3D = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(null)
            .withShopper(shopperWithShopperID)
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
        assertEquals(session, result.getOrder().getSession());
    }


    @Test
    public void createDirectAuthorisedWithoutMerchantShouldRaiseIllegalArgumentExceptionException() {
        thrown.expect(IllegalArgumentException.class);
        merchantInfo.setMerchantPassword(null);
        merchantInfo.setMerchantCode(null);

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(visa)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(requestParameters);
    }

    @Test
    public void shouldCreateGooglePayDirectAuthorisationRequest() {
        final PayWithGoogleSSL payment = new PayWithGoogleSSL();
        payment.setProtocolVersion("protocolVersion");
        payment.setSignature("signature");
        payment.setSignedMessage("signedMessage");
        payment.setPaymentType(PaymentType.PAYWITHGOOGLESSL.getMethodCode());

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopperWithoutBrowserNorSession)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withTokenRequest(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();

        final DirectAuthoriseServiceRequest result = DirectAuthoriseServiceRequest.createGooglePayDirectAuthoriseRequest(requestParameters);

        assertThat(result.getOrder().getPaymentDetails().getPayment().getPaymentType()).isEqualTo(PaymentType.PAYWITHGOOGLESSL.getMethodCode());
    }

    @Test
    public void shouldRaiseIllegalArgumentExceptionWhenPaymentIsNotPayWithGoogleSSL() {
        thrown.expect(IllegalArgumentException.class);
        final Token token = new Token();
        token.setPaymentType(PaymentType.TOKENSSL.getMethodCode());
        token.setPaymentTokenID(TOKEN_EVENT_REFERENCE);
        token.setMerchantToken(false);
        payment = token;

        final AuthoriseRequestParameters requestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopperWithoutBrowserNorSession)
            .withShippingAddress(shippingAddress)
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
        assertEquals(billingAddress, resultOrder.getBillingAddress());
        assertEquals(shippingAddress, resultOrder.getShippingAddress());
        assertEquals(STATEMENT_NARRATIVE, resultOrder.getStatementNarrative());
        assertEquals(DynamicInteractionType.ECOMMERCE, resultOrder.getDynamicInteractionType());
        assertEquals(tokenRequest, resultOrder.getTokenRequest());
        assertEquals(fraudSightData, result.getOrder().getFraudSightData());
        assertEquals(guaranteedPaymentsData, result.getOrder().getGuaranteedPaymentsData());
        assertEquals(shopperFields, result.getOrder().getFraudSightData().getShopperFields());
        assertEquals(DEVICE_SESSION_ID, result.getOrder().getDeviceSession());
        assertEquals(branchSpecificExtension, result.getOrder().getBranchSpecificExtension());
        assert3DSInfoOrderRequestData(result);
    }

    protected AuthoriseRequestParameters getAuthoriseRequestParametersMock() {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withPayment(payment)
            .withShopper(shopper)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withTokenRequest(tokenRequest)
            .withAdditional3DSData(additional3DSData)
            .withRiskData(riskData)
            .withFraudSightData(fraudSightData)
            .withGuaranteedPaymentsData(guaranteedPaymentsData)
            .withCheckoutId(checkoutId)
            .withDeviceSession(DEVICE_SESSION_ID)
            .withLevel23Data(branchSpecificExtension)
            .build();
    }

    private void assertCommonOrderRequestData(final DirectAuthoriseServiceRequest result) {
        assertEquals(merchantInfo, result.getMerchantInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ORDER_CODE, result.getOrder().getOrderCode());
        assertEquals(DESCRIPTION, result.getOrder().getDescription());
        assertEquals(amount, result.getOrder().getAmount());
        assertEquals(shippingAddress, result.getOrder().getShippingAddress());
        assertEquals(STATEMENT_NARRATIVE, result.getOrder().getStatementNarrative());
        assertEquals(payment, result.getOrder().getPaymentDetails().getPayment());
        assertEquals(session, result.getOrder().getPaymentDetails().getSession());
    }

    private void assert3DSInfoOrderRequestData(final DirectAuthoriseServiceRequest result) {
        assertEquals(riskData, result.getOrder().getRiskData());
        assertEquals(additional3DSData, result.getOrder().getAdditional3DSData());
    }
}
