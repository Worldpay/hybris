package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

/**
 * POJO representation of the browser details
 */
public class Browser implements InternalModelTransformer, Serializable {

    private String acceptHeader;
    private String userAgentHeader;
    private String deviceType;
    private String deviceOS;
    private String httpAcceptLanguage;
    private String httpReferer;

    /**
     * Constructor with full list of fields
     *
     * @param acceptHeader
     * @param userAgentHeader
     * @param deviceType
     * @param deviceOs
     */
    public Browser(String acceptHeader, String userAgentHeader, String deviceType, final String deviceOs, final String httpAcceptLanguage, final String httpReferer) {
        this.deviceType = deviceType;
        this.acceptHeader = acceptHeader;
        this.userAgentHeader = userAgentHeader;
        this.deviceOS = deviceOs;
        this.httpAcceptLanguage = httpAcceptLanguage;
        this.httpReferer = httpReferer;
    }

    public Browser(String acceptHeader, String userAgentHeader, String deviceType) {
        this.deviceType = deviceType;
        this.acceptHeader = acceptHeader;
        this.userAgentHeader = userAgentHeader;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        com.worldpay.internal.model.Browser intBrowser = new com.worldpay.internal.model.Browser();

        intBrowser.setAcceptHeader(acceptHeader);
        intBrowser.setUserAgentHeader(userAgentHeader);
        intBrowser.setDeviceType(deviceType);
        intBrowser.setDeviceOS(deviceOS);
        intBrowser.setHttpAcceptLanguage(httpAcceptLanguage);
        intBrowser.setHttpReferer(httpReferer);

        return intBrowser;
    }

    public String getAcceptHeader() {
        return acceptHeader;
    }

    public void setAcceptHeader(String acceptHeader) {
        this.acceptHeader = acceptHeader;
    }

    public String getUserAgentHeader() {
        return userAgentHeader;
    }

    public void setUserAgentHeader(String userAgentHeader) {
        this.userAgentHeader = userAgentHeader;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getHttpAcceptLanguage() {
        return httpAcceptLanguage;
    }

    public void setHttpAcceptLanguage(String httpAcceptLanguage) {
        this.httpAcceptLanguage = httpAcceptLanguage;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Browser [acceptHeader=" + acceptHeader + ", userAgentHeader=" + userAgentHeader + ", deviceType=" + deviceType + "]";
    }
}
