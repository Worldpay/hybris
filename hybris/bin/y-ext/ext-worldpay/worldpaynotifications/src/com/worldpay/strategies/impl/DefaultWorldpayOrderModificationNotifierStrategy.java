package com.worldpay.strategies.impl;

import com.worldpay.core.services.WorldpayHybrisOrderService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayOrderModificationNotifierStrategy.class);

    protected final TicketBusinessService ticketBusinessService;
    protected final ModelService modelService;
    protected final OrderModificationDao orderModificationDao;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final L10NService l10nService;
    protected final WorldpayHybrisOrderService worldpayHybrisOrderService;

    public DefaultWorldpayOrderModificationNotifierStrategy(final TicketBusinessService ticketBusinessService,
                                                            final ModelService modelService,
                                                            final OrderModificationDao orderModificationDao,
                                                            final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                            final L10NService l10nService,
                                                            final WorldpayHybrisOrderService worldpayHybrisOrderService) {
        this.ticketBusinessService = ticketBusinessService;
        this.modelService = modelService;
        this.orderModificationDao = orderModificationDao;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.l10nService = l10nService;
        this.worldpayHybrisOrderService = worldpayHybrisOrderService;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationNotifierStrategy#notifyThatOrdersHaveNotBeenProcessed(int)
     */
    @Override
    public void notifyThatOrdersHaveNotBeenProcessed(int days) {
        final List<WorldpayOrderModificationModel> unprocessedOrderModifications = orderModificationDao.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(createDateInPast(days));
        if (!unprocessedOrderModifications.isEmpty()) {
            for (final WorldpayOrderModificationModel worldpayOrderModificationModel : unprocessedOrderModifications) {
                try {
                    final String worldpayOrderCode = worldpayOrderModificationModel.getWorldpayOrderCode();
                    final String unprocessedOrderMessage = format("{0} for worldpay order: {1}", l10nService.getLocalizedString(WORLDPAYNOTIFICATIONS_ERRORS_UNPROCESSED_ORDERS), worldpayOrderCode);
                    ticketBusinessService.createTicket(createParameters(unprocessedOrderMessage, worldpayOrderCode));
                    worldpayOrderModificationModel.setNotified(true);
                    modelService.save(worldpayOrderModificationModel);
                    LOG.info(unprocessedOrderMessage);
                } catch (Exception e) {
                    LOG.error("Error while creating ticket for unprocessed order: " + worldpayOrderModificationModel.getWorldpayOrderCode(), e);
                }
            }
        } else {
            LOG.info("No Unprocessed order were found");
        }
    }

    private CsTicketParameter createParameters(final String creationNotes, final String worldpayOrderCode) {
        final CsTicketParameter parameters = new CsTicketParameter();
        parameters.setCreationNotes(creationNotes);
        parameters.setHeadline(l10nService.getLocalizedString(WORLDPAYNOTIFICATIONS_ERRORS_THERE_ARE_UNPROCESSED_ORDERS));
        parameters.setCategory(CsTicketCategory.PROBLEM);
        parameters.setPriority(CsTicketPriority.HIGH);

        final OrderModel order = worldpayHybrisOrderService.findOrderByWorldpayOrderCode(worldpayOrderCode);
        parameters.setAssociatedTo(order);
        parameters.setCustomer(order.getUser());

        return parameters;
    }
}
