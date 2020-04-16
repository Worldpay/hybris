
package com.worldpay.commands.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;

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

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayVoidCommand.class);
    private final Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverter;

    public DefaultWorldpayVoidCommand(final Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverter, final WorldpayMerchantInfoService worldpayMerchantInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final WorldpayServiceGateway worldpayServiceGateway) {
        super(worldpayMerchantInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway);
        this.voidServiceResponseConverter = voidServiceResponseConverter;
    }

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public VoidResult perform(final VoidRequest request) {
        try {
            final CancelServiceRequest cancelRequest = buildCancelServiceRequest(request.getRequestId());
            return cancel(cancelRequest);
        } catch (final WorldpayException e) {
            LOG.error(format("Exception raised while issuing a cancelServiceRequest: [{0}]", e.getMessage()), e);
            return createErrorVoidResult();
        }
    }

    /**
     * Make the call through to worldpay to cancel the authorisation
     *
     * @param cancelServiceRequest service request object
     * @return VoidResult translated from the service response
     */
    private VoidResult cancel(final CancelServiceRequest cancelServiceRequest) throws WorldpayException {
        final CancelServiceResponse cancelResponse = getWorldpayServiceGateway().cancel(cancelServiceRequest);
        if (cancelResponse == null) {
            throw new WorldpayException("Response from worldpay is empty");
        }
        final VoidResult voidResult = voidServiceResponseConverter.convert(cancelResponse);
        voidResult.setRequestToken(cancelServiceRequest.getMerchantInfo().getMerchantCode());
        return voidResult;
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
}
