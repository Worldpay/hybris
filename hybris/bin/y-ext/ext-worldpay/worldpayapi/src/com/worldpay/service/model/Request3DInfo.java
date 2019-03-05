package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of the request 3D info
 */
public class Request3DInfo implements Serializable {

    private String paRequest;
    private String issuerUrl;

    public Request3DInfo() {
    }

    /**
     * Constructor with full list of fields
     *
     * @param paRequest
     * @param issuerUrl
     */
    public Request3DInfo(String paRequest, String issuerUrl) {
        this.paRequest = paRequest;
        this.issuerUrl = issuerUrl;
    }

    public String getPaRequest() {
        return paRequest;
    }

    public void setPaRequest(String paRequest) {
        this.paRequest = paRequest;
    }

    public String getIssuerUrl() {
        return issuerUrl;
    }

    public void setIssuerUrl(String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Request3DInfo [paRequest=" + paRequest + ", issuerUrl=" + issuerUrl + "]";
    }
}
