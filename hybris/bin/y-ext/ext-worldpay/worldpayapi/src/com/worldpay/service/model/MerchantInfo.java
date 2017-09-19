package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of the merchant information
 */
public class MerchantInfo implements Serializable {

    private String merchantCode;
    private String merchantPassword;
    private boolean usingMacValidation;
    private String macSecret;

    /**
     * Constructor with full list of fields
     *
     * @param merchantCode
     * @param merchantPassword
     * @param usingMacValidation
     * @param macSecret
     */
    public MerchantInfo(String merchantCode, String merchantPassword, boolean usingMacValidation, String macSecret) {
        this.merchantCode = merchantCode;
        this.merchantPassword = merchantPassword;
        this.usingMacValidation = usingMacValidation;
        this.macSecret = macSecret;
    }

    /**
     * Constructor with just merchant code and password. Assumes not using mac validation
     *
     * @param merchantCode
     * @param merchantPassword
     */
    public MerchantInfo(String merchantCode, String merchantPassword) {
        this.merchantCode = merchantCode;
        this.merchantPassword = merchantPassword;
        this.usingMacValidation = false;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
    }

    public boolean isUsingMacValidation() {
        return usingMacValidation;
    }

    public void setUsingMacValidation(boolean usingMacValidation) {
        this.usingMacValidation = usingMacValidation;
    }

    public String getMacSecret() {
        return macSecret;
    }

    public void setMacSecret(String macSecret) {
        this.macSecret = macSecret;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "MerchantInfo [merchantCode=" + merchantCode + ", merchantPassword=" + merchantPassword + "]";
    }
}
