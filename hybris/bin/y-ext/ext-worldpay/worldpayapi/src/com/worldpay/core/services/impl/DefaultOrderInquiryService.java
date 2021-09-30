package com.worldpay.core.services.impl;

import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.AbstractServiceRequest;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

/**
 * Default implementation fo the {@link OrderInquiryService}.
 * <p>
 * Saves payment type depending on the method code retrieved from the {@link OrderInquiryServiceResponse}
 * and creates the {@link WorldpayAPMPaymentInfoModel} if it is an APM.
 * </p>
 */
public class DefaultOrderInquiryService implements OrderInquiryService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOrderInquiryService.class);

    private static final String WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES = "worldpayapi.inquiry.max.number.of.retries";
    private static final int DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE = 3;
    private static final String WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES = "worldpayapi.inquiry.delay.between.retries";
    private static final int DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE = 3;

    protected final WorldpayPaymentInfoService worldpayPaymentInfoService;
    protected final ConfigurationService configurationService;
    protected final WorldpayServiceGateway worldpayServiceGateway;

    public DefaultOrderInquiryService(final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                      final ConfigurationService configurationService,
                                      final WorldpayServiceGateway worldpayServiceGateway) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.configurationService = configurationService;
        this.worldpayServiceGateway = worldpayServiceGateway;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderInquiryServiceResponse inquirePaymentTransaction(final MerchantInfo merchantConfig, final PaymentTransactionModel paymentTransactionModel) throws WorldpayException {
        final String orderCode = paymentTransactionModel.getRequestId();
        final OrderInquiryServiceRequest orderInquiryServiceRequest = createOrderInquiryServiceRequest(merchantConfig, orderCode);
        return worldpayServiceGateway.orderInquiry(orderInquiryServiceRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processOrderInquiryServiceResponse(final PaymentTransactionModel paymentTransactionModel, final OrderInquiryServiceResponse orderInquiryServiceResponse) throws WorldpayConfigurationException {
        if (!orderInquiryServiceResponse.isError()) {
            final String methodCode = orderInquiryServiceResponse.getPaymentReply().getPaymentMethodCode();
            worldpayPaymentInfoService.savePaymentType(paymentTransactionModel, methodCode);
            if (Boolean.TRUE.equals(paymentTransactionModel.getInfo().getIsApm())) {
                worldpayPaymentInfoService.createWorldpayApmPaymentInfo(paymentTransactionModel);
                LOG.info("Converting PaymentInfo to WorldpayAPMPaymentInfo and setting timeout-date for PaymentTransaction with code [{}] on order with code [{}]",
                    paymentTransactionModel.getCode(), paymentTransactionModel.getOrder().getCode());
            }
        } else {
            LOG.error("Order inquiry service returned error [{}]", orderInquiryServiceResponse.getErrorDetail().getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderInquiryServiceResponse inquireOrder(final MerchantInfo merchantInfo, final String worldpayOrderCode) throws WorldpayException {
        final OrderInquiryServiceRequest orderInquiryServiceRequest = createOrderInquiryServiceRequest(merchantInfo, worldpayOrderCode);
        return getOrderInquiryServiceResponse(orderInquiryServiceRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderInquiryServiceResponse inquiryKlarnaOrder(final MerchantInfo merchantInfo, final String worldpayOrderCode) throws WorldpayException {
        final KlarnaOrderInquiryServiceRequest klarnaOrderInquiryServiceRequest = createKlarnaOrderInquiryServiceRequest(merchantInfo, worldpayOrderCode);
        return getOrderInquiryServiceResponse(klarnaOrderInquiryServiceRequest);
    }

    private OrderInquiryServiceResponse getOrderInquiryServiceResponse(final AbstractServiceRequest orderInquiryServiceRequest) throws WorldpayException {
        final Callable<OrderInquiryServiceResponse> callable = getOrderInquiryServiceResponseCallable(orderInquiryServiceRequest);
        final RetryConfig config = buildRetryConfig();

        try {
            final Status<OrderInquiryServiceResponse> orderInquiryServiceResponseCallResults = executeInquiryCallable(callable, config);
            return orderInquiryServiceResponseCallResults.getResult();
        } catch (final RetriesExhaustedException | UnexpectedException e) {
            throw new WorldpayException("Unable to retrieve order status", e);
        }
    }

    private Callable<OrderInquiryServiceResponse> getOrderInquiryServiceResponseCallable(final AbstractServiceRequest orderInquiryServiceRequest) {
        return () -> {
            final OrderInquiryServiceResponse response = worldpayServiceGateway.orderInquiry(orderInquiryServiceRequest);

            if (response.getErrorDetail() != null) {
                throw new WorldpayException(response.getErrorDetail().getMessage());
            }

            return response;
        };
    }

    private RetryConfig buildRetryConfig() {
        return new RetryConfigBuilder()
            .retryOnSpecificExceptions(WorldpayException.class)
            .withMaxNumberOfTries(configurationService.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES_VALUE))
            .withDelayBetweenTries(configurationService.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, DEFAULT_WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES_VALUE), ChronoUnit.SECONDS)
            .withFixedBackoff()
            .build();
    }

    protected Status<OrderInquiryServiceResponse> executeInquiryCallable(final Callable<OrderInquiryServiceResponse> callable, final RetryConfig config) {
        return new CallExecutorBuilder<OrderInquiryServiceResponse>().config(config).build().execute(callable);
    }

    protected OrderInquiryServiceRequest createOrderInquiryServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        return OrderInquiryServiceRequest.createOrderInquiryRequest(merchantInfo, orderCode);
    }

    protected KlarnaOrderInquiryServiceRequest createKlarnaOrderInquiryServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        return KlarnaOrderInquiryServiceRequest.createKlarnaOrderInquiryRequest(merchantInfo, orderCode);
    }

}
