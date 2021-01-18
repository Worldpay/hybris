package com.worldpay.strategies.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.apache.log4j.Logger;

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

    private static final String WORLDPAYNOTIFICATIONS_ERRORS_UNPROCESSED_ORDERS = "worldpaynotifications.errors.unprocessed.orders";
    private static final String WORLDPAYNOTIFICATIONS_ERRORS_THERE_ARE_UNPROCESSED_ORDERS = "worldpaynotifications.errors.there.are.unprocessed.orders";

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderModificationNotifierStrategy.class);

    protected final TicketBusinessService ticketBusinessService;
    protected final ModelService modelService;
    protected final OrderModificationDao orderModificationDao;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final L10NService l10nService;

    public DefaultWorldpayOrderModificationNotifierStrategy(final TicketBusinessService ticketBusinessService,
                                                            final ModelService modelService,
                                                            final OrderModificationDao orderModificationDao,
                                                            final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                            final L10NService l10nService) {
        this.ticketBusinessService = ticketBusinessService;
        this.modelService = modelService;
        this.orderModificationDao = orderModificationDao;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.l10nService = l10nService;
    }

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
                final String unprocessedOrderMessage = format("{0} for worldpay order: {1}", getL10nService().getLocalizedString(WORLDPAYNOTIFICATIONS_ERRORS_UNPROCESSED_ORDERS), worldpayOrderCode);
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
        parameters.setHeadline(getL10nService().getLocalizedString(WORLDPAYNOTIFICATIONS_ERRORS_THERE_ARE_UNPROCESSED_ORDERS));
        parameters.setCategory(CsTicketCategory.PROBLEM);
        parameters.setPriority(CsTicketPriority.HIGH);
        return parameters;
    }

    public TicketBusinessService getTicketBusinessService() {
        return ticketBusinessService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public OrderModificationDao getOrderModificationDao() {
        return orderModificationDao;
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public WorldpayPaymentTransactionService getWorldpayPaymentTransactionService() {
        return worldpayPaymentTransactionService;
    }
}
