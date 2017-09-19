package com.worldpay.service.http;

import com.worldpay.exception.WorldpayCommunicationException;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.model.MerchantInfo;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;

/**
 * Interface template for the connector required to send the xml {@link PaymentService} to Worldpay.
 */
public interface WorldpayConnector {

    /**
     * Use the {@link MerchantInfo} to look up the merchant code, password. Connects to the Worldpay url, convert the {@link PaymentService} to xml and POSTs
     * this information to Worldpay using the merchant code and password. Convert the replied xml into a new PaymentService object and then return this.
     *
     * @param paymentService Internal model representation of the PaymentService xml to be sent to Worldpay
     * @param merchantInfo   Object representation of the merchant code, password, environment and version.
     * @param cookie         String cookie to be sent as HTTP header
     * @return {@link ServiceReply} object representation of the reply message xml and associated cookie if one exists.
     * @throws WorldpayCommunicationException       if there have been issues connecting with Worldpay
     * @throws WorldpayModelTransformationException if there have been issues transforming the xml to send, or received from Worldpay
     */
    ServiceReply send(PaymentService paymentService, MerchantInfo merchantInfo, String cookie) throws WorldpayCommunicationException, WorldpayModelTransformationException;

    void logXMLOut(XMLOutputFactory xof, Marshaller marshaller, PaymentService paymentService);
}
