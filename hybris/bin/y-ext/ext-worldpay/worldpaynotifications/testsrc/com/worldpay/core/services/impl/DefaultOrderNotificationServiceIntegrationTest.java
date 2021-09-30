package com.worldpay.core.services.impl;

import com.worldpay.converters.OrderModificationRequestConverter;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@IntegrationTest
public class DefaultOrderNotificationServiceIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private OrderNotificationService orderNotificationService;
    @Resource
    private OrderModificationRequestConverter orderNotificationRequestToMessageConverter;
    @Resource
    private OrderModificationDao orderModificationDao;
    @Resource
    private ModelService modelService;
    @Resource
    private PaymentServiceMarshaller paymentServiceMarshaller;

    private String orderCode;

    @Before
    public void setUp() throws WorldpayModelTransformationException {
        orderCode = String.valueOf(System.currentTimeMillis());
        final PaymentService paymentService = paymentServiceMarshaller.unmarshal(new ByteArrayInputStream(getXMLMessage().getBytes(StandardCharsets.UTF_8)));
        final OrderNotificationMessage orderNotificationMessage = orderNotificationRequestToMessageConverter.convert(paymentService);
        createOrderModificationModel(orderNotificationMessage);
    }

    @Test
    public void testSerialise() {
        final List<WorldpayOrderModificationModel> unprocessedOrderModificationsByType = orderModificationDao.findUnprocessedOrderModificationsByType(AUTHORIZATION);
        assertEquals(1, unprocessedOrderModificationsByType.size());
        assertTrue(unprocessedOrderModificationsByType.get(0).getOrderNotificationMessage().contains(orderCode));
    }

    @Test
    public void testDeserialise() {
        final List<WorldpayOrderModificationModel> unprocessedOrderModificationsByType = orderModificationDao.findUnprocessedOrderModificationsByType(AUTHORIZATION);
        assertEquals(1, unprocessedOrderModificationsByType.size());
        final String orderNotificationMessage = unprocessedOrderModificationsByType.get(0).getOrderNotificationMessage();
        final OrderNotificationMessage deserialisedMessage = orderNotificationService.deserialiseNotification(orderNotificationMessage);
        assertEquals(orderCode, deserialisedMessage.getOrderCode());
        assertEquals(AUTHORISED, deserialisedMessage.getJournalReply().getJournalType());
    }

    private void createOrderModificationModel(OrderNotificationMessage orderNotificationMessage) {
        final WorldpayOrderModificationModel worldpayOrderModificationModel = new WorldpayOrderModificationModel();
        worldpayOrderModificationModel.setType(AUTHORIZATION);
        worldpayOrderModificationModel.setWorldpayOrderCode(orderCode);
        worldpayOrderModificationModel.setOrderNotificationMessage(orderNotificationService.serialiseNotification(orderNotificationMessage));
        modelService.save(worldpayOrderModificationModel);
    }

    private String getXMLMessage() {
        return "<!DOCTYPE paymentService PUBLIC \"-//worldpay//DTD worldpay PaymentService v1//EN\"\n" +
            "        \"http://dtd.worldpay.com/paymentService_v1.dtd\">\n" +
            "<paymentService version=\"1.4\" merchantCode=\"MERCHANT1ECOM\">\n" +
            "    <notify>\n" +
            "        <orderStatusEvent orderCode=\"" + orderCode + "\">\n" +
            "            <payment>\n" +
            "                <paymentMethod>VISA-SSL</paymentMethod>\n" +
            "                <paymentMethodDetail>\n" +
            "                    <card number=\"4444********1111\" type=\"creditcard\">\n" +
            "                        <expiryDate>\n" +
            "                            <date month=\"01\" year=\"2022\"/>\n" +
            "                        </expiryDate>\n" +
            "                    </card>\n" +
            "                </paymentMethodDetail>\n" +
            "                <amount value=\"100.00\" currencyCode=\"GBP\" exponent=\"2\"\n" +
            "                        debitCreditIndicator=\"credit\"/>\n" +
            "                <lastEvent>AUTHORISED</lastEvent>\n" +
            "                <AuthorisationId id=\"666\"/>\n" +
            "                <CVCResultCode description=\"NOT CHECKED BY ACQUIRER\"/>\n" +
            "                <AVSResultCode description=\"NOT CHECKED BY ACQUIRER\"/>\n" +
            "                <cardHolderName><![CDATA[aaa bbb]]></cardHolderName>\n" +
            "                <issuerCountryCode>N/A</issuerCountryCode>\n" +
            "                <balance accountType=\"IN_PROCESS_AUTHORISED\">\n" +
            "                    <amount value=\"100.00\" currencyCode=\"GBP\" exponent=\"2\"\n" +
            "                            debitCreditIndicator=\"credit\"/>\n" +
            "                </balance>\n" +
            "                <riskScore value=\"1.00\"/>\n" +
            "            </payment>\n" +
            "            <journal journalType=\"AUTHORISED\">\n" +
            "                <bookingDate>\n" +
            "                    <date dayOfMonth=\"5\" month=\"6\" year=\"2015\"/>\n" +
            "                </bookingDate>\n" +
            "                <accountTx accountType=\"IN_PROCESS_AUTHORISED\" batchId=\"84\">\n" +
            "                    <amount value=\"100.00\" currencyCode=\"GBP\" exponent=\"2\"\n" +
            "                            debitCreditIndicator=\"credit\"/>\n" +
            "                </accountTx>\n" +
            "            </journal>\n" +
            "        </orderStatusEvent>\n" +
            "    </notify>\n" +
            "</paymentService>";
    }
}
