package com.worldpay.service.payment.impl;

import com.worldpay.data.Exemption;
import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

public class DefaultWorldpayExemptionStrategy extends AbstractWorldpayExemptionStrategy implements WorldpayAdditionalDataRequestStrategy {

    protected ModelService modelService;

    protected DefaultWorldpayExemptionStrategy(final BaseSiteService baseSiteService,
                                               final ModelService modelService) {
        super(baseSiteService);
        this.modelService = modelService;
    }

    @Override
    public boolean isExemptionEnabled() {
        return Optional.ofNullable(baseSiteService.getCurrentBaseSite())
            .map(BaseSiteModel::getEnableEE)
            .orElse(false);
    }

    @Override
    public boolean isExemptionEnabled(final BaseSiteModel baseSite) {
        return Optional.ofNullable(baseSite)
            .map(BaseSiteModel::getEnableEE)
            .orElse(false);
    }

    @Override
    public void addExemptionResponse(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        Optional.ofNullable(paymentTransactionModel).ifPresent(paymentTransaction -> Optional.ofNullable(paymentReply)
            .map(PaymentReply::getExemptionResponseInfo)
            .ifPresent(worldpayExemptionResponseModel -> saveExemptionResponseToPaymentTransaction(worldpayExemptionResponseModel, paymentTransaction)));
    }

    private void saveExemptionResponseToPaymentTransaction(final ExemptionResponseInfo exemptionResponseInfo,
                                                           final PaymentTransactionModel paymentTransactionModel) {
        paymentTransactionModel.setExemptionResponseResult(exemptionResponseInfo.getResult());
        paymentTransactionModel.setExemptionResponseReason(exemptionResponseInfo.getReason());
        Optional.ofNullable(exemptionResponseInfo.getExemption())
            .ifPresent(exemption -> populatePaymentTransactionWithExemptionAttributes(exemption, paymentTransactionModel));
        modelService.save(paymentTransactionModel);
    }

    private void populatePaymentTransactionWithExemptionAttributes(final Exemption exemption,
                                                                   final PaymentTransactionModel paymentTransactionModel) {
        paymentTransactionModel.setExemptionType(exemption.getType());
        paymentTransactionModel.setExemptionPlacement(exemption.getPlacement());
    }

    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        if (isExemptionEnabled() && isValidPaymentMethod(cart)) {
            authoriseRequestParametersCreator.withExemption(createExemption());
        }
    }

    protected Exemption createExemption() {
        final Exemption exemption = new Exemption();
        exemption.setPlacement(EXEMPTION_PLACEMENT_DEFAULT_VALUE);
        exemption.setType(EXEMPTION_TYPE_DEFAULT_VALUE);

        return exemption;
    }

    private boolean isValidPaymentMethod(final AbstractOrderModel cart) {
        final PaymentInfoModel paymentInfo = cart.getPaymentInfo();

        return paymentInfo != null && VALID_PAYMENT_TYPES.contains(paymentInfo.getPaymentType());
    }

}
