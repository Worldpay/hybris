package com.worldpay.service.payment.request.impl;

import com.worldpay.data.ErrorDetail;
import com.worldpay.data.Order;
import com.worldpay.data.PaymentDetails;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.request.WorldpayRequestRetryStrategy;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.order.CartService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DefaultWorldpayRequestRetryEFTPOSStrategy implements WorldpayRequestRetryStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayRequestRetryEFTPOSStrategy.class);

    private static final String PAYMENT_DETAILS_ERROR_CODE = "7";

    protected final WorldpayOrderService worldpayOrderService;
    protected final CartService cartService;

    public DefaultWorldpayRequestRetryEFTPOSStrategy(final WorldpayOrderService worldpayOrderService,
                                                     final CartService cartService) {
        this.worldpayOrderService = worldpayOrderService;
        this.cartService = cartService;
    }

    @Override
    public boolean isRequestToBeRetried(final DirectAuthoriseServiceRequest request,
                                        final DirectAuthoriseServiceResponse response) {
        return response.isError()
            && PAYMENT_DETAILS_ERROR_CODE.equals(getErrorCodeFromResponse(response))
            && isRequestWithRoutingMIDValueNonNull(request);
    }

    private String getErrorCodeFromResponse(final DirectAuthoriseServiceResponse response) {
        return Optional.ofNullable(response.getErrorDetail())
            .map(ErrorDetail::getCode)
            .orElse(StringUtils.EMPTY);
    }

    private boolean isRequestWithRoutingMIDValueNonNull(final DirectAuthoriseServiceRequest request) {
        return Optional.ofNullable(request.getOrder())
            .map(Order::getPaymentDetails)
            .map(PaymentDetails::getRoutingMID)
            .map(StringUtils::isNotBlank)
            .orElse(false);
    }

    @Override
    public DirectAuthoriseServiceRequest getDirectAuthoriseServiceRequestToRetry(final DirectAuthoriseServiceRequest request,
                                                                                 final DirectAuthoriseServiceResponse response) {

        final String errorCodeFromResponse = getErrorCodeFromResponse(response);
        final String errorMessageFromResponse = getErrorMessageFromResponse(response);

        LOG.warn("Request with Routing MID has returned an error response with code {}, message: {}",
            errorCodeFromResponse,
            errorMessageFromResponse);
        LOG.warn("Retrying request without Routing MID and a new order code");

        request.setOrderCode(worldpayOrderService.generateWorldpayOrderCode(cartService.getSessionCart()));
        request.getOrder().setOrderCode(request.getOrderCode());
        request.getOrder().setDescription(request.getOrderCode());
        request.getOrder().getPaymentDetails().setRoutingMID(null);

        return request;
    }

    private String getErrorMessageFromResponse(final DirectAuthoriseServiceResponse response) {
        return Optional.ofNullable(response.getErrorDetail())
            .map(ErrorDetail::getMessage)
            .orElse(StringUtils.EMPTY);
    }
}
