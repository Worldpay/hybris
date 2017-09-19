package com.worldpay.service.request.transform;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Inquiry;
import com.worldpay.internal.model.KlarnaConfirmationInquiry;
import com.worldpay.internal.model.OrderInquiry;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderInquiryRequestTransformerTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String VERSION = "version";
    private static final String MERCHANT_CODE = "merchantCode";
    @InjectMocks
    private OrderInquiryRequestTransformer testObj;
    @Mock
    private OrderInquiryServiceRequest orderInquiryRequestMock;
    @Mock
    private KlarnaOrderInquiryServiceRequest klarnaOrderInquiryRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;

    @Test
    public void transformOrderInquiry() throws WorldpayModelTransformationException {
        when(orderInquiryRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(orderInquiryRequestMock.getWorldpayConfig()).thenReturn(worldpayConfigMock);
        when(worldpayConfigMock.getVersion()).thenReturn(VERSION);
        when(orderInquiryRequestMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        final PaymentService result = testObj.transform(orderInquiryRequestMock);

        assertThat(result.getMerchantCode()).isEqualTo(MERCHANT_CODE);
        assertThat(result.getVersion()).isEqualTo(VERSION);
        final Object inquiry = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        assertThat(inquiry).isInstanceOf(Inquiry.class);
        final Inquiry inquiryObject = (Inquiry) inquiry;
        final Object orderInquiry = inquiryObject.getOrderInquiryOrKlarnaConfirmationInquiryOrBatchInquiryOrAccountBatchInquiryOrRefundableAmountInquiryOrShopperAuthenticationOrPriceInquiryOrBankAccountInquiryOrIdentifyMeInquiryOrPaymentOptionsInquiryOrPaymentTokenInquiryOrShopperTokenRetrieval().get(0);
        assertThat(orderInquiry).isInstanceOf(OrderInquiry.class);
        final OrderInquiry orderInquiryObject = (OrderInquiry) orderInquiry;
        assertThat(orderInquiryObject.getOrderCode()).isEqualTo(WORLDPAY_ORDER_CODE);
    }


    @Test
    public void transformKlarnaOrderInquiry() throws WorldpayModelTransformationException {
        when(klarnaOrderInquiryRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(klarnaOrderInquiryRequestMock.getWorldpayConfig()).thenReturn(worldpayConfigMock);
        when(worldpayConfigMock.getVersion()).thenReturn(VERSION);
        when(klarnaOrderInquiryRequestMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        final PaymentService result = testObj.transform(klarnaOrderInquiryRequestMock);

        assertThat(result.getMerchantCode()).isEqualTo(MERCHANT_CODE);
        assertThat(result.getVersion()).isEqualTo(VERSION);
        final Object inquiry = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        assertThat(inquiry).isInstanceOf(Inquiry.class);
        final Inquiry inquiryObject = (Inquiry) inquiry;
        final Object orderInquiry = inquiryObject.getOrderInquiryOrKlarnaConfirmationInquiryOrBatchInquiryOrAccountBatchInquiryOrRefundableAmountInquiryOrShopperAuthenticationOrPriceInquiryOrBankAccountInquiryOrIdentifyMeInquiryOrPaymentOptionsInquiryOrPaymentTokenInquiryOrShopperTokenRetrieval().get(0);
        assertThat(orderInquiry).isInstanceOf(KlarnaConfirmationInquiry.class);
        final KlarnaConfirmationInquiry klarnaConfirmationInquiryObject = (KlarnaConfirmationInquiry) orderInquiry;
        assertThat(klarnaConfirmationInquiryObject.getOrderCode()).isEqualTo(WORLDPAY_ORDER_CODE);
    }


}
