package com.worldpay.service.request.validation.impl;

import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.validation.WorldpayErrorHandler;
import com.worldpay.service.request.validation.WorldpayXMLValidator;
import com.worldpay.util.WorldpayConstants;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.net.URL;

/**
 * This class uses standard java classes to validate that the xml that {@link PaymentService} will generate matches an associated xsd
 * <p/>
 * <p>Uses JAXB implementation that validates against an xsd and throws SAXParseException if there are any errors. The associated {@link WorldpayErrorHandler} is associated to
 * the JAXB validator at initialisation</p>
 */
public class DefaultWorldpayXMLValidator implements WorldpayXMLValidator {

    private static final Schema SCHEMA;

    private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";

    static {
        try {
            final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            sf.setFeature(SCHEMA_FULL_CHECKING, false);

            final URL url = WorldpayConstants.class.getResource(WorldpayConstants.XSD_LOCATION);
            SCHEMA = sf.newSchema(url);
        } catch (final SAXException e) {
            throw new IllegalStateException("Failed to initialise Schema", e);
        }
    }

    @Override
    public void validate(final PaymentService paymentService) throws WorldpayValidationException {
        try {
            final JAXBSource source = new JAXBSource(WorldpayConstants.JAXB_CONTEXT, paymentService);
            final Validator validator = SCHEMA.newValidator();
            validator.setErrorHandler(new WorldpayErrorHandler());
            validator.validate(source);
        } catch (final JAXBException e) {
            throw new WorldpayValidationException("XML context or source building failure while validating xml model", e);
        } catch (final SAXException e) {
            throw new WorldpayValidationException("Validation error against paymentService xsd", e);
        } catch (final IOException e) {
            throw new WorldpayValidationException("Unable to parse or validate against paymentService xsd", e);
        }
    }
}
