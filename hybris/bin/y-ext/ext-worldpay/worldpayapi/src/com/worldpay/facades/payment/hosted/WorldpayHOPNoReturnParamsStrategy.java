package com.worldpay.facades.payment.hosted;

import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;

/**
 * Strategy to place the order if there is no url params in the return url from HOP
 */
public interface WorldpayHOPNoReturnParamsStrategy {
    /**
     * Sets the redirect with the values needed to place correctly the order
     *
     * @return String representing the Authorisation status
     */
    RedirectAuthoriseResult authoriseCart();
}
