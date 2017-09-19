package com.worldpay.config;

import com.worldpay.service.WorldpayServiceGateway;

/**
 * WorldpayConfig encapsulates the configuration details required by each call through the {@link WorldpayServiceGateway}. Each config stores details
 * of the endpoint and version.
 */
public class WorldpayConfig {

    private String version;
    private Environment environment;

    public WorldpayConfig(String version, Environment environment) {
        this.version = version;
        this.environment = environment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
