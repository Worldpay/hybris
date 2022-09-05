package com.worldpay.util;

import com.worldpay.data.Date;
import com.worldpay.data.PaymentDetails;
import com.worldpay.data.Session;
import com.worldpay.data.Shopper;
import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.data.klarna.KlarnaRedirectURLs;
import com.worldpay.data.payment.*;
import com.worldpay.enums.PaymentAction;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayInternalModelTransformerUtilTest {

    private static final String COUNTRY_CODE = "ES";
    private static final String SHOPPER_LOCALE = "shopperLocale";
    private static final String EXTRA_MERCHANT_DATA = "extraMerchantData";
    private static final String SHOPPER_BANK_CODE = "shopperBankCode";
    private static final String SUCCESS_URL = "successURL";
    private static final String FAILURE_URL = "failureURL";
    private static final String PENDING_URL = "pendingURL";
    private static final String CANCEL_URL = "cancelURL";

    @InjectMocks
    private WorldpayInternalModelTransformerUtil testObj;


    private Payment payment = new Cse();
    private Shopper shopper = new Shopper();
    private StoredCredentials storedCredentials = new StoredCredentials();
    private PaymentAction action = PaymentAction.AUTHORISE;
    private KlarnaRedirectURLs klarnaRedirectURLs = new KlarnaRedirectURLs();

    @Before
    public void setUp() throws Exception {
        klarnaRedirectURLs.setSuccessURL(SUCCESS_URL);
        klarnaRedirectURLs.setCancelURL(CANCEL_URL);
        klarnaRedirectURLs.setPendingURL(PENDING_URL);
        klarnaRedirectURLs.setFailureURL(FAILURE_URL);
    }

    @Test
    public void newDateFromLocalDateTime_ShouldReturnDate() {
        final LocalDateTime sourceMock = LocalDateTime.of(2021, 1, 1, 1, 1, 1);

        final Date result = testObj.newDateFromLocalDateTime(sourceMock);

        assertThat(result.getDayOfMonth()).isEqualTo("1");
        assertThat(result.getYear()).isEqualTo("2021");
        assertThat(result.getSecond()).isEqualTo("1");
        assertThat(result.getMonth()).isEqualTo("1");
        assertThat(result.getMinute()).isEqualTo("1");
        assertThat(result.getHour()).isEqualTo("1");

    }

    @Test
    public void createAlternativePayment_ShouldReturnAlternativePayment() {

        final AlternativePayment result = testObj.createAlternativePayment(PaymentType.IDEAL, SUCCESS_URL, FAILURE_URL, CANCEL_URL, PENDING_URL, COUNTRY_CODE);

        assertThat(result.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(result.getPendingURL()).isEqualTo(PENDING_URL);
        assertThat(result.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(result.getSuccessURL()).isEqualTo(SUCCESS_URL);
        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.IDEAL.getMethodCode());
    }

    @Test
    public void createAlternativeShopperBankCodePayment() {
        final AlternativeShopperBankCodePayment result = testObj.createAlternativeShopperBankCodePayment(PaymentType.IDEAL, SHOPPER_BANK_CODE, SUCCESS_URL, FAILURE_URL, CANCEL_URL, PENDING_URL, COUNTRY_CODE);

        assertThat(result.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(result.getPendingURL()).isEqualTo(PENDING_URL);
        assertThat(result.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(result.getSuccessURL()).isEqualTo(SUCCESS_URL);
        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getShopperBankCode()).isEqualTo(SHOPPER_BANK_CODE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.IDEAL.getMethodCode());
    }

    @Test
    public void createPaymentDetailsFromRequestParameters_ShouldReturnPaymentDetails() {
        shopper.setSession(new Session());
        final AuthoriseRequestParameters authoriseRequestParameters = AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withPayment(payment)
            .withShopper(shopper)
            .withStoredCredentials(storedCredentials)
            .withPaymentDetailsAction(action)
            .build();

        final PaymentDetails result = testObj.createPaymentDetailsFromRequestParameters(authoriseRequestParameters);

        assertThat(result.getAction()).isEqualTo(PaymentAction.AUTHORISE);
        assertThat(result.getPayment()).isEqualTo(payment);
        assertThat(result.getSession()).isEqualTo(shopper.getSession());
        assertThat(result.getStoredCredentials()).isEqualTo(storedCredentials);
    }

    @Test
    public void createKlarnaPayment_ShouldReturnKLARNAV2SSL() {
        final KlarnaPayment result = testObj.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, EXTRA_MERCHANT_DATA, PaymentType.KLARNAV2SSL.getMethodCode(), klarnaRedirectURLs);

        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(result.getLocale()).isEqualTo(SHOPPER_LOCALE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNAV2SSL.getMethodCode());
        assertThat(result.getCancelURL()).isEqualTo(klarnaRedirectURLs.getCancelURL());
        assertThat(result.getFailureURL()).isEqualTo(klarnaRedirectURLs.getFailureURL());
        assertThat(result.getPendingURL()).isEqualTo(klarnaRedirectURLs.getPendingURL());
        assertThat(result.getSuccessURL()).isEqualTo(klarnaRedirectURLs.getSuccessURL());
    }

    @Test
    public void createKlarnaPayment_ShouldReturnKLARNAPAYNOWSSL() {
        final KlarnaPayment result = testObj.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, EXTRA_MERCHANT_DATA, PaymentType.KLARNAPAYNOWSSL.getMethodCode(), klarnaRedirectURLs);

        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(result.getLocale()).isEqualTo(SHOPPER_LOCALE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNAPAYNOWSSL.getMethodCode());
        assertThat(result.getCancelURL()).isEqualTo(klarnaRedirectURLs.getCancelURL());
        assertThat(result.getFailureURL()).isEqualTo(klarnaRedirectURLs.getFailureURL());
        assertThat(result.getPendingURL()).isEqualTo(klarnaRedirectURLs.getPendingURL());
        assertThat(result.getSuccessURL()).isEqualTo(klarnaRedirectURLs.getSuccessURL());
    }

    @Test
    public void createKlarnaPayment_ShouldReturnKLARNASLICESSL() {
        final KlarnaPayment result = testObj.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, EXTRA_MERCHANT_DATA, PaymentType.KLARNASLICESSL.getMethodCode(), klarnaRedirectURLs);

        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(result.getLocale()).isEqualTo(SHOPPER_LOCALE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNASLICESSL.getMethodCode());
        assertThat(result.getCancelURL()).isEqualTo(klarnaRedirectURLs.getCancelURL());
        assertThat(result.getFailureURL()).isEqualTo(klarnaRedirectURLs.getFailureURL());
        assertThat(result.getPendingURL()).isEqualTo(klarnaRedirectURLs.getPendingURL());
        assertThat(result.getSuccessURL()).isEqualTo(klarnaRedirectURLs.getSuccessURL());
    }

    @Test
    public void createKlarnaPayment_ShouldReturnKLARNAPAYLATERSSL() {
        final KlarnaPayment result = testObj.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, EXTRA_MERCHANT_DATA, PaymentType.KLARNAPAYLATERSSL.getMethodCode(), klarnaRedirectURLs);

        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(result.getLocale()).isEqualTo(SHOPPER_LOCALE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNAPAYLATERSSL.getMethodCode());
        assertThat(result.getCancelURL()).isEqualTo(klarnaRedirectURLs.getCancelURL());
        assertThat(result.getFailureURL()).isEqualTo(klarnaRedirectURLs.getFailureURL());
        assertThat(result.getPendingURL()).isEqualTo(klarnaRedirectURLs.getPendingURL());
        assertThat(result.getSuccessURL()).isEqualTo(klarnaRedirectURLs.getSuccessURL());
    }
}
