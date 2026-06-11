package com.worldpay.voidprocess.adapters;

import de.hybris.platform.ordercancel.OrderCancelNotificationServiceAdapter;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.event.EventService;


/**
 * Sends cancel notifications
 */
public class WorldpayOrderCancelNotificationServiceAdapter implements OrderCancelNotificationServiceAdapter {

    protected final EventService eventService;

    public WorldpayOrderCancelNotificationServiceAdapter(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void sendCancelFinishedNotifications(final OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        eventService.publishEvent(new CancelFinishedEvent(orderCancelRecordEntryModel));
    }

    @Override
    public void sendCancelPendingNotifications(final OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        //Deliberately does nothing...
    }

}
