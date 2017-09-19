package com.worldpay.service;

import com.worldpay.config.Environment;
import com.worldpay.config.WorldpayConfig;

public class WorldpayTestConfigHelper {

    public static WorldpayConfig getWorldpayTestConfig() {
        return new WorldpayConfig("1.4", new Environment("https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp", Environment.EnvironmentRole.TEST));
    }

}
