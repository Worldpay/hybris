package com.worldpay.service;

import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.AbstractServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

@IntegrationTest
public class OrderInquiryServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASS = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());

    private static final String WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES = "worldpayapi.inquiry.max.number.of.retries";
    private static final Integer DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE = 3;
    private static final String WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES = "worldpayapi.inquiry.delay.between.retries";
    private static final Integer DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE = 3;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Before
    public void setUp() throws Exception {
        configurationService.getConfiguration().setProperty(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE.toString());
        configurationService.getConfiguration().setProperty(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE.toString());


    }

    @Test
    @Ignore("Ignored because taking to much time to pass")
    public void testOrderInquiry() throws WorldpayException {

        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASS);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final OrderInquiryServiceRequest request = OrderInquiryServiceRequest.createOrderInquiryRequest(merchantInfo, ORDER_CODE);
        final Callable<OrderInquiryServiceResponse> callable = getOrderInquiryServiceResponseCallable(request);

        final RetryConfig config = buildRetryConfig();

        try {
            final Status<OrderInquiryServiceResponse> status = executeInquiryCallable(callable, config);
            final OrderInquiryServiceResponse orderInquiry = status.getResult();
            assertNotNull("Order inquiry response is null!", orderInquiry);
            assertFalse("Errors returned from order inquiry request", orderInquiry.isError());
            assertEquals("Order code returned is incorrect", ORDER_CODE, orderInquiry.getOrderCode());
            final PaymentReply paymentReply = orderInquiry.getPaymentReply();
            assertNotNull("Payment reply in the order inquiry is null!", paymentReply);
            final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
            assertNotNull("Auth status in the order inquiry is null!", authStatus);
        } catch (RetriesExhaustedException | UnexpectedException e) {
            throw new WorldpayException("Unable to retrieve order status", e);
        }
    }

    private RetryConfig buildRetryConfig() {
        return new RetryConfigBuilder()
            .retryOnSpecificExceptions(WorldpayException.class)
            .withMaxNumberOfTries(configurationService.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE))
            .withDelayBetweenTries(configurationService.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE), ChronoUnit.SECONDS)
            .withFixedBackoff()
            .build();
    }

    private Status<OrderInquiryServiceResponse> executeInquiryCallable(final Callable<OrderInquiryServiceResponse> callable, final RetryConfig config) {
        return new CallExecutorBuilder<OrderInquiryServiceResponse>().config(config).build().execute(callable);
    }

    private Callable<OrderInquiryServiceResponse> getOrderInquiryServiceResponseCallable(final AbstractServiceRequest orderInquiryServiceRequest) {
        return () -> {
            final OrderInquiryServiceResponse response = gateway.orderInquiry(orderInquiryServiceRequest);

            if (response.getErrorDetail() != null) {
                throw new WorldpayException(response.getErrorDetail().getMessage());
            }

            return response;
        };
    }
}
