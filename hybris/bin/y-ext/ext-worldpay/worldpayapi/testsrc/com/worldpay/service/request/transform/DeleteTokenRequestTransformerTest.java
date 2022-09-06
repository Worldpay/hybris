package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenDelete;
import com.worldpay.data.token.DeleteTokenRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
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
public class DeleteTokenRequestTransformerTest {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String VERSION = "1.4";

    @InjectMocks
    private DeleteTokenRequestTransformer testObj;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DeleteTokenServiceRequest serviceRequestMock;
    @Mock
    private Converter<DeleteTokenRequest, PaymentTokenDelete> internalPaymentTokenDeleteConverter;

    @Mock
    private PaymentTokenDelete paymentTokenDeleteMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @Before
    public void setUp() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void shouldRaiseExceptionWhenServiceRequestIsNull() throws Exception {
        testObj.transform(null);
    }

    @Test
    public void shouldReturnPaymentServiceWithPaymentTokenDelete() throws Exception {
        when(internalPaymentTokenDeleteConverter.convert(serviceRequestMock.getDeleteTokenRequest())).thenReturn(paymentTokenDeleteMock);
        when(serviceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);

        final PaymentService result = testObj.transform(serviceRequestMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(VERSION, result.getVersion());
        assertTrue(result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Modify);
        final Modify modify = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        assertTrue(modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0) instanceof PaymentTokenDelete);
        final PaymentTokenDelete paymentTokenDelete = (PaymentTokenDelete) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
        assertEquals(paymentTokenDeleteMock, paymentTokenDelete);
    }
}
