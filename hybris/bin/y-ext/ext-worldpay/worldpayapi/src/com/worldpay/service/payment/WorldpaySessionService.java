package com.worldpay.service.payment;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;

/**
 * Service to handle the data that should be stored and retrieved from the session to complete the direct order authorisation
 */
public interface WorldpaySessionService {
    /**
     * Stores in session the necessary values to complete a 3dSecure transaction (either in Flex or Legacy flow)
     *
     * @param authoriseServiceResponse
     * @param worldpayAdditionalInfoData
     */
    void setSessionAttributesFor3DSecure(DirectAuthoriseServiceResponse authoriseServiceResponse, WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Removes the ThreeDSecureCookie stored in the session, and returns it
     *
     * @return
     */
    String getAndRemoveThreeDSecureCookie();

    /**
     * Removes the AddidionalDataSessionId stored in the session, and returns it
     *
     * @return
     */
    String getAndRemoveAdditionalDataSessionId();

    /**
     * Returns the window size stored in the current session
     *
     * @return
     */
    ChallengeWindowSizeEnum getWindowSizeChallengeFromSession();
}
