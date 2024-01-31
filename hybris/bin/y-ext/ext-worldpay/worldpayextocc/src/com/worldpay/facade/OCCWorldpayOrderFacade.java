package com.worldpay.facade;

import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;

import java.util.Map;

public interface OCCWorldpayOrderFacade extends OrderFacade {

    /**
     * Handles the response from a redirect authorise call
     * @param result
     * @return OrderData
     * @throws WorldpayException
     */
    OrderData handleHopResponseWithoutPaymentStatus(final RedirectAuthoriseResult result) throws WorldpayException;

    /**
     * Handles the response from a redirect authorise call
     * @param result
     * @return OrderData
     * @throws WorldpayException
     */
    OrderData handleHopResponseWithPaymentStatus(final RedirectAuthoriseResult result) throws WorldpayException;

    /**
     * Get RedirectAuthoriseResult from requestParameterMap
     * @param requestParameterMap
     * @return RedirectAuthoriseResult
     */
    RedirectAuthoriseResult getRedirectAuthoriseResult(final Map<String, String> requestParameterMap);

    /**
     * Check is the order code is valid
     * @param orderCode
     * @return boolean
     */
    boolean isValidEncryptedOrderCode(final String orderCode);

    /**
     * Find the order from the order code into the response and user id
     * @param orderCode the order code
     * @param userId the user id
     * @return OrderData
     */
    OrderData findOrderByCodeAndUserId(final String orderCode, final String userId);
}
