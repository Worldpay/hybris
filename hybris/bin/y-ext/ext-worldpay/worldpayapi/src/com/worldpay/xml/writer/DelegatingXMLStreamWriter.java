package com.worldpay.xml.writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class wraps a standard XMLStreamWriter but allows us to extend and add functionality to any of the methods
 */
public abstract class DelegatingXMLStreamWriter implements XMLStreamWriter {

    private XMLStreamWriter xmlStreamWriter;

    public DelegatingXMLStreamWriter(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    @Override
    public void writeStartElement(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(paramString);
    }

    @Override
    public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(paramString1, paramString2);
    }

    @Override
    public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(paramString1, paramString2, paramString3);
    }

    @Override
    public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(paramString1, paramString2);
    }

    @Override
    public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(paramString1, paramString2, paramString3);
    }

    @Override
    public void writeEmptyElement(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(paramString);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        xmlStreamWriter.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException {
        xmlStreamWriter.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    @Override
    public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(paramString1, paramString2);
    }

    @Override
    public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(paramString1, paramString2, paramString3, paramString4);
    }

    @Override
    public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(paramString1, paramString2, paramString3);
    }

    @Override
    public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.writeNamespace(paramString1, paramString2);
    }

    @Override
    public void writeDefaultNamespace(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeDefaultNamespace(paramString);
    }

    @Override
    public void writeComment(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeComment(paramString);
    }

    @Override
    public void writeProcessingInstruction(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(paramString);
    }

    @Override
    public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.writeProcessingInstruction(paramString1, paramString2);
    }

    @Override
    public void writeCData(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeCData(paramString);
    }

    @Override
    public void writeDTD(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeDTD(paramString);
    }

    @Override
    public void writeEntityRef(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeEntityRef(paramString);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        xmlStreamWriter.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(paramString);
    }

    @Override
    public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(paramString1, paramString2);
    }

    @Override
    public void writeCharacters(String paramString) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(paramString);
    }

    @Override
    public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    }

    @Override
    public String getPrefix(String paramString) throws XMLStreamException {
        return xmlStreamWriter.getPrefix(paramString);
    }

    @Override
    public void setPrefix(String paramString1, String paramString2) throws XMLStreamException {
        xmlStreamWriter.setPrefix(paramString1, paramString2);
    }

    @Override
    public void setDefaultNamespace(String paramString) throws XMLStreamException {
        xmlStreamWriter.setDefaultNamespace(paramString);
    }

    @Override
    public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException {
        xmlStreamWriter.setNamespaceContext(paramNamespaceContext);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return xmlStreamWriter.getNamespaceContext();
    }

    @Override
    public Object getProperty(String paramString) throws IllegalArgumentException {
        return xmlStreamWriter.getProperty(paramString);
    }
}
