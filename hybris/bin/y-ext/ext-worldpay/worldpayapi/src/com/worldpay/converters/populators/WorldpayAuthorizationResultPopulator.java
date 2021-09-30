package com.worldpay.converters.populators;

import com.worldpay.service.WorldpayAuthorisationResultService;
import com.worldpay.data.ErrorDetail;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Populator that fills the necessary details on a {@link AuthorizationResult} with the information of a {@link DirectAuthoriseServiceResponse}
 */
public class WorldpayAuthorizationResultPopulator implements Populator<DirectAuthoriseServiceResponse, AuthorizationResult> {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayAuthorizationResultPopulator.class);

    protected final WorldpayAuthorisationResultService worldpayAuthorisationResultService;

    public WorldpayAuthorizationResultPopulator(final WorldpayAuthorisationResultService worldpayAuthorisationResultService) {
        this.worldpayAuthorisationResultService = worldpayAuthorisationResultService;
    }

    /**
     * Populates the data from the {@link DirectAuthoriseServiceResponse} to a {@link AuthorizationResult}
     * @param source a {@link DirectAuthoriseServiceResponse} from Worldpay
     * @param target a {@link AuthorizationResult} in hybris.
     * @throws ConversionException
     */
    @Override
    public void populate(final DirectAuthoriseServiceResponse source, final AuthorizationResult target) throws ConversionException {
        final PaymentReply paymentReply = source.getPaymentReply();
        if (paymentReply == null) {
            LOG.warn("No PaymentReply in response from worldpay");
            final ErrorDetail errorDetail = source.getErrorDetail();
            if (errorDetail != null) {
                LOG.error("Error message from Worldpay: {}", errorDetail.getMessage());
            }
            worldpayAuthorisationResultService.setAuthoriseResultAsError(target);
        } else {
            worldpayAuthorisationResultService.setAuthoriseResultByTransactionStatus(target, paymentReply.getAuthStatus(), source.getOrderCode());
        }
    }
}
