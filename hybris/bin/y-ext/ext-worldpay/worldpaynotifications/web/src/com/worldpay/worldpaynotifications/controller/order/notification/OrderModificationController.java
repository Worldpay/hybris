package com.worldpay.worldpaynotifications.controller.order.notification;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * OrderModificationController
 * <p/>
 * <p>
 * The OrderModificationController receives the order notification messages from worldpay and in turn initiates a process to
 * write it to a queue from which it will be picked up and processed.
 * </p>
 */
@Controller
@RequestMapping(value = "/worldpay/merchant_callback")
public class OrderModificationController {
    protected static final String WORLDPAY_RESPONSE_OK_VIEW = "pages/orderNotification/worldpayResponseOkView";

    private static final Logger LOG = Logger.getLogger(OrderModificationController.class);

    @Resource
    private Set<AuthorisedStatus> processableJournalTypeCodes;

    @Resource(name = "orderNotificationRequestToMessageConverter")
    private Converter<PaymentService, OrderNotificationMessage> orderModificationRequestConverter;

    @Resource
    private EventService eventService;

    @Resource
    private PaymentServiceMarshaller paymentServiceMarshaller;

    /**
     * This is the entry point for the Order Notification process process. The page returned by this method contains the
     * unconditional "[OK]" that will be sent to worldpay.
     *
     * @param request - the HttpServletRequest carrying the order notification XML from worldpay.
     * @return - the world pay response page containing the unconditional [OK].
     */
    @RequestMapping(method = POST)
    public String processOrderNotification(final HttpServletRequest request) {
        try {
            final OrderNotificationMessage orderNotificationMessage = createOrderModificationMessageFromRequest(request);
            if (shouldProcessModificationMessage(orderNotificationMessage.getJournalReply().getJournalType())) {
                eventService.publishEvent(new OrderModificationEvent(orderNotificationMessage));
            }
        } catch (WorldpayModelTransformationException | IOException e) {
            LOG.error("Notification message transformation error", e);
        }
        return WORLDPAY_RESPONSE_OK_VIEW;
    }

    private OrderNotificationMessage createOrderModificationMessageFromRequest(HttpServletRequest request) throws WorldpayModelTransformationException, IOException {
        final PaymentService paymentService = paymentServiceMarshaller.unmarshal(request.getInputStream());
        return orderModificationRequestConverter.convert(paymentService);
    }

    protected boolean shouldProcessModificationMessage(final AuthorisedStatus journalTypeCode) {
        return getProcessableJournalTypeCodes().contains(journalTypeCode);
    }

    public Set<AuthorisedStatus> getProcessableJournalTypeCodes() {
        return processableJournalTypeCodes;
    }

    public void setProcessableJournalTypeCodes(Set<AuthorisedStatus> processableJournalTypeCodes) {
        this.processableJournalTypeCodes = processableJournalTypeCodes;
    }
}
