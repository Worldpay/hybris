package com.worldpay.service.request;

import com.worldpay.data.*;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static com.worldpay.service.model.payment.PaymentType.VISA;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderBuilderTest {

    private static final String ORDER_CONTENT = "orderContent";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String ORDER_CODE = "orderCode";
    private static final String INSTALLATION_ID = "installationId";
    private static final String STATEMENT_NARRATIVE = "STATEMENT NARRATIVE TEXT";
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final List<PaymentType> INCLUDED_PAYMENT_TYPES = singletonList(ONLINE);
    private static final List<PaymentType> EXCLUDED_PAYMENT_TYPES = singletonList(VISA);
    private static final List<String> INCLUDED_PAYMENT_TYPES_METHOD = singletonList(ONLINE.getMethodCode());
    private static final List<String> EXCLUDED_PAYMENT_TYPES_METHOD = singletonList(VISA.getMethodCode());
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReferenceNumber";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String SESSION_ID = "sessionId";
    private static final String SHOPPER_IP_ADDRESS = "shopperIPAddress";
    private Payment payment;
    private static final StoredCredentials STORED_CREDENTIALS = new StoredCredentials();
    private static final String ORDER_DESC = "Your Order & Order desc";
    private static final DynamicInteractionType DYNAMIC_INTERACTION_TYPE = DynamicInteractionType.ECOMMERCE;
    private PaymentDetails paymentDetails;
    private static final String ECHO_DATA = "echoData";
    private static final LineItem LINE_ITEM = new LineItem();
    private static final String ORDER_TAX_AMOUNT = "orderTaxAmount";
    private static final String TERMS_URL = "termsUrl";
    private OrderLines orderLines;
    private static final List<PaymentMethodAttribute> PAYMENT_METHOD_ATTRIBUTES = List.of(new PaymentMethodAttribute());
    private TokenRequest tokenRequest;
    private Address shippingAddress;
    private Address billingAddress;
    private Session session;
    private Shopper shopper;
    private Amount amount;
    private BasicOrderInfo basicOrderInfo;
    private static final String MANDATE_TYPE = "mandateType";
    private static final String CHECKOUT_ID = "checkoutId";

    @Mock
    private FraudSightData fraudSightDataMock;
    @Mock
    private BranchSpecificExtension level23DataMock;
    @Mock
    private GuaranteedPaymentsData guaranteedPaymentsDataMock;

    @Before
    public void setUp() throws Exception {
        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        this.tokenRequest = tokenRequest;

        final Amount amount = new Amount();
        amount.setExponent("2");
        amount.setCurrencyCode("EUR");
        amount.setValue("100");
        this.amount = amount;

        final BasicOrderInfo basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setOrderCode(ORDER_CODE);
        basicOrderInfo.setDescription(ORDER_DESC);
        basicOrderInfo.setAmount(amount);
        this.basicOrderInfo = basicOrderInfo;

        final Address address = new Address();
        address.setFirstName(FIRST_NAME);
        address.setLastName(LAST_NAME);
        address.setAddress1(SHOPPER_ADDRESS_1);
        address.setAddress2(SHOPPER_ADDRESS_2);
        address.setAddress3(SHOPPER_ADDRESS_3);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);

        this.shippingAddress = address;
        this.billingAddress = address;

        final Session session = new Session();
        session.setId(SESSION_ID);
        session.setShopperIPAddress(SHOPPER_IP_ADDRESS);
        this.session = session;

        final Shopper shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setSession(session);
        this.shopper = shopper;

        final Cse cse = new Cse();
        cse.setEncryptedData(ENCRYPTED_DATA);
        cse.setAddress(shippingAddress);
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());
        payment = cse;

        final PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPayment(payment);
        paymentDetails.setSession(this.session);
        paymentDetails.setStoredCredentials(STORED_CREDENTIALS);
        this.paymentDetails = paymentDetails;

        final OrderLines orderLines = new OrderLines();
        orderLines.setLineItems(List.of(LINE_ITEM));
        orderLines.setTermsURL(TERMS_URL);
        orderLines.setOrderTaxAmount(ORDER_TAX_AMOUNT);
        this.orderLines = orderLines;
    }

    @Test
    public void build_ShouldCreateOrderWithAllParameters_WhenAllRequestParametersAreProvided() {
        final Order result = createOrder();

        verifyOrder(result);
    }

    private void verifyOrder(final Order order) {
        assertEquals(ORDER_CODE, order.getOrderCode());
        assertEquals(ORDER_DESC, order.getDescription());
        assertEquals(amount, order.getAmount());
        assertNotNull(order.getPaymentDetails());
        assertEquals(payment, order.getPaymentDetails().getPayment());
        assertEquals(session, order.getPaymentDetails().getSession());
        assertEquals(STORED_CREDENTIALS, order.getPaymentDetails().getStoredCredentials());
        assertEquals(INSTALLATION_ID, order.getInstallationId());
        assertEquals(INCLUDED_PAYMENT_TYPES_METHOD, order.getPaymentMethodMask().getIncludes());
        assertEquals(EXCLUDED_PAYMENT_TYPES_METHOD, order.getPaymentMethodMask().getExcludes());
        assertEquals(ORDER_CONTENT, order.getOrderContent());
        assertEquals(shopper, order.getShopper());
        assertEquals(tokenRequest, order.getTokenRequest());
        assertEquals(shippingAddress, order.getShippingAddress());
        assertEquals(billingAddress, order.getBillingAddress());
        assertEquals(STATEMENT_NARRATIVE, order.getStatementNarrative());
        assertEquals(DYNAMIC_INTERACTION_TYPE, order.getDynamicInteractionType());
        assertEquals(ECHO_DATA, order.getEchoData());
        assertEquals(orderLines, order.getOrderLines());
        assertEquals(PAYMENT_METHOD_ATTRIBUTES, order.getPaymentMethodAttributes());
        assertEquals(fraudSightDataMock, order.getFraudSightData());
        assertEquals(level23DataMock, order.getBranchSpecificExtension());
        assertEquals(MANDATE_TYPE, order.getMandateType());
        assertEquals(guaranteedPaymentsDataMock, order.getGuaranteedPaymentsData());
        assertEquals(CHECKOUT_ID, order.getCheckoutId());
    }

    private Order createOrder() {
        return new OrderBuilder()
            .withOrderInfo(basicOrderInfo)
            .withShopper(shopper)
            .withInstallationId(INSTALLATION_ID)
            .withOrderContent(ORDER_CONTENT)
            .withTokenRequest(tokenRequest)
            .withShippingAddress(shippingAddress)
            .withBillingAddress(billingAddress)
            .withStatementNarrative(STATEMENT_NARRATIVE)
            .withExcludedPaymentMethods(EXCLUDED_PAYMENT_TYPES)
            .withIncludedPaymentMethods(INCLUDED_PAYMENT_TYPES)
            .withDynamicInteractionType(DYNAMIC_INTERACTION_TYPE)
            .withPaymentDetails(paymentDetails)
            .withEchoData(ECHO_DATA)
            .withOrderLines(orderLines)
            .withPaymentMethodAttribute(PAYMENT_METHOD_ATTRIBUTES)
            .withFraudSightAttribute(fraudSightDataMock)
            .withLevel23Data(level23DataMock)
            .withMandateType(MANDATE_TYPE)
            .whitGuaranteedPaymentsAttribute(guaranteedPaymentsDataMock)
            .whitCheckoutId(CHECKOUT_ID)
            .build();
    }
}
