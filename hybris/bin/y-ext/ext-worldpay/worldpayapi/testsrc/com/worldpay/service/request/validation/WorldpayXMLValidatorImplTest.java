package com.worldpay.service.request.validation;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.WorldpayTestConfigHelper;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.AlternativePayment;
import com.worldpay.service.model.payment.Card;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.AuthoriseServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.request.transform.AuthoriseRequestTransformer;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;

@UnitTest
public class WorldpayXMLValidatorImplTest {

    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String SUCCESS_URL = "http://successURL.com";
    private static final String CANCEL_URL = "http://cancelURL.com";
    private static final String PENDING_URL = "http://pendingURL.com";
    private static final String FAILURE_URL = "http://failureURL.com";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String TOKEN_REASON = "tokenReason";

    private static final Shopper SHOPPER = new Shopper(EMAIL_ADDRESS, null, null, null);
    public static final String AUTH_SHOPPER_ID = "authShopperId";

    private MerchantInfo merchantInfo;
    private WorldpayConfig worldpayConfig;
    private BasicOrderInfo basicOrderInfo;
    private Address shippingAddress;
    private Address billingAddress;
    private Address cardAddress;

    private static final String ORDER_CONTENT = "orderContent";

    @Before
    public void setUp() {
        merchantInfo = new MerchantInfo("MERCHANT_CODE", "MERCHANT_PASSWORD");
        worldpayConfig = WorldpayTestConfigHelper.getWorldpayTestConfig();
        basicOrderInfo = new BasicOrderInfo("DS1347889928107_3", "Your Order & Order desc", new Amount("100", "EUR", "2"));
        shippingAddress = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
        billingAddress = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
        cardAddress = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    }

    @Test
    public void testValidate() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT);

        final PaymentService paymentService = new AuthoriseRequestTransformer().transform(request);

        final WorldpayXMLValidator validator = new WorldpayXMLValidatorImpl();
        validator.validate(paymentService);
    }

    @Test
    public void testValidateXMLWithCreateToken() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_REFERENCE, TOKEN_REASON);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT, tokenRequest);

        final PaymentService paymentService = new AuthoriseRequestTransformer().transform(request);

        final WorldpayXMLValidator validator = new WorldpayXMLValidatorImpl();
        validator.validate(paymentService);
    }

    @Test
    public void testValidateVMESSL() throws WorldpayValidationException, WorldpayModelTransformationException {
        final AlternativePayment vmeSsl = PaymentBuilder.createVMESSL(SUCCESS_URL, FAILURE_URL, CANCEL_URL, cardAddress);
        final AuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, vmeSsl,
                SHOPPER, null, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT);

        final PaymentService paymentService = new AuthoriseRequestTransformer().transform(request);

        final WorldpayXMLValidator validator = new WorldpayXMLValidatorImpl();
        validator.validate(paymentService);
    }

    @Test
    public void testValidatePayPal() throws WorldpayValidationException, WorldpayModelTransformationException {
        final AlternativePayment payPal = PaymentBuilder.createPAYPALEXPRESS("false", SUCCESS_URL, CANCEL_URL, PENDING_URL);
        final AuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, payPal,
                SHOPPER, null, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT);

        final PaymentService paymentService = new AuthoriseRequestTransformer().transform(request);

        final WorldpayXMLValidator validator = new WorldpayXMLValidatorImpl();
        validator.validate(paymentService);
    }

    @Test
    public void testValidateVISASSL() throws WorldpayModelTransformationException, WorldpayValidationException {
        final Date date = new Date(new java.util.Date());
        final Card visaSsl = PaymentBuilder.createVISASSL("4111111111111111", date, "Mr J Shopper", "123", cardAddress);
        final AuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, visaSsl,
                SHOPPER, null, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT);

        final PaymentService paymentService = new AuthoriseRequestTransformer().transform(request);

        final WorldpayXMLValidator validator = new WorldpayXMLValidatorImpl();
        validator.validate(paymentService);
    }
}
