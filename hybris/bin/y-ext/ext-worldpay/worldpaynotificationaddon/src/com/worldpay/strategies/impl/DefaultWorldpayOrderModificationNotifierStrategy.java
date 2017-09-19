package com.worldpay.strategies.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static com.worldpay.util.WorldpayUtil.createDateInPast;
import static java.text.MessageFormat.format;

/**
 * Default implementation of {@link WorldpayOrderModificationNotifierStrategy}.
 * <p>
 * For each unprocessed {@link WorldpayOrderModificationModel} a ticket is created for further investigation by Customer Service Agent.
 * </p>
 */
public class DefaultWorldpayOrderModificationNotifierStrategy implements WorldpayOrderModificationNotifierStrategy {

    public static final String WORLDPAYADDON_ERRORS_UNPROCESSED_ORDERS = "worldpayaddon.errors.unprocessed.orders";
    public static final String WORLDPAYADDON_ERRORS_THERE_ARE_UNPROCESSED_ORDERS = "worldpayaddon.errors.there.are.unprocessed.orders";

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderModificationNotifierStrategy.class);

    private TicketBusinessService ticketBusinessService;
    private ModelService modelService;
    private OrderModificationDao orderModificationDao;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private L10NService l10nService;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationNotifierStrategy#notifyThatOrdersHaveNotBeenProcessed(int)
     */
    @Override
    public void notifyThatOrdersHaveNotBeenProcessed(int days) {
        final List<WorldpayOrderModificationModel> unprocessedOrderModifications = getOrderModificationDao().findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(createDateInPast(days));
        if (!unprocessedOrderModifications.isEmpty()) {
            for (final WorldpayOrderModificationModel worldpayOrderModificationModel : unprocessedOrderModifications) {
                final String worldpayOrderCode = worldpayOrderModificationModel.getWorldpayOrderCode();
                final String unprocessedOrderMessage = format("{0} for worldpay order: {1}", getL10nService().getLocalizedString(WORLDPAYADDON_ERRORS_UNPROCESSED_ORDERS), worldpayOrderCode);
                getTicketBusinessService().createTicket(createParameters(unprocessedOrderMessage));
                worldpayOrderModificationModel.setNotified(true);
                modelService.save(worldpayOrderModificationModel);
                LOG.info(unprocessedOrderMessage);
            }
        } else {
            LOG.info("No Unprocessed order were found");
        }
    }

    private CsTicketParameter createParameters(final String creationNotes) {
        final CsTicketParameter parameters = new CsTicketParameter();
        parameters.setCreationNotes(creationNotes);
        parameters.setHeadline(getL10nService().getLocalizedString(WORLDPAYADDON_ERRORS_THERE_ARE_UNPROCESSED_ORDERS));
        parameters.setCategory(CsTicketCategory.PROBLEM);
        parameters.setPriority(CsTicketPriority.HIGH);
        return parameters;
    }

    public TicketBusinessService getTicketBusinessService() {
        return ticketBusinessService;
    }

    @Required
    public void setTicketBusinessService(TicketBusinessService ticketBusinessService) {
        this.ticketBusinessService = ticketBusinessService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public OrderModificationDao getOrderModificationDao() {
        return orderModificationDao;
    }

    @Required
    public void setOrderModificationDao(OrderModificationDao orderModificationDao) {
        this.orderModificationDao = orderModificationDao;
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    @Required
    public void setL10nService(L10NService l10nService) {
        this.l10nService = l10nService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    public WorldpayPaymentTransactionService getWorldpayPaymentTransactionService() {
        return worldpayPaymentTransactionService;
    }
}
