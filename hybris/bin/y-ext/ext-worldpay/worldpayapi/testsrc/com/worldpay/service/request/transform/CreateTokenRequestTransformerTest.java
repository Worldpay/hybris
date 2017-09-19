package com.worldpay.service.request.transform;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CSEDATA;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.WorldpayTestConfigHelper;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequest;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@UnitTest
public class CreateTokenRequestTransformerTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final Address BILLING_ADDRESS = new Address(FIRST_NAME, LAST_NAME, null, null, null, POSTAL_CODE, CITY, GB);
    private static final WorldpayConfig WORLDPAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo("MERCHANT_CODE", "MERCHANT_PASSWORD");
    private static final String AUTH_SHOPPER_ID = "authShopperID";
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final Payment PAYMENT = PaymentBuilder.createCSE(ENCRYPTED_DATA, BILLING_ADDRESS);
    private static final TokenRequest TOKEN_REQUEST = new TokenRequest(TOKEN_REFERENCE, TOKEN_REASON);

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CreateTokenRequestTransformer testObj = new CreateTokenRequestTransformer();

    @Test
    public void transformShouldRaiseErrorWhenRequestIsNull() throws WorldpayModelTransformationException {
        expectedException.expect(WorldpayModelTransformationException.class);
        expectedException.expectMessage("Request provided to create token is invalid.");
        final ServiceRequest serviceRequest = null;
        testObj.transform(serviceRequest);
    }

    @Test
    public void transformShouldCreatePaymentServiceRequest() throws WorldpayModelTransformationException {
        final ServiceRequest serviceRequest = createTokenRequest(WORLDPAY_CONFIG, MERCHANT_INFO, AUTH_SHOPPER_ID, PAYMENT, TOKEN_REQUEST);
        final PaymentService result = testObj.transform(serviceRequest);

        final Submit submit = (Submit) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final PaymentTokenCreate paymentTokenCreate = (PaymentTokenCreate) submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
        final CSEDATA cseData = (CSEDATA) paymentTokenCreate.getPaymentInstrumentOrCSEDATA().get(0);

        assertEquals(MERCHANT_INFO.getMerchantCode(), result.getMerchantCode());
        assertEquals(WORLDPAY_CONFIG.getVersion(), result.getVersion());
        assertEquals(AUTH_SHOPPER_ID, paymentTokenCreate.getAuthenticatedShopperID());
        assertEquals(TOKEN_REFERENCE, paymentTokenCreate.getCreateToken().getTokenEventReference());
        assertEquals(TOKEN_REASON, paymentTokenCreate.getCreateToken().getTokenReason().getvalue());
        assertEquals(ENCRYPTED_DATA, cseData.getEncryptedData());
        assertThat(BILLING_ADDRESS.transformToInternalModel(), samePropertyValuesAs(cseData.getCardAddress().getAddress()));
    }
}
