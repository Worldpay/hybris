package com.worldpay.service.request.validation.impl;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;
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
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Collections.singletonList;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayXMLValidatorTest {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String EMAIL_ADDRESS = "jshopper@myprovider.com";
    private static final String SUCCESS_URL = "http://successURL.com";
    private static final String CANCEL_URL = "http://cancelURL.com";
    private static final String PENDING_URL = "http://pendingURL.com";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String TOKEN_REASON = "tokenReason";

    private static final Shopper SHOPPER = new Shopper(EMAIL_ADDRESS, null, null, null);
    private static final String AUTH_SHOPPER_ID = "authShopperId";

    private MerchantInfo merchantInfo;
    private BasicOrderInfo basicOrderInfo;
    private Address shippingAddress;
    private Address billingAddress;
    private Address cardAddress;

    @InjectMocks
    private AuthoriseRequestTransformer testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    private static final String ORDER_CONTENT = "orderContent";
    private static final WorldpayXMLValidator VALIDATOR = new DefaultWorldpayXMLValidator();

    @Before
    public void setUp() {
        merchantInfo = new MerchantInfo("MERCHANT_CODE", "MERCHANT_PASSWORD");
        basicOrderInfo = new BasicOrderInfo("DS1347889928107_3", "Your Order & Order desc", new Amount("100", "EUR", "2"));
        shippingAddress = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
        billingAddress = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
        cardAddress = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn("1.4");
    }

    @Test
    public void testValidate() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT);

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    @Test
    public void testValidateXMLWithCreateTokenWithShopperScope() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_REFERENCE, TOKEN_REASON, false);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT, tokenRequest);

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    @Test
    public void testValidateXMLWithCreateTokenWithMerchantScope() throws WorldpayValidationException, WorldpayModelTransformationException {
        final List<PaymentType> includedPTs = singletonList(ONLINE);
        final Shopper shopper = new Shopper(EMAIL_ADDRESS, AUTH_SHOPPER_ID, null, null);
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_REFERENCE, TOKEN_REASON, true);
        final AuthoriseServiceRequest request = RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(merchantInfo, basicOrderInfo, null, ORDER_CONTENT,
                includedPTs, null, shopper, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT, tokenRequest);

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    @Test
    public void testValidatePayPal() throws WorldpayValidationException, WorldpayModelTransformationException {
        final AlternativePayment payPal = PaymentBuilder.createPAYPALEXPRESS("false", SUCCESS_URL, CANCEL_URL, PENDING_URL);
        final AuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, payPal,
                SHOPPER, null, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT, DynamicInteractionType.ECOMMERCE);

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }

    @Test
    public void testValidateVISASSL() throws WorldpayModelTransformationException, WorldpayValidationException {
        final Date date = new Date(LocalDateTime.now());
        final Card visaSsl = PaymentBuilder.createVISASSL("4111111111111111", date, "Mr J Shopper", "123", cardAddress);
        final AuthoriseServiceRequest request = DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(merchantInfo, basicOrderInfo, visaSsl,
                SHOPPER, null, shippingAddress, billingAddress, STATEMENT_NARRATIVE_TEXT, DynamicInteractionType.ECOMMERCE);

        final PaymentService paymentService = testObj.transform(request);

        VALIDATOR.validate(paymentService);
    }
}
