package com.worldpay.service;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.worldpay.data.MerchantInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.AbstractServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerJUnit5BaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@IntegrationTest
class OrderInquiryServiceRequestIntegrationTest extends ServicelayerJUnit5BaseTest {

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

    @BeforeEach
    void setUp() {
        configurationService.getConfiguration().setProperty(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE.toString());
        configurationService.getConfiguration().setProperty(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE.toString());
    }

    @Test
    @Disabled("Ignored because it is taking too much time to pass")
    void testOrderInquiry() throws WorldpayException {

        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASS);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        final DirectAuthoriseServiceResponse directAuthoriseResponse =
                WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        assertNotNull(directAuthoriseResponse, "Direct authorise response is null!");
        assertFalse(
                directAuthoriseResponse.isError(),
                () -> "Errors returned from direct authorise request: " +
                        (directAuthoriseResponse.getErrorDetail() != null
                                ? directAuthoriseResponse.getErrorDetail().getMessage()
                                : "No error detail")
        );
        assertEquals(ORDER_CODE, directAuthoriseResponse.getOrderCode(), "Order code returned from direct authorise is incorrect");
        assertNotNull(directAuthoriseResponse.getRedirectReference(), "Direct authorise redirect reference is null");

        final OrderInquiryServiceRequest request = OrderInquiryServiceRequest.createOrderInquiryRequest(merchantInfo, ORDER_CODE);
        final Callable<OrderInquiryServiceResponse> callable = getOrderInquiryServiceResponseCallable(request);

        final RetryConfig config = buildRetryConfig();

        try {
            final OrderInquiryServiceResponse orderInquiry = executeInquiryCallable(callable, config);

            assertEquals(ORDER_CODE, orderInquiry.getOrderCode(), "Order code returned is incorrect");

            final PaymentReply paymentReply = orderInquiry.getPaymentReply();
            assertNotNull(paymentReply, "Payment reply in the order inquiry is null!");

            final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
            assertNotNull(authStatus, "Auth status in the order inquiry is null!");
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WorldpayException("Unable to retrieve order status", e);
        } catch (final Exception e) {
            throw new WorldpayException("Unable to retrieve order status", e);
        }
    }

    private RetryConfig buildRetryConfig() {
        return RetryConfig.custom()
                .retryExceptions(WorldpayException.class)
                .maxAttempts(configurationService.getConfiguration().getInt(
                        WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES,
                        DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE))
                .waitDuration(Duration.ofSeconds(configurationService.getConfiguration().getInt(
                        WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES,
                        DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE)))
                .build();
    }

    private OrderInquiryServiceResponse executeInquiryCallable(final Callable<OrderInquiryServiceResponse> callable, final RetryConfig config) throws Exception {
        final Retry retry = Retry.of("worldpayOrderInquiryIntegrationTest", config);
        return Retry.decorateCallable(retry, callable).call();
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
