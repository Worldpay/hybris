package com.worldpay.converters;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.internal.model.*;
import com.worldpay.data.JournalReply;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.WebformRefundReply;
import com.worldpay.data.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.response.transform.ServiceResponseTransformerHelper;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderModificationRequestConverterTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String MERCHANT_CODE = "merchantCode";

    @InjectMocks
    private OrderModificationRequestConverter testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private PaymentService paymentServiceMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Notify notifyMock;
    @Mock
    private OrderStatusEvent orderStatusEventMock;
    @Mock
    private Payment paymentMock;
    @Mock
    private Journal journalMock;
    @Mock
    private ServiceResponseTransformerHelper responseTransformerHelperMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private JournalReply journalReplyMock;
    @Mock
    private Token tokenMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private ShopperWebformRefundDetails shopperWebformRefundDetailsMock;
    @Mock
    private WebformRefundReply webformRefundReplyMock;
    @Mock
    private ExemptionResponse exemptionResponseMock;
    @Mock
    private ExemptionResponseInfo exemptionResponseInfoMock;

    @Before
    public void setUp() {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0)).thenReturn(notifyMock);
        when(notifyMock.getOrderStatusEventOrEncryptedDataOrReport().get(0)).thenReturn(orderStatusEventMock);
    }

    @Test
    public void convertShouldConvertXMLMessageIntoOrderModificationMessage() {
        when(responseTransformerHelperMock.buildPaymentReply(paymentMock)).thenReturn(paymentReplyMock);
        when(responseTransformerHelperMock.buildJournalReply(journalMock)).thenReturn(journalReplyMock);
        when(orderStatusEventMock.getPayment()).thenReturn(paymentMock);
        when(orderStatusEventMock.getJournal()).thenReturn(journalMock);
        when(orderStatusEventMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(orderStatusEventMock.getExemptionResponse()).thenReturn(exemptionResponseMock);
        when(responseTransformerHelperMock.buildExemptionResponse(exemptionResponseMock)).thenReturn(exemptionResponseInfoMock);
        when(paymentServiceMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(orderStatusEventMock.getToken()).thenReturn(tokenMock);
        when(responseTransformerHelperMock.buildTokenReply(tokenMock)).thenReturn(tokenReplyMock);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertSame(paymentReplyMock, result.getPaymentReply());
        assertSame(journalReplyMock, result.getJournalReply());
        assertSame(tokenReplyMock, result.getTokenReply());
        verify(paymentReplyMock).setExemptionResponseInfo(exemptionResponseInfoMock);
    }

    @Test
    public void convertShouldConvertXMLMessageIntoOrderModificationMessageWithoutToken() {
        when(responseTransformerHelperMock.buildPaymentReply(paymentMock)).thenReturn(paymentReplyMock);
        when(responseTransformerHelperMock.buildJournalReply(journalMock)).thenReturn(journalReplyMock);
        when(orderStatusEventMock.getPayment()).thenReturn(paymentMock);
        when(orderStatusEventMock.getJournal()).thenReturn(journalMock);
        when(orderStatusEventMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(paymentServiceMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(orderStatusEventMock.getToken()).thenReturn(null);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertSame(paymentReplyMock, result.getPaymentReply());
        assertSame(journalReplyMock, result.getJournalReply());
        assertNull(result.getTokenReply());
    }

    @Test
    public void shouldSetShopperWebformReply() {
        when(orderStatusEventMock.getShopperWebformRefundDetails()).thenReturn(shopperWebformRefundDetailsMock);
        when(responseTransformerHelperMock.buildWebformRefundReply(shopperWebformRefundDetailsMock)).thenReturn(webformRefundReplyMock);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertEquals(webformRefundReplyMock, result.getWebformRefundReply());
    }

    @Test
    public void shouldNotSetShopperWebformReplyWhenNull() {
        when(orderStatusEventMock.getShopperWebformRefundDetails()).thenReturn(null);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertNull(result.getWebformRefundReply());
        verify(responseTransformerHelperMock, never()).buildWebformRefundReply(any());
    }
}
