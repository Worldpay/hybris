package com.worldpay.xml.writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.regex.Pattern;

/**
 * Extension to the {@link DelegatingXMLStreamWriter} to ensure that any xml that contains [<>] characters encloses these into a CDATA section
 */
public class CDataXMLStreamWriter extends DelegatingXMLStreamWriter implements AutoCloseable {

    private static final Pattern XML_CHARS = Pattern.compile("[<>]");

    /**
     * Constructor taking the XMLStreamWriter that we are using to write out the xml
     * @param xmlStreamWriter xmlStreamWriter used for writing the xml
     */
    public CDataXMLStreamWriter(XMLStreamWriter xmlStreamWriter) {
        super(xmlStreamWriter);
    }

    /**
     * (non-Javadoc)
     * @see com.worldpay.xml.writer.DelegatingXMLStreamWriter#writeCharacters(String)
     */
    @Override
    public void writeCharacters(String paramString) throws XMLStreamException {
        boolean useCData = XML_CHARS.matcher(paramString).find();
        if (useCData) {
            super.writeCData(paramString);
        } else {
            super.writeCharacters(paramString);
        }
    }
}
