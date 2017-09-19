package com.worldpay.service.request;


import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.model.MerchantInfo;

/**
 * Interface representation of a Service Request that will be used to make the calls through to Worldpay.
 * <p/>
 * <p>Actual implementation must provide at least the {@link WorldpayConfig}, {@link MerchantInfo} and Order code that is the minimum requirements for making a call to
 * Worldpay</p>
 */
public interface ServiceRequest {

    WorldpayConfig getWorldpayConfig();

    void setWorldpayConfig(WorldpayConfig worldpayConfig);

    MerchantInfo getMerchantInfo();

    void setMerchantInfo(MerchantInfo merchantInfo);

    String getOrderCode();

    void setOrderCode(String orderCode);

    String getCookie();

    void setCookie(String cookie);
}
