package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of the request 3D info
 */
public class Request3DInfo implements Serializable {

    private String paRequest;
    private String issuerUrl;
    private String transactionId3DS;
    private String major3DSVersion;
    private String issuerPayload;

    public Request3DInfo() {
    }

    /**
     * Constructor for legacy flow
     *  @param paRequest
     * @param issuerUrl
     */
    public Request3DInfo(final String paRequest, final String issuerUrl) {
        this.paRequest = paRequest;
        this.issuerUrl = issuerUrl;
    }

    /**
     * Constructor for 3ds Flex flow
     *
     * @param issuerUrl
     * @param transactionId3DS
     * @param major3DSVersion
     * @param issuerPayload
     */
    public Request3DInfo(final String issuerUrl, final String transactionId3DS, final String major3DSVersion, final String issuerPayload) {
        this.issuerUrl = issuerUrl;
        this.transactionId3DS = transactionId3DS;
        this.major3DSVersion = major3DSVersion;
        this.issuerPayload = issuerPayload;
    }

    public String getPaRequest() {
        return paRequest;
    }

    public void setPaRequest(final String paRequest) {
        this.paRequest = paRequest;
    }

    public String getIssuerUrl() {
        return issuerUrl;
    }

    public void setIssuerUrl(final String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    public String getTransactionId3DS() {
        return transactionId3DS;
    }

    public void setTransactionId3DS(final String transactionId3DS) {
        this.transactionId3DS = transactionId3DS;
    }

    public String getMajor3DSVersion() {
        return major3DSVersion;
    }

    public void setMajor3DSVersion(final String major3DSVersion) {
        this.major3DSVersion = major3DSVersion;
    }


    public String getIssuerPayload() {
        return issuerPayload;
    }

    public void setIssuerPayload(final String issuerPayload) {
        this.issuerPayload = issuerPayload;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Request3DInfo ["
                + "paRequest=" + paRequest +
                ", issuerUrl=" + issuerUrl +
                ", transactionId3DS=" + transactionId3DS +
                ", major3DSVersion=" + major3DSVersion +
                ", issuerPayload=" + issuerPayload
                + "]";
    }
}
