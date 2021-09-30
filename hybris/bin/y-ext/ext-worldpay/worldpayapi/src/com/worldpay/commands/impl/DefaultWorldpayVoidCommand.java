package com.worldpay.commands.impl;

import com.worldpay.core.services.WorldpayPrimeRoutingService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.request.VoidSaleServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import com.worldpay.service.response.VoidSaleServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.COMMUNICATION_PROBLEM;
import static java.text.MessageFormat.format;

/**
 * Default worldpay Cancel command.
 * <p>
 * Communicates through the worldpayServiceGateway to make the cancel call to worldpay
 * </p>
 */
public class DefaultWorldpayVoidCommand extends WorldpayCommand implements VoidCommand {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayVoidCommand.class);

    protected final Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverter;
    protected final WorldpayPrimeRoutingService worldpayPrimeRoutingService;

    public DefaultWorldpayVoidCommand(final Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverter,
                                      final WorldpayMerchantInfoService worldpayMerchantInfoService,
                                      final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                      final WorldpayOrderService worldpayOrderService,
                                      final WorldpayServiceGateway worldpayServiceGateway,
                                      final WorldpayPrimeRoutingService worldpayPrimeRoutingService) {
        super(worldpayMerchantInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway);
        this.voidServiceResponseConverter = voidServiceResponseConverter;
        this.worldpayPrimeRoutingService = worldpayPrimeRoutingService;
    }

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public VoidResult perform(final VoidRequest request) {
        final String worldpayOrderCode = request.getRequestId();

        if (worldpayPrimeRoutingService.isOrderAuthorisedWithPrimeRouting(worldpayOrderCode)) {
            return voidSale(worldpayOrderCode);
        }
        return cancel(worldpayOrderCode);
    }

    /**
     * Make the call through to worldpay to void the authorisation
     *
     * @param worldpayOrderCode void worldpayOrderCode
     * @return VoidResult translated from the service response
     */
    private VoidResult voidSale(final String worldpayOrderCode) {
        try {
            final VoidSaleServiceRequest voidServiceRequest = buildVoidServiceRequest(worldpayOrderCode);

            final VoidSaleServiceResponse voidSaleServiceResponse = getWorldpayServiceGateway().voidSale(voidServiceRequest);
            if (voidSaleServiceResponse == null) {
                throw new WorldpayException("Response from Worldpay is empty");
            }
            final VoidResult voidResult = voidServiceResponseConverter.convert(voidSaleServiceResponse);
            voidResult.setRequestToken(voidServiceRequest.getMerchantInfo().getMerchantCode());
            return voidResult;
        } catch (final WorldpayException e) {
            LOG.error(format("Exception raised while issuing a cancelServiceRequest: [{0}]", e.getMessage()), e);
            return createErrorVoidResult();
        }
    }

    /**
     * Make the call through to worldpay to cancel the authorisation
     *
     * @param worldpayOrderCode worldpayOrderCode
     * @return VoidResult translated from the service response
     */
    private VoidResult cancel(final String worldpayOrderCode) {
        try {
            final CancelServiceRequest cancelServiceRequest = buildCancelServiceRequest(worldpayOrderCode);
            final CancelServiceResponse cancelResponse = getWorldpayServiceGateway().cancel(cancelServiceRequest);
            if (cancelResponse == null) {
                throw new WorldpayException("Response from Worldpay is empty");
            }
            final VoidResult voidResult = voidServiceResponseConverter.convert(cancelResponse);
            voidResult.setRequestToken(cancelServiceRequest.getMerchantInfo().getMerchantCode());
            return voidResult;
        } catch (final WorldpayException e) {
            LOG.error(format("Exception raised while issuing a cancelServiceRequest: [{0}]", e.getMessage()), e);
            return createErrorVoidResult();
        }
    }

    protected VoidResult createErrorVoidResult() {
        final VoidResult result = new VoidResult();
        result.setTransactionStatus(ERROR);
        result.setTransactionStatusDetails(COMMUNICATION_PROBLEM);
        return result;
    }

    /**
     * Build the cancel service request
     *
     * @param worldpayOrderCode order code to be used in the service request
     * @return CancelServiceRequest object
     */
    private CancelServiceRequest buildCancelServiceRequest(final String worldpayOrderCode) throws WorldpayException {
        final MerchantInfo merchantInfo = getMerchantInfo(worldpayOrderCode);
        return CancelServiceRequest.createCancelRequest(merchantInfo, worldpayOrderCode);
    }

    /**
     * Build the void service request
     *
     * @param worldpayOrderCode order code to be used in the service request
     * @return CancelServiceRequest object
     */
    private VoidSaleServiceRequest buildVoidServiceRequest(final String worldpayOrderCode) throws WorldpayException {
        final MerchantInfo merchantInfo = getMerchantInfo(worldpayOrderCode);
        return VoidSaleServiceRequest.createVoidSaleRequest(merchantInfo, worldpayOrderCode);
    }
}
