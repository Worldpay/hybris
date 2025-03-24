package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Refund;
import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RefundRequestTransformerTest {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String CONFIG_VERSION = "1.4";
    private static final String REFERENCE = "reference";
    private static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private RefundRequestTransformer testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    protected Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverterMock;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RefundServiceRequest requestMock;
    @Mock
    private com.worldpay.internal.model.Amount internalAmountMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(CONFIG_VERSION);
        when(requestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(requestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(requestMock.getOrderCode()).thenReturn(ORDER_CODE);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenServiceRequestIsNull_ShouldRaiseException() throws Exception {
        testObj.transform(null);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenMerchantCodeIsNull_ShouldRaiseException() throws Exception {
        when(requestMock.getMerchantInfo()).thenReturn(null);

        testObj.transform(requestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenOrderCodeIsNull_ShouldRaiseException() throws Exception {
        when(requestMock.getOrderCode()).thenReturn(null);

        testObj.transform(requestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenAmountIsNull_ShouldRaiseException() throws Exception {
        when(requestMock.getAmount()).thenReturn(null);

        testObj.transform(requestMock);
    }

    @Test
    public void transform_ShouldReturnPaymentService() throws Exception {
        when(internalAmountConverterMock.convert(requestMock.getAmount())).thenReturn(internalAmountMock);
        when(internalAmountConverterMock.convert(requestMock.getAmount())).thenReturn(internalAmountMock);
        when(requestMock.getReference()).thenReturn(REFERENCE);
        when(requestMock.getShopperWebformRefund()).thenReturn(Boolean.TRUE);

        final PaymentService result = testObj.transform(requestMock);

        final Modify modify = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderModification orderModification = (OrderModification) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDeleteOrDeleteNetworkPaymentToken().get(0);
        final Refund refund = (Refund) orderModification.getCancelOrCaptureOrProvideCryptogramOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSaleOrApprove().get(0);

        assertThat(refund.getAmount()).isEqualTo(internalAmountMock);
        assertThat(refund.getReference()).isEqualTo(REFERENCE);
        assertThat(refund.getShopperWebformRefund()).isEqualTo(Boolean.TRUE.toString());
        assertThat(result.getMerchantCode()).isEqualTo(MERCHANT_CODE);
        assertThat(result.getVersion()).isEqualTo(CONFIG_VERSION);
        assertThat(orderModification.getOrderCode()).isEqualTo(ORDER_CODE);
    }
}
