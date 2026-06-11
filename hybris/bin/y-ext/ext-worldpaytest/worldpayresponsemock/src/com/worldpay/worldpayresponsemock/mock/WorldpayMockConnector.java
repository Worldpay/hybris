package com.worldpay.worldpayresponsemock.mock;

import com.worldpay.exception.WorldpayException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Mocked connector to Worldpay
 */
public class WorldpayMockConnector {

    private static final Logger LOG = Logger.getLogger(WorldpayMockConnector.class);

    private static final String WORLDPAYRESPONSEMOCK_ORDER_NOTIFICATION_ENDPOINT = "worldpayresponsemock.order.notification.endpoint";
    private static final String NOTIFICATION_EXTENSION_CONTEXT_ROOT = "worldpaynotifications.webroot";
    private static final String SCHEME_SEPARATOR = "://";
    private static final String PROTOCOL_SEPARATOR = ":";
    private static final String EXCEPTION_MESSAGE = "Exception sending response using the mocked connector";

    protected final RestTemplate worldpayRestTemplate;
    protected final ConfigurationService configurationService;

    public WorldpayMockConnector(final RestTemplate worldpayRestTemplate, final ConfigurationService configurationService) {
        this.worldpayRestTemplate = worldpayRestTemplate;
        this.configurationService = configurationService;
    }

    /**
     * Send the response
     *
     * @param request
     * @param responseXML
     * @throws WorldpayException
     */
    public void sendResponse(final HttpServletRequest request, final String responseXML) throws WorldpayException {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient;
        try {
            httpClient = buildAllowingAllHostNamesHttpClient();
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            LOG.error(EXCEPTION_MESSAGE, e);
            throw new WorldpayException(EXCEPTION_MESSAGE, e);
        }
        factory.setHttpClient(httpClient);
        worldpayRestTemplate.setRequestFactory(factory);
        worldpayRestTemplate.postForObject(constructEndpoint(request), responseXML, String.class);
    }

    /**
     * Builds an HttpClient that trusts all SSL certificates and allows all hostnames
     */
    protected CloseableHttpClient buildAllowingAllHostNamesHttpClient()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // SSL context that trusts all certificates
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();

        // SSLConnectionSocketFactory with NoopHostnameVerifier
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext, NoopHostnameVerifier.INSTANCE);

        // Connection manager with the SSL factory
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // Build HttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    /**
     * Constructs the full endpoint URL for sending the response
     */
    private String constructEndpoint(final HttpServletRequest request) {
        final String orderNotificationEndpointPostfix =
                configurationService.getConfiguration().getString(WORLDPAYRESPONSEMOCK_ORDER_NOTIFICATION_ENDPOINT);
        final String notificationExtensionContextRoot =
                configurationService.getConfiguration().getString(NOTIFICATION_EXTENSION_CONTEXT_ROOT);
        final String serverName = request.getServerName();
        final String scheme = request.getScheme();
        int serverPort = request.getServerPort();
        return scheme + SCHEME_SEPARATOR + serverName + PROTOCOL_SEPARATOR + serverPort
                + notificationExtensionContextRoot + orderNotificationEndpointPostfix;
    }
}
