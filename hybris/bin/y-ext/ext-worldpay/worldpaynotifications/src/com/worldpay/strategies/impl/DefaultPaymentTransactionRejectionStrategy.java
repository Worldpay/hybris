package com.worldpay.strategies.impl;

import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.strategies.PaymentTransactionRejectionStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;


import java.util.List;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

/**
 * {@inheritDoc}
 */
public class DefaultPaymentTransactionRejectionStrategy implements PaymentTransactionRejectionStrategy {

    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final ModelService modelService;
    protected final ProcessDefinitionDao processDefinitionDao;
    protected final BusinessProcessService businessProcessService;

    public DefaultPaymentTransactionRejectionStrategy(final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                      final ModelService modelService,
                                                      final ProcessDefinitionDao processDefinitionDao,
                                                      final BusinessProcessService businessProcessService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.modelService = modelService;
        this.processDefinitionDao = processDefinitionDao;
        this.businessProcessService = businessProcessService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeRejection(final PaymentTransactionModel paymentTransactionModel) {
        rejectTransactionEntries(paymentTransactionModel);
        wakeUpBusinessProcess(paymentTransactionModel.getOrder());
    }

    protected void rejectTransactionEntries(final PaymentTransactionModel paymentTransactionModel) {
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionModel.getEntries(), TransactionStatus.REJECTED.name());
        modelService.save(paymentTransactionModel);
    }

    protected void wakeUpBusinessProcess(final AbstractOrderModel orderModel) {
        final List<BusinessProcessModel> businessProcessModels = processDefinitionDao.findWaitingOrderProcesses(orderModel.getCode(), AUTHORIZATION);
        if (businessProcessModels.size() == 1) {
            triggerOrderProcessEvent(businessProcessModels);
        }
    }

    private void triggerOrderProcessEvent(final List<BusinessProcessModel> businessProcessModels) {
        final String eventName = getEventName(AUTHORIZATION, businessProcessModels.get(0));
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        return businessProcessModel.getCode() + "_" + paymentTransactionType;
    }

}
