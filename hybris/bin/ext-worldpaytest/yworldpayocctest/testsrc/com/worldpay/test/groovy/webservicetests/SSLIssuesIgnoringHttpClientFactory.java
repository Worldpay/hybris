/*
 * Forked from ycommercewebservicestest
 */
package com.worldpay.test.groovy.webservicetests;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public class SSLIssuesIgnoringHttpClientFactory {
    public static HttpClient createHttpClient() {
        try {
            final TrustManager[] trustAllCerts = {new DummyTrustManager()};

            final SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            httpClientBuilder.setSSLHostnameVerifier(new DummyHostnameVerifier());
            httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, new DummyHostnameVerifier()));

            return httpClientBuilder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Not able to create http client", e);
        }
    }
}
