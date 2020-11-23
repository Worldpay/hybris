package com.worldpay.facades.payment.hosted;

import java.util.Map;

/**
 * Facade to handle validation of the parameters in the URL after HOP redirection from Worldpay
 */
public interface WorldpayAfterRedirectValidationFacade {

    /**
     * Checks that the response is authentic and sent from Worldpay
     *
     * @param worldpayResponse
     * @return
     */
    boolean validateRedirectResponse(Map<String, String> worldpayResponse);
}
