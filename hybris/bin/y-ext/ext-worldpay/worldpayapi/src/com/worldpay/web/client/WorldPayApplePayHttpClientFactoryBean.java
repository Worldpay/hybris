package com.worldpay.web.client;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

public class WorldPayApplePayHttpClientFactoryBean extends AbstractFactoryBean<HttpClient> {
    private Resource certificateFile;
    private String password;
    private String keyStoreType;

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    protected HttpClient createInstance() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        final SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(getKeyStore(), password.toCharArray())
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();

        final SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(30L))
                .build();

        final PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext))
                .setDefaultSocketConfig(socketConfig)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .build();

    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        if (!certificateFile.exists() || !certificateFile.isReadable()) {
            throw new ResourceNotFoundException("Certificate in path [" + certificateFile.getFilename() + "] file does not exists");
        }
        keyStore.load(certificateFile.getInputStream(), this.password.toCharArray());
        return keyStore;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public void setCertificateFile(Resource certificateFile) {
        this.certificateFile = certificateFile;
    }
}
