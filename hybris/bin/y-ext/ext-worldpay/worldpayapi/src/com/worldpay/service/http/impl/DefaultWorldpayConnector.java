package com.worldpay.service.http.impl;

import com.google.common.collect.Iterables;
import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.data.MerchantInfo;
import com.worldpay.util.WorldpayConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation class to make HTTP Post of messages to Worldpay. Implements the {@link WorldpayConnector} interface.
 */
public class DefaultWorldpayConnector implements WorldpayConnector {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayConnector.class);

    protected static final String WORLDPAY_CONFIG_CONTEXT = "worldpay.config.context";
    protected static final String WORLDPAY_CONFIG_DOMAIN = "worldpay.config.domain";
    protected static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";

    protected final PaymentServiceMarshaller paymentServiceMarshaller;
    protected final ConfigurationService configurationService;
    protected final RestTemplate restTemplate;

    public DefaultWorldpayConnector(final PaymentServiceMarshaller paymentServiceMarshaller,
                                    final ConfigurationService configurationService,
                                    final RestTemplate restTemplate) {
        this.paymentServiceMarshaller = paymentServiceMarshaller;
        this.configurationService = configurationService;
        this.restTemplate = restTemplate;
    }

    @Override
    public ServiceReply send(final PaymentService outboundPaymentService, final MerchantInfo merchantInfo, final String cookie) throws WorldpayException {
        final AtomicReference<ResponseEntity<String>> responseXML = new AtomicReference<>();
        final Single<ResponseEntity<String>> response = sendOutboundXML(outboundPaymentService, merchantInfo, cookie);
        responseXML.set(response.toBlocking().value());
        return processResponseXML(responseXML.get());
    }

    protected Single<ResponseEntity<String>> sendOutboundXML(final PaymentService paymentService,
                                                             final MerchantInfo merchantInfo,
                                                             final String cookie) throws WorldpayException {
        final String environment = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT);
        final String domain = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_DOMAIN + "." + environment);
        final String context = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_CONTEXT + "." + environment);
        final String endpoint = domain + context;


        final URI uri = URI.create(endpoint);
        final HttpHeaders headers = configureHttpHeaders(merchantInfo, cookie, uri.getHost());
        final HttpEntity<String> request = configureRequest(paymentService, headers);

        final AtomicInteger maxAttemptNumber = new AtomicInteger(0);

        return Observable.just(restTemplate)
            .map(template -> template.postForEntity(uri, request, String.class))
            .retryWhen(observable -> observable.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                // Retry just if the number if attempt is lower than max number attempts and if is an IOException or ResourceAccessException
                if ((throwable instanceof IOException || throwable instanceof ResourceAccessException) && maxAttemptNumber.getAndIncrement() < 3) {
                    // Retry code
                    return Observable.timer(200, TimeUnit.MILLISECONDS);
                }
                // Pass the throwable
                return Observable.error(throwable);
            }))
            .toSingle();
    }

    private HttpHeaders configureHttpHeaders(final MerchantInfo merchantInfo, final String cookie, final String host) {
        final byte[] plainCreds = (merchantInfo.getMerchantCode() + ":" + merchantInfo.getMerchantPassword()).getBytes(StandardCharsets.UTF_8);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.getEncoder().encode(plainCreds), StandardCharsets.UTF_8));
        headers.add(HttpHeaders.HOST, host);
        headers.add(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_XML.withCharset(StandardCharsets.UTF_8).toString());
        Optional.ofNullable(cookie).ifPresent(cookieValue -> headers.add(HttpHeaders.COOKIE, cookieValue));
        return headers;
    }

    private HttpEntity<String> configureRequest(final PaymentService paymentService, final HttpHeaders headers) throws WorldpayException {
        final String marshaledXML = paymentServiceMarshaller.marshalAsFragment(paymentService);
        return new HttpEntity<>(WorldpayConstants.XML_HEADER + marshaledXML, headers);
    }

    private ServiceReply processResponseXML(final ResponseEntity<String> responseXML) throws WorldpayException {
        final ServiceReply serviceReply = new ServiceReply();
        final ByteBuffer encode = StandardCharsets.ISO_8859_1.encode(responseXML.getBody());
        final String response = StandardCharsets.UTF_8.decode(encode).toString();

        serviceReply.setCookie(Iterables.getFirst(responseXML.getHeaders().get("Set-Cookie"), ""));
        serviceReply.setPaymentService(paymentServiceMarshaller.unmarshal(IOUtils.toInputStream(response, StandardCharsets.UTF_8)));

        return serviceReply;
    }

    @Override
    public String logXMLOut(final PaymentService paymentService) {
        try {
            LOG.info("*** XML OUT ***");
            final String parsedPaymentService = paymentServiceMarshaller.marshal(paymentService);
            LOG.info(parsedPaymentService);
            LOG.info("*** XML OUT END ***");
            return parsedPaymentService;
        } catch (final WorldpayException e) {
            LOG.debug("There was an error marshalling the paymentService for debug logging", e);
        }
        return null;
    }
}
