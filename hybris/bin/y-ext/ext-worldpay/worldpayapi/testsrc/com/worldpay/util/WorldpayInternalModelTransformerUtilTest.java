package com.worldpay.util;

import com.worldpay.data.Date;
import com.worldpay.data.PaymentDetails;
import com.worldpay.data.Session;
import com.worldpay.data.Shopper;
import com.worldpay.data.klarna.KlarnaMerchantUrls;
import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.data.klarna.KlarnaRedirectURLs;
import com.worldpay.data.payment.*;
import com.worldpay.enums.PaymentAction;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayInternalModelTransformerUtilTest {

    private static final String CHECKOUT_URL = "checkoutURL";
    private static final String CONFIRMATION_URL = "confirmationURL";
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
    public void createKlarnaPayment_ShouldRetunrKLARNASSL() {
        final KlarnaMerchantUrls merchantUrls = new KlarnaMerchantUrls();
        merchantUrls.setCheckoutURL(CHECKOUT_URL);
        merchantUrls.setConfirmationURL(CONFIRMATION_URL);

        final KlarnaPayment result = testObj.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, merchantUrls, EXTRA_MERCHANT_DATA);

        assertThat(result.getPurchaseCountry()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getMerchantUrls().getCheckoutURL()).isEqualTo(merchantUrls.getCheckoutURL());
        assertThat(result.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(result.getShopperLocale()).isEqualTo(SHOPPER_LOCALE);
    }

    @Test
    public void createKlarnaPayment_ShouldRetunrKLARNAPAYNOWSSL() {
        final KlarnaRedirectURLs klarnaRedirectURLs = new KlarnaRedirectURLs();
        klarnaRedirectURLs.setSuccessURL(SUCCESS_URL);
        klarnaRedirectURLs.setCancelURL(CANCEL_URL);
        klarnaRedirectURLs.setPendingURL(PENDING_URL);
        klarnaRedirectURLs.setFailureURL(FAILURE_URL);

        final KlarnaPayment result = testObj.createKlarnaPayment(COUNTRY_CODE, SHOPPER_LOCALE, EXTRA_MERCHANT_DATA, PaymentType.KLARNAPAYNOWSSL.getMethodCode(), klarnaRedirectURLs);

        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(result.getLocale()).isEqualTo(SHOPPER_LOCALE);
        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNAPAYNOWSSL.getMethodCode());
        assertThat(result.getSuccessURL()).isEqualTo(SUCCESS_URL);
    }
}
