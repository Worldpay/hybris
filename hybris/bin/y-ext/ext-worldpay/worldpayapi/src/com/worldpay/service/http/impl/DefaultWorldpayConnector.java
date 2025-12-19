package com.worldpay.service.http.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Iterables;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.partnertracker.AdditionalDetails;
import com.worldpay.data.partnertracker.PluginData;
import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.WorldpayIntegrationVersionService;
import com.worldpay.service.http.ServiceReply;
import com.worldpay.service.http.WorldpayConnector;
import com.worldpay.service.marshalling.PaymentServiceMarshaller;
import com.worldpay.util.WorldpayConstants;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
    protected static final String ECOMMERCE_PLATFORM_HEADER = "ecommerce_platform";
    protected static final String ECOMMERCE_PLATFORM_VERSION_HEADER = "ecommerce_platform_version";
    protected static final String ECOMMERCE_PLUGIN_DATA_HEADER = "ecommerce_plugin_data";
    protected static final String ECOMMERCE_PLATFORM_HEADER_VALUE = "SAP Commerce Cloud";
    protected static final String PAYMENT_METHOD_PARAM = "paymentMethod";
    protected static final String ECOMMERCE_PLATFORM_HEADER_VERSION_VALUE = "build.version.api";
    protected static final String ECOMMERCE_PLATFORM_HEADER_EDITION_VALUE = "build.version";
    protected static final String CURRENCY = "currency";

    protected final PaymentServiceMarshaller paymentServiceMarshaller;
    protected final ConfigurationService configurationService;
    protected final RestTemplate restTemplate;
    protected final SessionService sessionService;
    protected final WorldpayIntegrationVersionService worldpayIntegrationVersionService;

    private final ObjectWriter objectWriter;


    public DefaultWorldpayConnector(final PaymentServiceMarshaller paymentServiceMarshaller,
                                    final ConfigurationService configurationService,
                                    final RestTemplate restTemplate,
                                    final SessionService sessionService,
                                    final WorldpayIntegrationVersionService worldpayIntegrationVersionService) {
        this.paymentServiceMarshaller = paymentServiceMarshaller;
        this.configurationService = configurationService;
        this.restTemplate = restTemplate;
        this.sessionService = sessionService;
        this.worldpayIntegrationVersionService = worldpayIntegrationVersionService;
        this.objectWriter = new ObjectMapper().writer();
    }

    @Override
    public ServiceReply send(final PaymentService outboundPaymentService, final MerchantInfo merchantInfo, final String cookie, final String paymentMethod) throws WorldpayException {
        final AtomicReference<ResponseEntity<String>> responseXML = new AtomicReference<>();
        final Single<ResponseEntity<String>> response = sendOutboundXML(outboundPaymentService, merchantInfo, cookie, paymentMethod);
        responseXML.set(response.toBlocking().value());
        return processResponseXML(responseXML.get());
    }

    protected Single<ResponseEntity<String>> sendOutboundXML(final PaymentService paymentService,
                                                             final MerchantInfo merchantInfo,
                                                             final String cookie,
                                                             final String paymentMethod) throws WorldpayException {
        final String environment = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT);
        final String domain = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_DOMAIN + "." + environment);
        final String context = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_CONTEXT + "." + environment);
        final String endpoint = domain + context;


        final URI uri = URI.create(endpoint);
        final HttpHeaders headers = configureHttpHeaders(merchantInfo, cookie, uri.getHost(), paymentMethod);
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

    private HttpHeaders configureHttpHeaders(final MerchantInfo merchantInfo, final String cookie, final String host, final String paymentMethod) {
        final byte[] plainCreds = (merchantInfo.getMerchantCode() + ":" + merchantInfo.getMerchantPassword()).getBytes(StandardCharsets.UTF_8);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.getEncoder().encode(plainCreds), StandardCharsets.UTF_8));
        headers.add(HttpHeaders.HOST, host);
        headers.add(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_XML.withCharset(StandardCharsets.UTF_8).toString());
        headers.add(ECOMMERCE_PLATFORM_HEADER, ECOMMERCE_PLATFORM_HEADER_VALUE);
        headers.add(ECOMMERCE_PLATFORM_VERSION_HEADER, configurationService.getConfiguration().getString(ECOMMERCE_PLATFORM_HEADER_VERSION_VALUE));

        final PluginData data = new PluginData();
        data.setIntegrationVersion(worldpayIntegrationVersionService.getCurrentIntegrationVersionValue());
        data.setHistoricIntegrationVersion(worldpayIntegrationVersionService.getPreviousThreeIntegrationVersions());
        data.setEcommercePlatformEdition(configurationService.getConfiguration().getString(ECOMMERCE_PLATFORM_HEADER_EDITION_VALUE, ""));

        final AdditionalDetails additionalDetails = new AdditionalDetails();
        additionalDetails.setPaymentMethod(StringUtils.isNotBlank(paymentMethod) ?
                paymentMethod :
                Optional.ofNullable(sessionService.getAttribute(PAYMENT_METHOD_PARAM))
                .orElse("")
                .toString());
        additionalDetails.setCurrency(Optional.ofNullable(sessionService.getAttribute(CURRENCY))
                .filter(CurrencyModel.class::isInstance)
                .map(CurrencyModel.class::cast)
                .map(CurrencyModel::getIsocode)
                .orElse(""));
        data.setAdditionalDetails(additionalDetails);

        try {
            final String serializedData = objectWriter.writeValueAsString(data);
            headers.add(ECOMMERCE_PLUGIN_DATA_HEADER, serializedData);
        } catch (final JsonProcessingException e) {
            LOG.error("Error serializing plugin data for headers", e);
        }

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
