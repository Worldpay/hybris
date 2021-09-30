package com.worldpay.service.mac;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.data.RedirectReference;

/**
 * Class to validate of a mac code.
 * <p/>
 * <p>Ensures that the redirect url being provided by Worldpay and that is returned in the {@link RedirectReference} has not been tampered with after being
 * sent out by Worldpay and before being received. The mac is a digest of the other information in the url and so can be validated by using the same
 * routine to generate a mac code and then validating against the one supplied by Worldpay</p>
 */
public interface MacValidator {

    /**
     * Validate the mac code returned in the response url is valid for the rest of the details that have been returned.
     * <p>
     * There are 2 algorithms for Mac Validation:
     * <p>
     * HMAC256 and MD5
     *
     * @param orderKey      OrderKey parameter returned in url
     * @param worldpayMac   Worldpay Mac code returned in url
     * @param paymentAmount Payment Amount returned in url
     * @param currency      Payment Currency returned in url
     * @param status        Payment Status returned in url
     * @param macSecret     Secret seed that has been agreed between the merchant and Worldpay
     * @return true or false depending whether the mac code is valid or not
     * @throws WorldpayMacValidationException if there has been an issue trying to validate the mac code
     */
    boolean validateResponse(final String orderKey, final String worldpayMac, final String paymentAmount, final String currency, final AuthorisedStatus status, final String macSecret) throws WorldpayMacValidationException;
}
