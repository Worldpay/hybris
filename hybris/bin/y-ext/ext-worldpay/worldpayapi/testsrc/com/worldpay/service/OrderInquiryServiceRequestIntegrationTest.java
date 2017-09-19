package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class OrderInquiryServiceRequestIntegrationTest {

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final WorldpayServiceGateway GATEWAY = WorldpayServiceGateway.getInstance();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());

    @Test
    public void testOrderInquiry() throws WorldpayException {

        WPSGTestHelper.directAuthorise(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        final OrderInquiryServiceRequest request = OrderInquiryServiceRequest.createOrderInquiryRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);
        final OrderInquiryServiceResponse orderInquiry = GATEWAY.orderInquiry(request);

        assertNotNull("Order inquiry response is null!", orderInquiry);
        assertFalse("Errors returned from order inquiry request", orderInquiry.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, orderInquiry.getOrderCode());
        final PaymentReply paymentReply = orderInquiry.getPaymentReply();
        assertNotNull("Payment reply in the order inquiry is null!", paymentReply);
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        assertNotNull("Auth status in the order inquiry is null!", authStatus);
    }
}
