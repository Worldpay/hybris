package com.worldpay.service;

import com.worldpay.exception.WorldpayConfigurationException;

/**
 * Interface defining the WorldpayUrlService. Implementing service needs to be able to provide fully qualified urls for
 * the success, pending, failure and cancel responses from redirect payment
 */
public interface WorldpayUrlService {

    /**
     * Get the fully qualified success url to provide to third party payment provider
     *
     * @return Fully qualified success url
     */
    String getFullSuccessURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified pending url to provide to third party payment provider
     *
     * @return Fully qualified pending url
     */
    String getFullPendingURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified failure url to provide to third party payment provider
     *
     * @return Fully qualified failure url
     */
    String getFullFailureURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified cancel url to provide to third party payment provider
     *
     * @return Fully qualified cancel url
     */
    String getFullCancelURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified error url to provide to third party payment provider
     *
     * @return Fully qualified error url
     */
    String getFullErrorURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified 3D secure termURL to provide to card issuer
     *
     * @return Fully qualified 3D secure termURL
     */
    String getFullThreeDSecureTermURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified 3D secure termURL for Quote acceptance to provide to card issuer
     *
     * @return Fully qualified 3D secure termURL for Quote acceptance
     */
    String getFullThreeDSecureQuoteTermURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified secure terms url
     *
     * @return The terms url
     * @throws WorldpayConfigurationException
     */
    String getFullTermsUrl() throws WorldpayConfigurationException;

    /**
     * Get the base site url for the current site
     *
     * @return The base site url
     * @throws WorldpayConfigurationException
     */
    String getBaseWebsiteUrlForSite() throws WorldpayConfigurationException;

    /**
     * Get the Klarna confirmation url to confirm the order
     *
     * @return The Klarna confirmation url
     * @throws WorldpayConfigurationException
     */
    String getKlarnaConfirmationURL() throws WorldpayConfigurationException;

    /**
     * Get the website url without path for the current base site
     *
     * @return The base site url
     */
    String getWebsiteUrlForCurrentSite();

    /**
     * Get the fully qualified 3D secure url for Flex flow to provide to card issuer
     *
     * @return
     */
    String getFullThreeDSecureFlexFlowReturnUrl() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified Autosubmit url for Flex flow to provide to card issuer
     *
     * @return
     */
    String getFullThreeDSecureFlexAutosubmitUrl() throws WorldpayConfigurationException;
}
