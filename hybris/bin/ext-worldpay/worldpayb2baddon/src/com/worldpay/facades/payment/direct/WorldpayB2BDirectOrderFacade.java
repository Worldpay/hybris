package com.worldpay.facades.payment.direct;

import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.order.InvalidCartException;

/**
 * Interface that exposes the authorise operations that enables the Client Side Encryption with Worldpay
 */
public interface WorldpayB2BDirectOrderFacade extends WorldpayDirectOrderFacade {

    /**
     * Performs authorisation of a transaction after it has been handled by 3dSecure issuer.
     *
     * @param orderCode  The hybris order code
     * @param paResponse                 The response from the 3dSecure issuer.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     */
    DirectResponseData authorise3DSecureOnOrder(final String orderCode, final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a direct authorisation using a saved order with Worldpay.
     *
     * @param orderCode The code of the order to authorise.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     */
    DirectResponseData authoriseRecurringPayment(final String orderCode, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

}
