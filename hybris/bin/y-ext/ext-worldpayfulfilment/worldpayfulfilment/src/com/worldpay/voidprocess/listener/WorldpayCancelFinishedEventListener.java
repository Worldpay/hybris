package com.worldpay.voidprocess.listener;

import com.worldpay.dao.ProcessDefinitionDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.MessageFormat;
import java.util.List;

/**
 * Event listener to handle a CancelFinished event
 */
public class WorldpayCancelFinishedEventListener extends AbstractEventListener<CancelFinishedEvent> {

    protected static final String WORLDPAY_VOID_PROCESS_NAME = "worldpay-void-process";

    protected final BusinessProcessService businessProcessService;
    protected final ModelService modelService;
    protected final ProcessDefinitionDao processDefinitionDao;

    public WorldpayCancelFinishedEventListener(final BusinessProcessService businessProcessService,
                                               final ModelService modelService,
                                               final ProcessDefinitionDao processDefinitionDao) {
        this.businessProcessService = businessProcessService;
        this.modelService = modelService;
        this.processDefinitionDao = processDefinitionDao;
    }

    @Override
    protected void onEvent(final CancelFinishedEvent cancelFinishedEvent) {
        final OrderModel order = cancelFinishedEvent.getCancelRequestRecordEntry().getModificationRecord().getOrder();
        final String businessProcessId = MessageFormat.format("{0}-{1}-{2}", WORLDPAY_VOID_PROCESS_NAME, order.getCode(), getCurrentTimeInMillis());
        final OrderProcessModel voidOrderProcess = businessProcessService.createProcess(businessProcessId, WORLDPAY_VOID_PROCESS_NAME);
        voidOrderProcess.setOrder(order);
        modelService.save(voidOrderProcess);
        businessProcessService.startProcess(voidOrderProcess);
        triggerOrderProcessEvent(order);
    }

    private void triggerOrderProcessEvent(final OrderModel orderModel) {
        final List<BusinessProcessModel> businessProcessModels = processDefinitionDao.findWaitingOrderProcesses(orderModel.getCode(), PaymentTransactionType.CANCEL);
        if (businessProcessModels.size() == 1) {
            businessProcessService.triggerEvent(businessProcessModels.get(0).getCode() + "_" + PaymentTransactionType.CANCEL);
        }
    }

    protected long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }
}
