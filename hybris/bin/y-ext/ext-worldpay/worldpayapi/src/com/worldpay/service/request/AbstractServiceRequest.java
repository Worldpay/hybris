package com.worldpay.service.request;

import com.worldpay.service.model.MerchantInfo;

/**
 * Template implementation of a {@link ServiceRequest} providing {@link MerchantInfo} and order code
 */
public class AbstractServiceRequest implements ServiceRequest {

    private MerchantInfo merchantInfo;
    private String orderCode;
    private String cookie;

    /**
     * Default constructor that takes the full list of fields
     *
     * @param merchantInfo
     * @param orderCode
     */
    protected AbstractServiceRequest(MerchantInfo merchantInfo, String orderCode) {
        this.merchantInfo = merchantInfo;
        this.orderCode = orderCode;
    }

    protected static void checkParameters(String requestType, final Object... params) {
        for (final Object param : params) {
            if (param == null) {
                throw new IllegalArgumentException("Required parameter to create " + requestType + " cannot be null");
            }
        }
    }

    @Override
    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    @Override
    public void setMerchantInfo(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    @Override
    public String getOrderCode() {
        return orderCode;
    }

    @Override
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @Override
    public String getCookie() {
        return cookie;
    }

    @Override
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
