package com.worldpay.test.groovy.webservicetests;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;


public class DummyTrustManager extends X509ExtendedTrustManager {
    private static final X509Certificate[] ACCEPTED_ISSUERS = new X509Certificate[0];

    @Override
    public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) {
        // Empty on purpose
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) {
        // Empty on purpose
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ACCEPTED_ISSUERS;
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] arg0, final String arg1, final Socket arg2) {
        // Empty on purpose
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] arg0, final String arg1, final SSLEngine arg2) {
        // Empty on purpose
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] arg0, final String arg1, final Socket arg2) {
        // Empty on purpose
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] arg0, final String arg1, final SSLEngine arg2) {
        // Empty on purpose
    }
}
