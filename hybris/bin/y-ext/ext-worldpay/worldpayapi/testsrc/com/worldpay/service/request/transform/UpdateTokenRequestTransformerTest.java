package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenUpdate;
import com.worldpay.data.token.UpdateTokenRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateTokenRequestTransformerTest {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private static final String MERCHANT_CODE = "merchantCode";

    @InjectMocks
    private UpdateTokenRequestTransformer testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    protected Converter<UpdateTokenRequest, PaymentTokenUpdate> internalPaymentTokenUpdateConverterMock;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private UpdateTokenServiceRequest serviceRequestMock;
    @Mock
    private PaymentTokenUpdate paymentTokenUpdateMock;

    @Before
    public void setUp() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn("1.4");
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void shouldRaiseExceptionWhenServiceRequestIsNull() throws Exception {
        testObj.transform(null);
    }

    @Test
    public void shouldReturnPaymentServiceWithPaymentTokenUpdate() throws Exception {
        when(internalPaymentTokenUpdateConverterMock.convert(serviceRequestMock.getUpdateTokenRequest())).thenReturn(paymentTokenUpdateMock);
        when(serviceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);

        final PaymentService result = testObj.transform(serviceRequestMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertTrue(result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Modify);
        final Modify modify = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        assertTrue(modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0) instanceof PaymentTokenUpdate);
        final PaymentTokenUpdate paymentTokenUpdate = (PaymentTokenUpdate) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
        assertEquals(paymentTokenUpdateMock, paymentTokenUpdate);
    }
}
