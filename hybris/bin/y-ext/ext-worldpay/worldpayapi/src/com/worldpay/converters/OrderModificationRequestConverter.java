package com.worldpay.converters;

import com.worldpay.data.JournalReply;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.WebformRefundReply;
import com.worldpay.data.token.TokenReply;
import com.worldpay.internal.model.*;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.response.transform.ServiceResponseTransformerHelper;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Converter used to transform an {@link OrderNotificationMessage} into a {@link PaymentService}
 */
public class OrderModificationRequestConverter implements Converter<PaymentService, OrderNotificationMessage> {

    protected final ServiceResponseTransformerHelper serviceResponseTransformerHelper;

    public OrderModificationRequestConverter(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        this.serviceResponseTransformerHelper = serviceResponseTransformerHelper;
    }

    /**
     * Converts a {@link PaymentService} to a {@link OrderNotificationMessage} setting in the target the necessary values.
     *
     * @param source a {@link PaymentService}
     * @return an OrderNotificationMessage with the necessary values from the PaymentService.
     */
    @Override
    public OrderNotificationMessage convert(final PaymentService source) throws ConversionException {
        return convert(source, new OrderNotificationMessage());
    }

    /**
     * Converts a {@link PaymentService} to a {@link OrderNotificationMessage} setting in the target the necessary values.
     *
     * @param source a {@link PaymentService}
     * @param target a {@link OrderNotificationMessage}
     * @return an OrderNotificationMessage with the necessary values from the PaymentService.
     */
    @Override
    public OrderNotificationMessage convert(final PaymentService source, final OrderNotificationMessage target) {
        final OrderStatusEvent orderStatusEvent = getOrderStatusEvent(source);

        target.setOrderCode(orderStatusEvent.getOrderCode());
        target.setMerchantCode(source.getMerchantCode());

        final Payment intPayment = orderStatusEvent.getPayment();
        final PaymentReply paymentReply = serviceResponseTransformerHelper.buildPaymentReply(intPayment);
        target.setPaymentReply(paymentReply);

        final Journal intJournal = orderStatusEvent.getJournal();
        final JournalReply journalReply = serviceResponseTransformerHelper.buildJournalReply(intJournal);
        target.setJournalReply(journalReply);

        final Token intToken = orderStatusEvent.getToken();
        if (intToken != null) {
            final TokenReply tokenReply = serviceResponseTransformerHelper.buildTokenReply(intToken);
            target.setTokenReply(tokenReply);
        }

        final ShopperWebformRefundDetails intShopperWebformRefundDetails = orderStatusEvent.getShopperWebformRefundDetails();
        if (intShopperWebformRefundDetails != null) {
            final WebformRefundReply webformRefundReply = serviceResponseTransformerHelper.buildWebformRefundReply(intShopperWebformRefundDetails);
            target.setWebformRefundReply(webformRefundReply);
        }

        final ExemptionResponse intExemptionResponse = orderStatusEvent.getExemptionResponse();
        if (intExemptionResponse != null && paymentReply != null) {
            paymentReply.setExemptionResponseInfo(serviceResponseTransformerHelper.buildExemptionResponse(intExemptionResponse));
        }

        return target;
    }

    protected OrderStatusEvent getOrderStatusEvent(final PaymentService source) {
        return (OrderStatusEvent) ((Notify) source
                .getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0))
                .getOrderStatusEventOrEncryptedDataOrReport().get(0);
    }
}
