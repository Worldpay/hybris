package com.worldpay.service.http.impl;

import com.google.common.collect.Iterables;
import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.util.WorldpayConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Implementation class to make HTTP Post of messages to Worldpay. Implements the {@link WorldpayConnector} interface.
 */
public class DefaultWorldpayConnector implements WorldpayConnector {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayConnector.class);

    protected static final String WORLDPAY_CONFIG_ENDPOINT = "worldpay.config.endpoint";
    protected static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";

    private PaymentServiceMarshaller paymentServiceMarshaller;
    private ConfigurationService configurationService;
    private RestTemplate restTemplate;

    @Override
    public ServiceReply send(final PaymentService paymentService, final MerchantInfo merchantInfo, final String cookie) throws WorldpayException {
        final ResponseEntity<String> responseEntity = sendXML(paymentService, merchantInfo, cookie);
        return receiveXML(responseEntity);
    }

    private ResponseEntity<String> sendXML(final PaymentService paymentService,
                                           final MerchantInfo merchantInfo,
                                           final String cookie) throws WorldpayException {
        final String environment = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT);
        final String endpoint = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENDPOINT + "." + environment);

        final URI uri = URI.create(endpoint);
        final HttpHeaders headers = configureHttpHeaders(merchantInfo, cookie, uri.getHost());
        final HttpEntity<String> request = configureRequest(paymentService, headers);
        return restTemplate.postForEntity(uri, request, String.class);
    }

    private HttpHeaders configureHttpHeaders(final MerchantInfo merchantInfo, final String cookie, final String host) {
        final byte[] plainCreds = (merchantInfo.getMerchantCode() + ":" + merchantInfo.getMerchantPassword()).getBytes(StandardCharsets.UTF_8);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.getEncoder().encode(plainCreds), StandardCharsets.UTF_8));
        headers.add(HttpHeaders.HOST, host);
        Optional.ofNullable(cookie).ifPresent(cookieValue -> headers.add(HttpHeaders.COOKIE, cookieValue));
        return headers;
    }

    private HttpEntity<String> configureRequest(final PaymentService paymentService, final HttpHeaders headers) throws WorldpayException {
        final String marshaledXML = paymentServiceMarshaller.marshalAsFragment(paymentService);
        return new HttpEntity<>(WorldpayConstants.XML_HEADER + marshaledXML, headers);
    }

    private ServiceReply receiveXML(final ResponseEntity<String> responseXML) throws WorldpayException {
        final ServiceReply serviceReply = new ServiceReply();
        serviceReply.setCookie(Iterables.getFirst(responseXML.getHeaders().get("Set-Cookie"), ""));
        serviceReply.setPaymentService(paymentServiceMarshaller.unmarshal(IOUtils.toInputStream(responseXML.getBody())));
        return serviceReply;
    }

    @Override
    public void logXMLOut(final PaymentService paymentService) {
        try {
            LOG.info("*** XML OUT ***");
            LOG.info(paymentServiceMarshaller.marshal(paymentService));
            LOG.info("*** XML OUT END ***");
        } catch (final WorldpayException e) {
            LOG.debug("There was an error marshalling the paymentService for debug logging", e);
        }
    }

    @Required
    public void setPaymentServiceMarshaller(final PaymentServiceMarshaller paymentServiceMarshaller) {
        this.paymentServiceMarshaller = paymentServiceMarshaller;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Required
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
