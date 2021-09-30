package com.worldpay.strategies.impl;

import com.worldpay.core.services.WorldpayHybrisOrderService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationNotifierStrategyTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final int UNPROCESSED_DAYS = 5;
    private static final String THERE_ARE_UNPROCESSED_ORDERS = "thereAreUnprocessedOrders";
    private static final String UNPROCESSED_ORDERS = "unprocessedOrders";
    private static final String WORLDPAYNOTIFICATIONS_ERRORS_UNPROCESSED_ORDERS = "worldpaynotifications.errors.unprocessed.orders";
    private static final String WORLDPAYNOTIFICATIONS_ERRORS_THERE_ARE_UNPROCESSED_ORDERS = "worldpaynotifications.errors.there.are.unprocessed.orders";

    @InjectMocks
    private DefaultWorldpayOrderModificationNotifierStrategy testObj;

    @Mock
    private TicketBusinessService ticketBusinessServiceMock;
    @Mock
    private OrderModificationDao orderModificationDaoMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private L10NService l10nService;
    @Mock
    private WorldpayHybrisOrderService worldpayHybrisOrderServiceMock;
    @Mock
    private WorldpayOrderModificationModel orderModificationModelMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private UserModel userMock;

    @Captor
    private ArgumentCaptor<CsTicketParameter> csTicketParameterArgumentCaptor;

    @Before
    public void setUp() {
        when(orderModificationModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(orderModificationModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModificationModelMock.getProcessed()).thenReturn(false);
        when(orderModificationModelMock.getNotified()).thenReturn(false);
        when(l10nService.getLocalizedString(WORLDPAYNOTIFICATIONS_ERRORS_THERE_ARE_UNPROCESSED_ORDERS)).thenReturn(THERE_ARE_UNPROCESSED_ORDERS);
        when(l10nService.getLocalizedString(WORLDPAYNOTIFICATIONS_ERRORS_UNPROCESSED_ORDERS)).thenReturn(UNPROCESSED_ORDERS);
        when(worldpayHybrisOrderServiceMock.findOrderByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(orderMock);
        when(orderMock.getUser()).thenReturn(userMock);
    }

    @Test
    public void notifyThatOrdersHaveNotBeenProcessed_WhenUnprocessedModification_ShouldReturnedThenPublishTicket() {
        when(orderModificationDaoMock.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(any(Date.class))).thenReturn(Collections.singletonList(orderModificationModelMock));
        when(modelServiceMock.create(CsTicketModel.class)).thenReturn(new CsTicketModel());

        testObj.notifyThatOrdersHaveNotBeenProcessed(UNPROCESSED_DAYS);

        verify(ticketBusinessServiceMock).createTicket(csTicketParameterArgumentCaptor.capture());
        verify(orderModificationModelMock).setNotified(true);
        verify(modelServiceMock).save(orderModificationModelMock);
        final CsTicketParameter csTicketParameter = csTicketParameterArgumentCaptor.getValue();

        assertEquals(THERE_ARE_UNPROCESSED_ORDERS, csTicketParameter.getHeadline());
        assertEquals(CsTicketCategory.PROBLEM, csTicketParameter.getCategory());
        assertEquals(CsTicketPriority.HIGH, csTicketParameter.getPriority());
        assertEquals(orderMock, csTicketParameter.getAssociatedTo());
        assertEquals(userMock, csTicketParameter.getCustomer());

        assertTrue(csTicketParameter.getCreationNotes().startsWith(UNPROCESSED_ORDERS));
    }

    @Test
    public void notifyThatOrdersHaveNotBeenProcessed_WhenNoUnprocessedModifications_ShouldNotPublishTickets() {
        when(orderModificationDaoMock.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(new Date())).thenReturn(Collections.emptyList());

        testObj.notifyThatOrdersHaveNotBeenProcessed(5);

        verify(ticketBusinessServiceMock, never()).createTicket(any(CsTicketParameter.class));
    }
}
