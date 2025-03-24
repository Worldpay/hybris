package com.worldpay.service.request;

import com.worldpay.data.MerchantInfo;

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
    protected AbstractServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        this.merchantInfo = merchantInfo;
        this.orderCode = orderCode;
    }

    @Override
    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    @Override
    public void setMerchantInfo(final MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    @Override
    public String getOrderCode() {
        return orderCode;
    }

    @Override
    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    @Override
    public String getCookie() {
        return cookie;
    }

    @Override
    public void setCookie(final String cookie) {
        this.cookie = cookie;
    }

    protected static void checkParameters(final String requestType, final Object... params) {
        for (final Object param : params) {
            if (param == null) {
                throw new IllegalArgumentException("Required parameter to create " + requestType + " cannot be null");
            }
        }
    }
}
