package com.worldpay.service;

import com.worldpay.data.*;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.payment.PaymentType;
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
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASS = "3l3ph4nt_&_c4st!3";
    private static final String VALUE = "100";
    private static final String EUR = "EUR";
    private static final String EXPONENT = "2";
    private static final String YOUR_ORDER_ORDER_DESC = "Your Order & Order desc";
    private static final String NAME = "John";
    private static final String SHOPPER = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String TOKEN_EVENT_REFERENCE_NUMBER = "tokenEventReferenceNumber";
    private static final String TOKEN_REASON = "tokenReason";

    private Address shippingAddress;
    private TokenRequest tokenRequest;
    private Address address;
    private List<PaymentType> includedPaymentMethods = singletonList(ONLINE);
    private Address billingAddress;
    private Payment payment;
    private Session session;
    private Shopper shopper;
    private StoredCredentials storedCredentials;
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

        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantPassword(MERCHANT_PASS);
        merchantInfo.setUsingMacValidation(false);
        this.merchantInfo = merchantInfo;

        final Amount amount = new Amount();
        amount.setValue(VALUE);
        amount.setCurrencyCode(EUR);
        amount.setExponent(EXPONENT);

        final BasicOrderInfo basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setOrderCode(orderCode);
        basicOrderInfo.setDescription(YOUR_ORDER_ORDER_DESC);
        basicOrderInfo.setAmount(amount);
        this.basicOrderInfo = basicOrderInfo;

        final Address shippingAddress = new Address();
        shippingAddress.setFirstName(NAME);
        shippingAddress.setLastName(SHOPPER);
        shippingAddress.setAddress1(SHOPPER_ADDRESS_1);
        shippingAddress.setAddress2(SHOPPER_ADDRESS_2);
        shippingAddress.setAddress3(SHOPPER_ADDRESS_3);
        shippingAddress.setPostalCode(POSTAL_CODE);
        shippingAddress.setCity(CITY);
        shippingAddress.setCountryCode(GB);
        this.shippingAddress = shippingAddress;

        final TokenRequest tokenRequestData = new TokenRequest();
        tokenRequestData.setTokenEventReference(TOKEN_EVENT_REFERENCE_NUMBER);
        tokenRequestData.setTokenReason(TOKEN_REASON);
        tokenRequestData.setMerchantToken(false);
        tokenRequest = tokenRequestData;

        final Address address = new Address();
        address.setFirstName(NAME);
        address.setLastName(SHOPPER);
        address.setAddress1(SHOPPER_ADDRESS_1);
        address.setAddress2(SHOPPER_ADDRESS_2);
        address.setAddress3(SHOPPER_ADDRESS_3);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);
        this.address = address;

        final Address billingAddress = new Address();
        billingAddress.setFirstName(NAME);
        billingAddress.setLastName(SHOPPER);
        billingAddress.setAddress1(SHOPPER_ADDRESS_1);
        billingAddress.setAddress2(SHOPPER_ADDRESS_2);
        billingAddress.setAddress3(SHOPPER_ADDRESS_3);
        billingAddress.setPostalCode(POSTAL_CODE);
        billingAddress.setCity(CITY);
        billingAddress.setCountryCode(GB);
        this.billingAddress = billingAddress;

        final Cse payment = new Cse();
        payment.setEncryptedData(ENCRYPTED_DATA);
        payment.setAddress(this.shippingAddress);
        this.payment = payment;

        final Session session = new Session();
        session.setShopperIPAddress(SHOPPER_IP_ADDRESS);
        session.setId(SESSION_ID);
        this.session = session;

        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setSession(this.session);
        this.shopper = shopper;

        final StoredCredentials storedCredentials = new StoredCredentials();
        storedCredentials.setUsage(Usage.FIRST);
        this.storedCredentials = storedCredentials;
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
            .withPayment(payment)
            .withShopper(shopper)
            .withStoredCredentials(storedCredentials)
            .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        final RedirectAuthoriseServiceResponse response = gateway.redirectAuthorise(request);
        assertTrue(response.isError());
    }

    @Test
    public void createTokenAndRedirectAuthoriseRequestShouldReturnErrorWhenThereAreMissingFields() throws WorldpayException {
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withTokenRequest(tokenRequest)
            .withPayment(payment)
            .withShopper(shopper)
            .withStoredCredentials(storedCredentials)
            .withIncludedPTs(includedPaymentMethods)
            .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        RedirectAuthoriseServiceResponse response = gateway.redirectAuthorise(request);
        assertTrue(response.isError());
    }

    @Test
    public void createRedirectAuthoriseRequestShouldGetRedirectReference() throws WorldpayException {
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withOrderContent(ORDER_CONTENT)
            .withIncludedPTs(includedPaymentMethods)
            .withShippingAddress(address)
            .withBillingAddress(billingAddress)
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
        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setAuthenticatedShopperID(AUTH_SHOPPER_ID);
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfo)
            .withOrderInfo(basicOrderInfo)
            .withOrderContent(ORDER_CONTENT)
            .withIncludedPTs(includedPaymentMethods)
            .withShopper(shopper)
            .withShippingAddress(address)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withTokenRequest(tokenRequest)
            .build();
        final RedirectAuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(authoriseRequestParameters);

        assertEquals(tokenRequest, request.getOrder().getTokenRequest());

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
