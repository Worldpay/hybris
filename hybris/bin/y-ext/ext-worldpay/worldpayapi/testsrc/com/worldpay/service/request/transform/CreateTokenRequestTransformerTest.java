package com.worldpay.service.request.transform;

import com.worldpay.data.Address;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.token.CardTokenRequest;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequestForShopperToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreateTokenRequestTransformerTest {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Shopper";
    private static final String POSTAL_CODE = "postalCode";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_REFERENCE = "tokenReference";

    private static final String MERCHANT_CODE = "MERCHANT_CODE";
    private static final String MERCHANT_PASSWORD = "MERCHANT_PASSWORD";
    private static final String AUTH_SHOPPER_ID = "authShopperID";
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final String VERSION = "1.4";

    private TokenRequest tokenRequest;
    private MerchantInfo merchantInfo;

    @InjectMocks
    private CreateTokenRequestTransformer testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private Converter<CardTokenRequest, PaymentTokenCreate> internalPaymentTokenCreateConverterMock;

    @Mock
    private PaymentTokenCreate paymentTokenCreateMock;

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;

        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_REFERENCE);
        this.tokenRequest = tokenRequest;

        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
        testObj = new CreateTokenRequestTransformer(configurationServiceMock, internalPaymentTokenCreateConverterMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transformShouldRaiseErrorWhenRequestIsNull() throws WorldpayModelTransformationException {
        testObj.transform(null);
    }

    @Test
    public void transformShouldCreatePaymentServiceRequest() throws WorldpayModelTransformationException {

        final Address address = new Address();
        address.setFirstName(FIRST_NAME);
        address.setLastName(LAST_NAME);
        address.setPostalCode(POSTAL_CODE);
        address.setCity(CITY);
        address.setCountryCode(GB);

        final Cse cse = new Cse();
        cse.setEncryptedData(ENCRYPTED_DATA);
        cse.setAddress(address);
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());

        final ServiceRequest serviceRequest = createTokenRequestForShopperToken(merchantInfo, AUTH_SHOPPER_ID, cse, tokenRequest);
        final CreateTokenServiceRequest tokenRequest = (CreateTokenServiceRequest) serviceRequest;

        when(internalPaymentTokenCreateConverterMock.convert(tokenRequest.getCardTokenRequest())).thenReturn(paymentTokenCreateMock);

        final PaymentService result = testObj.transform(serviceRequest);

        final Submit submit = (Submit) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final PaymentTokenCreate paymentTokenCreate = (PaymentTokenCreate) submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge().get(0);

        assertThat(result.getMerchantCode()).isEqualTo(merchantInfo.getMerchantCode());
        assertThat(result.getVersion()).isEqualTo(VERSION);
        assertThat(paymentTokenCreate).isEqualTo(paymentTokenCreateMock);
    }

}
