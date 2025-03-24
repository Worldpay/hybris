package com.worldpay.service.request.transform;

import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VoidRequestTransformerTest {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private static final String VERSION = "1";
    private static final String ORDER_CODE = "1232456789";
    private static final String MERCHANT_CODE = "merchCode";

    @InjectMocks
    private VoidRequestTransformer testObj;

    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock
    private Configuration configurationMock;
    @Mock
    private ServiceRequest serviceRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    @Before
    public void setUp() {
        when(serviceRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(serviceRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
    }

    @Test
    public void transform() throws WorldpayModelTransformationException {
        final PaymentService result = testObj.transform(serviceRequestMock);
        final Modify modify = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderModification orderModification = (OrderModification) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDeleteOrDeleteNetworkPaymentToken().get(0);

        assertEquals(VERSION, result.getVersion());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(ORDER_CODE, orderModification.getOrderCode());
    }

    @Test
    public void transform_shouldThrowException_WhenServiceRequestIsNull() {
        assertThrows(WorldpayModelTransformationException.class, () -> testObj.transform(null));
    }
}
