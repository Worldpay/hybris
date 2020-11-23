package com.worldpay.commands.impl;

import com.worldpay.core.services.WorldpayHybrisOrderService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.COMMUNICATION_PROBLEM;


/**
 * Default Worldpay Capture command.
 * <p>
 * Communicates through the WorldpayServiceGateway to make the capture call to Worldpay
 * </p>
 */
public class DefaultWorldpayCaptureCommand extends WorldpayCommand implements CaptureCommand {
    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCaptureCommand.class);

    protected final Converter<CaptureServiceResponse, CaptureResult> captureServiceResponseConverter;
    protected final WorldpayHybrisOrderService worldpayHybrisOrderService;

    public DefaultWorldpayCaptureCommand(final Converter<CaptureServiceResponse, CaptureResult> captureServiceResponseConverter, final WorldpayHybrisOrderService worldpayHybrisOrderService, final WorldpayMerchantInfoService worldpayMerchantInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final WorldpayServiceGateway worldpayServiceGateway) {
        super(worldpayMerchantInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway);
        this.captureServiceResponseConverter = captureServiceResponseConverter;
        this.worldpayHybrisOrderService = worldpayHybrisOrderService;
    }

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public CaptureResult perform(final CaptureRequest request) {
        final String merchantCode = request.getRequestToken();
        final String orderCode = request.getRequestId();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();
        try {
            final CaptureServiceRequest captureRequest = buildCaptureServiceRequest(orderCode, amount, currency, getShippingInfosFromOrder(orderCode));
            return capture(captureRequest);
        } catch (final WorldpayException e) {
            LOG.error(MessageFormat.format("Error during capture of payment with ordercode [{0}] to merchant [{1}]", orderCode, merchantCode), e);
            return createErrorCaptureResult();
        }
    }

    protected List<String> getShippingInfosFromOrder(final String orderCode) {
        final OrderModel orderModel = worldpayHybrisOrderService.findOrderByWorldpayOrderCode(orderCode);
        return orderModel.getConsignments().stream()
                .map(ConsignmentModel::getTrackingID)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private CaptureResult createErrorCaptureResult() {
        final CaptureResult captureResult = new CaptureResult();
        captureResult.setTransactionStatus(ERROR);
        captureResult.setTransactionStatusDetails(COMMUNICATION_PROBLEM);
        return captureResult;
    }

    /**
     * Make the call through to Worldpay to capture the funds
     *
     * @param captureRequest service request object
     * @return CaptureResult translated from the service response
     */
    private CaptureResult capture(final CaptureServiceRequest captureRequest) throws WorldpayException {
        final CaptureServiceResponse captureResponse = getWorldpayServiceGateway().capture(captureRequest);
        if (captureResponse == null) {
            throw new WorldpayException("Response from worldpay is empty");
        }

        final CaptureResult captureResult = captureServiceResponseConverter.convert(captureResponse);
        captureResult.setRequestToken(captureRequest.getMerchantInfo().getMerchantCode());
        return captureResult;
    }

    /**
     * Build the capture service request
     *
     * @param worldpayOrderCode order code to be used in the service request
     * @param captureAmount     amount to be captured
     * @param currency          currency of the transaction
     * @return CaptureServiceRequest object
     */
    private CaptureServiceRequest buildCaptureServiceRequest(final String worldpayOrderCode, final BigDecimal captureAmount, final Currency currency, final List<String> trackingIds) throws WorldpayException {
        final Amount amount = getWorldpayOrderService().createAmount(currency, captureAmount.doubleValue());
        final Date date = new Date(LocalDateTime.now());

        final MerchantInfo merchantInfo = getMerchantInfo(worldpayOrderCode);
        return CaptureServiceRequest.createCaptureRequest(merchantInfo, worldpayOrderCode, amount, date, trackingIds);
    }

}
