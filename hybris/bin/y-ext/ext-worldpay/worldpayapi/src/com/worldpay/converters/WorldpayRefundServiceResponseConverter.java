package com.worldpay.converters;

import com.worldpay.data.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.util.Currency;

/**
 * {@see WorldpayAbstractServiceResponseConverter}
 */
public class WorldpayRefundServiceResponseConverter extends WorldpayAbstractServiceResponseConverter<RefundServiceResponse, RefundResult> {

    public WorldpayRefundServiceResponseConverter(final WorldpayOrderService worldpayOrderService) {
        super(worldpayOrderService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final RefundServiceResponse refundServiceResponse, final RefundResult refundResult) {
        final Amount amount = refundServiceResponse.getAmount();
        final String currencyCode = amount.getCurrencyCode();
        final Currency currency = Currency.getInstance(currencyCode);
        refundResult.setCurrency(currency);
        refundResult.setTotalAmount(getTotalAmount(amount));
        refundResult.setRequestId(refundServiceResponse.getOrderCode());
        refundResult.setRequestTime(new java.util.Date());
        if (refundServiceResponse.isError()) {
            refundResult.setTransactionStatus(TransactionStatus.ERROR);
            refundResult.setTransactionStatusDetails(getTransactionStatusDetails(refundServiceResponse.getErrorDetail()));
        } else {
            refundResult.setTransactionStatus(TransactionStatus.ACCEPTED);
            refundResult.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
        }
    }
}
