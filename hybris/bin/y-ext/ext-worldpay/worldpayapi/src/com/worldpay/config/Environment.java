package com.worldpay.config;

/**
 * Enumeration of the Worldpay Environments available to end users.
 * <ul>
 * <li>TEST - https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp</li>
 * <li>PROD - </li>
 * </ul>
 */
public class Environment {

    private String endpoint;
    private EnvironmentRole role;

    public Environment(String endpoint, EnvironmentRole role) {
        this.endpoint = endpoint;
        this.role = role;
    }

    public EnvironmentRole getRole() {
        return role;
    }

    public void setRole(EnvironmentRole role) {
        this.role = role;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


    public enum EnvironmentRole {
        TEST("TEST"), PROD("PROD"), MOCK("MOCK");

        private String value;

        EnvironmentRole(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}