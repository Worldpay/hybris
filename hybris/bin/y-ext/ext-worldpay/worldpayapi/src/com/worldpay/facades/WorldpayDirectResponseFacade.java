package com.worldpay.facades;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;

import java.util.Map;

/**
 * Facade to manage the redirection of the direct order response
 */
public interface WorldpayDirectResponseFacade {
    /**
     * Returns the data for 3DSecure redirection page for legacy flow based on {@code directResponseData}
     *
     * @param directResponseData - the converted world pay response
     * @return The params for the model
     * @throws WorldpayConfigurationException - when something goes wrong
     */
    Map<String, String> retrieveAttributesForLegacy3dSecure(DirectResponseData directResponseData) throws WorldpayConfigurationException;

    /**
     * Returns whether the response is in cancelled state or not
     *
     * @param directResponseData - the converted world pay response
     * @return Boolean - the result of the operation
     */
    Boolean isCancelled(DirectResponseData directResponseData);

    /**
     * Returns whether the response is in authorised state or not
     *
     * @param directResponseData - the converted world pay response
     * @return Boolean - the result of the operation
     */
    Boolean isAuthorised(DirectResponseData directResponseData);

    /**
     * Returns whether the response is a 3DSecure legacy flow or not
     *
     * @param directResponseData - the converted world pay response
     * @return Boolean - the result of the operation
     */
    Boolean is3DSecureLegacyFlow(DirectResponseData directResponseData);

    /**
     * Returns whether the response is a 3DSecure flex flow or not
     *
     * @param directResponseData - the converted world pay response
     * @return Boolean - the result of the operation
     */
    Boolean is3DSecureFlexFlow(DirectResponseData directResponseData);

    /**
     * Returns the data for 3DSecure flex redirection page for legacy flow based on {@code directResponseData}
     *
     * @param directResponseData - the converted world pay response
     * @return The params for the model
     * @throws WorldpayConfigurationException - when something goes wrong
     */
    Map<String, String> retrieveAttributesForFlex3dSecure(DirectResponseData directResponseData) throws WorldpayConfigurationException;
}
