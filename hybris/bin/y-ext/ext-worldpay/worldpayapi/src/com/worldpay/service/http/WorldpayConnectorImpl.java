package com.worldpay.service.http;

import com.worldpay.exception.WorldpayCommunicationException;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.util.WorldpayConstants;
import com.worldpay.xml.writer.CDataXMLStreamWriter;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;

/**
 * Implementation class to make HTTP Post of messages to Worldpay. Implements the {@link WorldpayConnector} interface.
 */
public class WorldpayConnectorImpl implements WorldpayConnector {

    private static final Logger LOG = Logger.getLogger(WorldpayConnectorImpl.class);

    private final PaymentServiceMarshaller paymentServiceMarshaller;
    private String endpoint;

    /**
     * Constructor sets up a connector with the given endpoint
     * @param endpoint
     */
    public WorldpayConnectorImpl(String endpoint) {
        this.endpoint = endpoint;
        this.paymentServiceMarshaller = DefaultPaymentServiceMarshaller.getInstance();
    }

    @Override
    public ServiceReply send(PaymentService paymentService, MerchantInfo merchantInfo, String cookie) throws WorldpayCommunicationException, WorldpayModelTransformationException {
        URLConnection connection = sendXML(paymentService, merchantInfo, cookie);
        return receiveXML(connection);
    }

    private ServiceReply receiveXML(URLConnection connection) throws WorldpayCommunicationException, WorldpayModelTransformationException {
        ServiceReply response = new ServiceReply();
        try (InputStream in = connection.getInputStream()) {
            setCookiesOnResponse(connection, response);
            response.setPaymentService(paymentServiceMarshaller.unmarshal(in));
        } catch (IOException e) {
            throw new WorldpayCommunicationException("Unable to receive response from Worldpay", e);
        } catch (JAXBException e) {
            throw new WorldpayModelTransformationException("XML context or unmarshalling failure while receiving response from Worldpay", e);
        }
        return response;
    }

    private void setCookiesOnResponse(URLConnection connection, ServiceReply response) {
        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
        if (cookies != null && !cookies.isEmpty()) {
            String cookie = cookies.get(0);
            response.setCookie(cookie);
        }
    }

    private URLConnection sendXML(PaymentService paymentService, MerchantInfo merchantInfo, String cookie) throws WorldpayCommunicationException, WorldpayModelTransformationException {
        URLConnection con;
        try {
            URL url = new URL(getEndpoint());
            con = url.openConnection();

            con.setDoOutput(true);
            con.setUseCaches(false);
            byte[] loginPassword = (merchantInfo.getMerchantCode() + ":" + merchantInfo.getMerchantPassword()).getBytes("UTF-8");
            con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode(loginPassword)));
            con.setRequestProperty("Host", url.getHost());

            if (con instanceof HttpURLConnection) {
                HttpURLConnection httpCon = (HttpURLConnection) con;
                httpCon.setRequestMethod("POST");
                if (cookie != null) {
                    httpCon.setRequestProperty("Cookie", cookie);
                }
            }

            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = xof.createXMLStreamWriter(con.getOutputStream(), "UTF-8");
            try (CDataXMLStreamWriter cdataStreamWriter = new CDataXMLStreamWriter(streamWriter)) {
                Marshaller marshaller = WorldpayConstants.JAXB_CONTEXT.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                cdataStreamWriter.writeDTD(WorldpayConstants.XML_HEADER);
                marshaller.marshal(paymentService, cdataStreamWriter);
            }
        } catch (MalformedURLException e) {
            throw new WorldpayCommunicationException("Worldpay URL is incorrect", e);
        } catch (IOException e) {
            throw new WorldpayCommunicationException("Unable to initiate communication with Worldpay", e);
        } catch (JAXBException e) {
            throw new WorldpayModelTransformationException("XML context or marshalling failure while sending message to Worldpay", e);
        } catch (XMLStreamException e) {
            throw new WorldpayCommunicationException("Exception generating XML stream while sending message to Worldpay", e);
        }
        return con;
    }

    @Override
    public void logXMLOut(XMLOutputFactory xof, Marshaller marshaller, PaymentService paymentService) {
        try {
            LOG.info("*** XML OUT ***");
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(paymentService, stringWriter);
            LOG.info(stringWriter.toString());
            LOG.info("*** XML OUT END ***");
        } catch (JAXBException e) {
            LOG.debug("There was an error marshalling the paymentService for debug logging", e);
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
