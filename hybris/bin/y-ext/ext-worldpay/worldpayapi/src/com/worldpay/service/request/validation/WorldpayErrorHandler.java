package com.worldpay.service.request.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Worldpay extension to the {@link ErrorHandler}
 * <p/>
 * <p>Ensures that error and fatalError generate a SAXParseException which will be captured in the {@link WorldpayXMLValidator}</p>
 */
public class WorldpayErrorHandler implements ErrorHandler {

    /**
     * (non-Javadoc)
     *
     * @see ErrorHandler#warning(SAXParseException)
     */
    @Override
    public void warning(SAXParseException paramSAXParseException) throws SAXException {
        // Do nothing for warnings
    }

    /**
     * (non-Javadoc)
     *
     * @see ErrorHandler#error(SAXParseException)
     */
    @Override
    public void error(SAXParseException paramSAXParseException) throws SAXException {
        throw paramSAXParseException;
    }

    /**
     * (non-Javadoc)
     *
     * @see ErrorHandler#fatalError(SAXParseException)
     */
    @Override
    public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
        throw paramSAXParseException;
    }
}
