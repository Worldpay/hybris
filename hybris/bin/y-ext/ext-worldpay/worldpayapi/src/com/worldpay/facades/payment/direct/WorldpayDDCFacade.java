package com.worldpay.facades.payment.direct;

/**
 * Handles the information related to the DDC information
 */
public interface WorldpayDDCFacade {

    /**
     * Creates a json web token for DDC
     *
     * @return String - json from dcc
     */
    String createJsonWebTokenForDDC();

    /**
     * Retrieves the origin event domain to verify the validity of the message received by DCC Iframe
     *
     * @return the origin event domain
     */
    String getEventOriginDomainForDDC();
}
