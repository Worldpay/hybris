package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Inquiry;
import com.worldpay.internal.model.KlarnaConfirmationInquiry;
import com.worldpay.internal.model.OrderInquiry;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.request.ServiceRequest;

/**
 * Specific class for transforming an {@link OrderInquiryServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;inquiry&gt;
 *          &lt;orderInquiry orderCode="5678"/&gt;
 *      &lt;/inquiry&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class OrderInquiryRequestTransformer implements ServiceRequestTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.request.transform.ServiceRequestTransformer#transform(com.worldpay.service.request.ServiceRequest)
     */
    @Override
    public PaymentService transform(ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null || request.getMerchantInfo() == null || request.getWorldpayConfig() == null || request.getOrderCode() == null) {
            throw new WorldpayModelTransformationException("Request provided to do the order inquiry is invalid.");
        }
        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(request.getWorldpayConfig().getVersion());


        final Inquiry inquiry = new Inquiry();
        buildInquiry(request, inquiry);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(inquiry);
        return paymentService;
    }

    private void buildInquiry(final ServiceRequest request, final Inquiry inquiry) {
        if (request instanceof OrderInquiryServiceRequest) {
            final OrderInquiry orderInquiry = new OrderInquiry();
            orderInquiry.setOrderCode(request.getOrderCode());
            inquiry.getOrderInquiryOrKlarnaConfirmationInquiryOrBatchInquiryOrAccountBatchInquiryOrRefundableAmountInquiryOrShopperAuthenticationOrPriceInquiryOrBankAccountInquiryOrIdentifyMeInquiryOrPaymentOptionsInquiryOrPaymentTokenInquiryOrShopperTokenRetrieval().add(orderInquiry);
        } else if (request instanceof KlarnaOrderInquiryServiceRequest) {
            final KlarnaConfirmationInquiry klarnaConfirmationInquiry = new KlarnaConfirmationInquiry();
            klarnaConfirmationInquiry.setOrderCode(request.getOrderCode());
            inquiry.getOrderInquiryOrKlarnaConfirmationInquiryOrBatchInquiryOrAccountBatchInquiryOrRefundableAmountInquiryOrShopperAuthenticationOrPriceInquiryOrBankAccountInquiryOrIdentifyMeInquiryOrPaymentOptionsInquiryOrPaymentTokenInquiryOrShopperTokenRetrieval().add(klarnaConfirmationInquiry);
        }
    }
}
