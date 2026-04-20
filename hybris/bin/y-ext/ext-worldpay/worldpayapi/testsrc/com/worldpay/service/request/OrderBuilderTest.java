package com.worldpay.service.request;

import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static com.worldpay.service.model.payment.PaymentType.VISA;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.worldpay.data.Address;
import com.worldpay.data.AlternativeShippingAddress;
import com.worldpay.data.Amount;
import com.worldpay.data.BasicOrderInfo;
import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.Exemption;
import com.worldpay.data.FraudSightData;
import com.worldpay.data.GuaranteedPaymentsData;
import com.worldpay.data.LineItem;
import com.worldpay.data.Order;
import com.worldpay.data.OrderLines;
import com.worldpay.data.PaymentDetails;
import com.worldpay.data.PaymentMethodAttribute;
import com.worldpay.data.Session;
import com.worldpay.data.Shopper;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class OrderBuilderTest {

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
    @Mock
    private AlternativeShippingAddress alternativeShippingAddressMock;
    @Mock
    private Exemption exemptionMock;

    @BeforeEach
    void setUp() throws Exception {
        tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);

        amount = new Amount();
        amount.setExponent("2");
        amount.setCurrencyCode("EUR");
        amount.setValue("100");

        basicOrderInfo = new BasicOrderInfo();
        basicOrderInfo.setOrderCode(ORDER_CODE);
        basicOrderInfo.setDescription(ORDER_DESC);
        basicOrderInfo.setAmount(amount);

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

        session = new Session();
        session.setId(SESSION_ID);
        session.setShopperIPAddress(SHOPPER_IP_ADDRESS);

        shopper = new Shopper();
        shopper.setShopperEmailAddress(EMAIL_ADDRESS);
        shopper.setSession(session);

        final Cse cse = new Cse();
        cse.setEncryptedData(ENCRYPTED_DATA);
        cse.setAddress(shippingAddress);
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());
        payment = cse;

        paymentDetails = new PaymentDetails();
        paymentDetails.setPayment(payment);
        paymentDetails.setSession(this.session);
        paymentDetails.setStoredCredentials(STORED_CREDENTIALS);

        orderLines = new OrderLines();
        orderLines.setLineItems(List.of(LINE_ITEM));
        orderLines.setTermsURL(TERMS_URL);
        orderLines.setOrderTaxAmount(ORDER_TAX_AMOUNT);
    }

    @Test
    void build_ShouldCreateOrderWithAllParameters_WhenAllRequestParametersAreProvided() {
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
        assertEquals(alternativeShippingAddressMock, order.getAlternativeShippingAddress());
        assertEquals(CHECKOUT_ID, order.getCheckoutId());
        assertEquals(exemptionMock, order.getExemption());
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
                .withGuaranteedPaymentsAttribute(guaranteedPaymentsDataMock)
                .withAlternativeShippingAddress(alternativeShippingAddressMock)
                .withCheckoutId(CHECKOUT_ID)
                .withExemption(exemptionMock)
                .build();
    }
}
