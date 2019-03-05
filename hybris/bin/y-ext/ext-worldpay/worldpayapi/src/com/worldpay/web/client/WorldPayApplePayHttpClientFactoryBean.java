package com.worldpay.web.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

public class WorldPayApplePayHttpClientFactoryBean extends AbstractFactoryBean<HttpClient> {
    private Resource certificateFile;
    private String password;
    private String keyStoreType;

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    @SuppressWarnings("squid:S00112")
    protected HttpClient createInstance() throws Exception {
        final SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(getKeyStore(), password.toCharArray())
                .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

        return HttpClients.custom().setSSLContext(sslContext).build();
    }

    @SuppressWarnings("squid:S00112")
    private KeyStore getKeyStore() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        if (!certificateFile.exists() || !certificateFile.isReadable()) {
            throw new ResourceNotFoundException("Certificate in path [" + certificateFile.getFilename() + "] file does not exists");
        }
        keyStore.load(certificateFile.getInputStream(), this.password.toCharArray());
        return keyStore;
    }

    @Required
    public void setPassword(final String password) {
        this.password = password;
    }

    @Required
    public void setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    @Required
    public void setCertificateFile(Resource certificateFile) {
        this.certificateFile = certificateFile;
    }
}
