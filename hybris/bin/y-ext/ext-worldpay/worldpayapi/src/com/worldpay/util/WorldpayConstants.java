package com.worldpay.util;

import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Worldpay Constants
 */
public class WorldpayConstants {

    private static final Logger LOG = Logger.getLogger(WorldpayConstants.class);

    /**
     * Package location holding the internal model objects
     */
    public static final String WORLDPAY_MODEL_PACKAGE = "com.worldpay.internal.model";

    /**
     * Initialised JAXB context
     */
    public static final JAXBContext JAXB_CONTEXT = initContext();

    /**
     * Location of the xsd file for validating xml
     */
    public static final String XSD_LOCATION = "/schema/paymentService_v1.xsd";


    /**
     * Constant xml header to be added to all outgoing messages to Worldpay
     */
    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE paymentService PUBLIC \"-//Worldpay//DTD Worldpay PaymentService v1//EN\" \"http://dtd.worldpay.com/paymentService_v1.dtd\">\n";

    private static JAXBContext initContext() {
        JAXBContext jaxBContext = null;
        try {
            jaxBContext = JAXBContext.newInstance(WORLDPAY_MODEL_PACKAGE);
        } catch (JAXBException e) {
            LOG.error("Error creating JAXBContext", e);
        }
        return jaxBContext;
    }

}
